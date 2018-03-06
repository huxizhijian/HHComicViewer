package org.huxizhijian.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Source annotation, mark source class then use APT to add SourceRouter.
 *
 * @author huxizhijian
 * @date 2018/3/5.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface SourceGenerator {
    /**
     * @return name of source
     */
    String value();
}
