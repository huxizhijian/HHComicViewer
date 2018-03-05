package org.huxizhijian.hhcomic.datalayer.comicparser.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source类注解
 * @author huxizhijian
 * @date 2018/3/5.
 */
// 注解的作用目标于接口、类、枚举、注解
@Target(ElementType.TYPE)
// 注解的保留位置,SOURCE表示注解仅存在源码中,编译成class文件后不包含
@Retention(RetentionPolicy.SOURCE)
public @interface SourceGenerator {
    String name();
}
