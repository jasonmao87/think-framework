package com.think.core.annotations.bean;


import com.think.core.annotations.Remark;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkColumn {

    @Remark("字段长度")
    int length() default 24;

    @Remark("是否允许空")
    boolean nullable() default false;

    /**
     *  默认值  用string 类型数值 ，
     *      如果是 字符串类型  那么清输入对应的 字符串
     *      如果是 数值类型， 那么清输入对应的 数字 string类型
     * @return
     */
    @Remark("默认值")
    String defaultValue() default "";


    @Remark(value = "敏感数据",description = "脱敏持久化")
    boolean sensitive() default false;

    @Remark(value = "允许修改",description = "设置false，将不允许修改")
    boolean editAble() default true;


    /**
     * 快速检索支持
     * @return
     */
    @Remark(value = "快速排序支持需求（不一定生效）",description = "不保证生效，thinkFramework限制了部分支持，考虑实际场景，在不明显增加数据库压力的情况下，才可能生效。针对排序或者筛选需要，在不适合建立索引的字段理，增加快速检索支持。")
    boolean fastMatch() default false ;

    @Remark("申明Date类类型不要设置默认时间")
    boolean noSetDateDefaultValue() default false;

    @Remark("当数据类型为string时候有效")
    boolean usingText() default false;

}
