package com.think.core.annotations.bean;


import java.lang.annotation.*;

/**
 * 忽略注解，用于 映射DB实际，忽略字段
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkIgnore {
}
