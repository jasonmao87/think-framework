package com.think.core.annotations.bean;

import com.think.core.annotations.Remark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 暂时不做支持
 */
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkMongoPartitionAble {

    @Remark(value = "启用按年分表",
            description = "（包含分区支持的基础再）支持按年分表，" +
                    "如果是分区表：可能 collection_a_2020," +
                    "如果不是分区表，那么可以是 ： collection_2020 ")
    boolean yearSplit() default false;

    @Remark(value = "启用后缀分区"
            ,description = "在基础collection的基础上，支持后缀，如 collection_A")
    boolean partitionAble() default false;

}
