package com.think.core.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仅用于注释
 */
@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE,ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER, })
@Retention(RetentionPolicy.RUNTIME)
public @interface  Remark {

    String value();

    String description() default "";
}
