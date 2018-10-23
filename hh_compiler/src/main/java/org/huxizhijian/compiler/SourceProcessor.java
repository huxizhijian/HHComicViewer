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
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final ClassName THIS_TYPE = ClassName.get(PACKAGE_NAME, CLS_NAME);

    /**
     * 成员变量名
     */
    private static final String SOURCE_MAP_FIELD_NAME = "mSourceMap";
    private static final String SOURCE_KEY_LIST_FIELD_NAME = "mSourceKeyList";
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

        if (sourceInterfaceElement != null) {
            if (sourceInterfaceElement.size() > 1) {
                throw new IllegalStateException("Only one interface could has @SourceInterface annotation!");
            } else {
                for (Element element : sourceInterfaceElement) {
                    TypeName clazz = ClassName.get(element.asType());
                    typeBuilder.addField(sourceArraySpec(clazz));
                    typeBuilder.addMethod(getSourceSpec(sourceImplElement, clazz));
                }
            }
        }

        typeBuilder
                .addField(staticInstantSpec())
                .addField(sourceKeyListSpec())
                .addMethod(getSourceKeyListSpec(sourceImplElement))
                .addMethod(getInstantSpec())
                .addMethod(constructorPrivate())
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
     * 包含source实例的SparseArray成员变量
     *
     * @param sourceName SourceInterface注解的类,仅能有一个
     * @return fieldSpec
     */
    private FieldSpec sourceArraySpec(TypeName sourceName) {
        TypeName arrayOfSource = ParameterizedTypeName.get(ClassName.get(HashMap.class),
                ClassName.get(String.class), sourceName);
        return FieldSpec.builder(arrayOfSource, SOURCE_MAP_FIELD_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", arrayOfSource)
                .build();
    }

    /**
     * sourceKey的列表
     *
     * @return fieldSpec
     */
    private FieldSpec sourceKeyListSpec() {
        TypeName arrayOfSourceKey = ParameterizedTypeName.get(ClassName.get(ArrayList.class),
                ClassName.get(String.class));
        return FieldSpec.builder(arrayOfSourceKey, SOURCE_KEY_LIST_FIELD_NAME)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .initializer("new $T()", arrayOfSourceKey)
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
     * 返回sourceKeyList的方法
     *
     * @return methodSpec
     */
    private MethodSpec getSourceKeyListSpec(Set<? extends Element> sourceImplElement) {
        TypeName arrayOfSourceKey = ParameterizedTypeName.get(ClassName.get(ArrayList.class),
                ClassName.get(String.class));
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getSourceKeyList")
                .addModifiers(Modifier.PUBLIC)
                .returns(arrayOfSourceKey);
        if (sourceImplElement != null) {
            for (Element element : sourceImplElement) {
                SourceImpl source = element.getAnnotation(SourceImpl.class);
                builder.addStatement("$L.add($S)", SOURCE_KEY_LIST_FIELD_NAME, source.id());
            }
        }
        return builder.addStatement("return $L", SOURCE_KEY_LIST_FIELD_NAME)
                .build();
    }

    /**
     * 获取source实例方法
     *
     * @param sourceImplElement 被{@link SourceImpl}注解修饰的类
     * @return methodSpec
     */
    private MethodSpec getSourceSpec(Set<? extends Element> sourceImplElement, TypeName sourceName) {
        String paraName = "sourceKey";
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getSource")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(String.class), paraName)
                .addException(IOException.class)
                .returns(sourceName)
                .beginControlFlow("if (!$L.containsKey($L))", SOURCE_MAP_FIELD_NAME, paraName)
                .beginControlFlow("switch ($L)", paraName);
        if (sourceImplElement != null) {
            for (Element element : sourceImplElement) {
                SourceImpl source = element.getAnnotation(SourceImpl.class);
                builder.addCode("case $S:\n", source.id())
                        .addStatement("  $L.put($L, new $T())", SOURCE_MAP_FIELD_NAME, paraName, ClassName.get(element.asType()))
                        .addStatement("  break");
            }
            builder.addCode("default:\n")
                    .addStatement("  return null")
                    .endControlFlow()
                    .endControlFlow();
        }
        return builder
                .addStatement("return $L.get($L)", SOURCE_MAP_FIELD_NAME, paraName)
                .build();
    }

    /**
     * 构建私有构造方法, 具有一些初始化方法
     *
     * @return method spec
     */
    private MethodSpec constructorPrivate() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();
    }
}
