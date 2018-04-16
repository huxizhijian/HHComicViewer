/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.huxizhijian.annotations.SourceImpl;
import org.huxizhijian.annotations.SourceInterface;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 注解处理器
 * A processor for generate java class to auto add source
 *
 * @author huxizhijian
 * @date 2018/3/6
 */
@SuppressWarnings("unused")
@AutoService(Processor.class)
public class SourceProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Types mTypeUtils;
    private Messager mMessager;
    private Elements mElementUtils;

    /**
     * 生成类的包名和类名
     */
    private static final String PACKAGE_NAME = "org.huxizhijian.generate";
    private static final String CLS_NAME = "SourceRouterApp";
    private static final String ENUM_CLS_NAME = "SourceType";
    private static final ClassName THIS_TYPE = ClassName.get(PACKAGE_NAME, CLS_NAME);
    private static final ClassName ENUM_TYPE = ClassName.get(PACKAGE_NAME, CLS_NAME + "." + ENUM_CLS_NAME);

    /**
     * 成员变量名
     */
    private static final String SOURCE_MAP_FIELD_NAME = "mSourceMap";
    private static final String SOURCE_NAME_MAP_FIELD_NAME = "mSourceNameMap";
    private static final String APP_INSTANCE = "sSourceRouterApp";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> types = new LinkedHashSet<>();
        final Set<Class<? extends Annotation>> supportAnnotations = getSupportedAnnotations();
        for (Class<? extends Annotation> annotation : supportAnnotations) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        final Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(SourceImpl.class);
        annotations.add(SourceInterface.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 取出两者注解的类的Element
        Set<? extends Element> sourceImplElement = roundEnv.getElementsAnnotatedWith(SourceImpl.class);
        Set<? extends Element> sourceInterfaceElement = roundEnv.getElementsAnnotatedWith(SourceInterface.class);

        // 生成的java类名,修饰
        TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(CLS_NAME)
                .addModifiers(Modifier.PUBLIC);

        // 生成的枚举类
        TypeSpec.Builder typeEnumBuilder = TypeSpec.enumBuilder(ENUM_CLS_NAME)
                .addModifiers(Modifier.PUBLIC);

        // 加入枚举常量
        if (sourceImplElement != null) {
            for (Element element : sourceImplElement) {
                String typeName = element.getSimpleName().toString().toUpperCase();
                typeEnumBuilder.addEnumConstant(typeName);
            }
            // JavaPoet里枚举内部类不能是空的
            if (sourceImplElement.size() != 0) {
                typeBuilder.addType(typeEnumBuilder.build());
            }
        }

        if (sourceInterfaceElement != null) {
            if (sourceInterfaceElement.size() > 1) {
                throw new IllegalStateException("Only one interface could has @SourceInterface annotation!");
            } else {
                for (Element element : sourceInterfaceElement) {
                    TypeName clazz = ClassName.get(element.asType());
                    typeBuilder.addField(sourceArraySpec(clazz));
                }
            }
        }

        typeBuilder
                .addField(sourceNameArraySpec())
                .addField(staticInstantSpec())
                .addMethod(getInstantSpec())
                .addMethod(constructorPrivate(sourceImplElement))
                .addMethod(getSourceNameSpec())
                .build();

        // 生成的java文件
        JavaFile javaFile = JavaFile.builder(PACKAGE_NAME, typeBuilder.build())
                .build();

        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 包含source名称的SparseArray成员变量
     *
     * @return field spec
     */
    private FieldSpec sourceNameArraySpec() {
        // 使用EnumMap获得更快的枚举索引速度, EnumMap与序数索引数组的速度相当
        TypeName arrayOfString = ParameterizedTypeName.get(ClassName.get(EnumMap.class),
                ENUM_TYPE, ClassName.get(String.class));
        return FieldSpec.builder(arrayOfString, SOURCE_NAME_MAP_FIELD_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T($L)", arrayOfString, ENUM_TYPE.simpleName() + ".class")
                .build();
    }

    /**
     * 包含source实例的SparseArray成员变量
     *
     * @param sourceName SourceInterface注解的类,仅能有一个
     * @return fieldSpec
     */
    private FieldSpec sourceArraySpec(TypeName sourceName) {
        TypeName arrayOfSource = ParameterizedTypeName.get(ClassName.get(EnumMap.class),
                ENUM_TYPE, ClassName.get(String.class));
        return FieldSpec.builder(arrayOfSource, SOURCE_MAP_FIELD_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T($L)", arrayOfSource, ENUM_TYPE.simpleName() + ".class")
                .build();
    }

    /**
     * 生成本类的静态成员变量, 用于单例模式
     *
     * @return field spec
     */
    private FieldSpec staticInstantSpec() {
        return FieldSpec.builder(THIS_TYPE, APP_INSTANCE)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE)
                .build();
    }

    /**
     * 单例模式获取实例方法
     *
     * @return method spec
     */
    private MethodSpec getInstantSpec() {
        return MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(THIS_TYPE)
                .beginControlFlow("if ($L == null)", APP_INSTANCE)
                .beginControlFlow("synchronized ($T.class)", THIS_TYPE)
                .beginControlFlow("if ($L == null)", APP_INSTANCE)
                .addStatement("$L = new $T()", APP_INSTANCE, THIS_TYPE)
                .endControlFlow()
                .endControlFlow()
                .endControlFlow()
                .addStatement("return $L", APP_INSTANCE)
                .build();
    }


    /**
     * 构建私有构造方法, 具有一些初始化方法
     *
     * @return method spec
     */
    private MethodSpec constructorPrivate(Set<? extends Element> sourceImplElement) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE);
        // 初始化mSourceNameArray
        if (sourceImplElement != null) {
            for (Element element : sourceImplElement) {
                SourceImpl source = element.getAnnotation(SourceImpl.class);
                builder.addStatement("$L.put($L,$S)", SOURCE_NAME_MAP_FIELD_NAME,
                        ENUM_CLS_NAME + "." + element.getSimpleName().toString().toUpperCase(), source.name());
            }
        }
        return builder.build();
    }

    /**
     * 获取source名称
     *
     * @return method spec
     */
    private MethodSpec getSourceNameSpec() {
        return MethodSpec.methodBuilder("getSourceName")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ENUM_TYPE, "sourceType")
                .returns(String.class)
                .addStatement("return $L.get(sourceType)", SOURCE_NAME_MAP_FIELD_NAME)
                .build();
    }
}
