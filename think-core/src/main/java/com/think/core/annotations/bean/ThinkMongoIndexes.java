package com.think.core.annotations.bean;


import com.think.core.annotations.Remark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 索引
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkMongoIndexes {
    @Remark(value = "【慎用】启动自动过期策略",description = "如果启用将会根据设置值自动过期，即删除数据")
    boolean expireAble() default false;

    @Remark(value = "数据过期天数",description = "如果启用了自动过期策略，那么此参数将会生效，将会根据数创建日期，自动给加上对应天数，表示着数据会在 N 天后，被删除 。")
    long expireAtDays() default 180L ;

    @Remark(value = "索引清单",description = "建立您需要的索引清单")
    ThinkMongoIndex[] indexes() ;
}
