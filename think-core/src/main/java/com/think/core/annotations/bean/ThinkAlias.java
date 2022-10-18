package com.think.core.annotations.bean;


import java.lang.annotation.*;

/**
 * 用于个性化VO的 自定义 别名 注解
 *  如 数据库中 的 列名 是 name,
 *  但是我们在 VO 中定义了   private String userName ;
 *  加上此 注解，@ThinkAlias(sourceColumnName="name") ，
 *   userName的值会 被自动赋 name列的值
 * @author JasonMao
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkAlias {
    /*>>>>>>>>>>>>>>>>>>>>>>-映射的数据库表列名-<<<<<<<<<<<<<<<<<<<<<<<*/
    String sourceColumnName() default "" ;
}
