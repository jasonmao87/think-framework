package com.think.core.annotations.bean;


import com.think.core.annotations.Remark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkIndex {

    @Remark("唯一索引")
    boolean unique() default false;

    @Remark("索引字段数组长度 1 - N ， 不建议操作3")
    String[] keys() ;
}
