package com.think.core.annotations.bean;


import com.think.core.annotations.Remark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表映射索引，将会 构建数据库表
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkTable {


    public static final String DEFAULT_DS_ID = "DEFAULT";
    /**
     * 映射到数据库的表明
     * @return
     */
    @Remark("映射到MYSQL数据库的表名")
    String value() ;

    @Remark("表备注")
    String comment() default "";

    @Remark("是否允许缓存,如果允许缓存，可能查询对象会先走缓存（不一定需要实现）")
    boolean cacheAble() default false;

    @Remark("启用数据分区")
    boolean partitionAble() default false;

    @Deprecated
    String dsId() default DEFAULT_DS_ID;

    @Remark("主键自动递增")
    boolean autoIncPK() default false ;

    @Remark(value = "启用年度数据切分",description = "（在数据分区得基础上，也可以是普通表）额外在支持按年分表")
    boolean yearSplit() default false;
//
//    @Remark("启用版本管理")
//    boolean enableVersion() default false;
}
