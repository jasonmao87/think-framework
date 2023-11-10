package com.think.core.annotations.bean;


import com.think.core.annotations.Remark;
import com.think.core.enums.DbType;
import com.think.core.enums.TableBusinessModeSplitStateEnum;

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

    /**
     * 业务模式划分 功能是否开启
     * @return
     */
    @Remark(value = "启用业务模式划分()",description = "同表不同产品的数据划分支持，如A产品，B产品，使用相同表，可使用该字段进行数据的隔离划分")
    TableBusinessModeSplitStateEnum businessModeSplitAble() default TableBusinessModeSplitStateEnum.DEFAULT;


    @Remark(value = "数据源类型，默认值可以在 启动类中指定（DbType.）" )
    DbType dbType() default DbType.DEFAULT;
//
//    @Remark("启用版本管理")
//    boolean enableVersion() default false;
}
