package com.think.core.annotations.bean;


import com.think.core.annotations.Remark;
import io.swagger.annotations.ApiModelProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解暂时不暴露
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TScheduleCron {

//    @Remark(value = "匹配的年",description = "*表示任意，也可以年份用逗号分割")
//    String year() ;
    @Remark(value = "thinkCron表达式" ,description = "" +
            "格式：$monthCron $dateCron $hourCron $minuteCron $second $maxTrigger \n" +
            "表达式规则\n" +
            "monthCron  范围：[1-12] Exp : * / 1-12 / 1,2,3 /;" +
            "dateCron   范围：[1-31] Exp : * / 1-31 / 1,3,9 /;  " +
            "hourCron   范围：[0-23] Exp : * / 0-24 / 1,3,9 /; " +
            "minuteCorn 范围：[0-59] Exp : * / 0-24 / 1,3,9 /;" +
            "second     范围：[0-59] Exp : 1    " +
            "maxTrigger 范围：[-1-N] Exp : / 1 / 12 ; "
    )
    String cron();

    @Remark(value = "匹配的月",description = "*表示任意，也可以1-12用逗号分割")
    String month() ;

    @Remark(value = "匹配的日",description = "*表示任意，也可以1-31用逗号分割，超过当月最大值，自动调整为 最后一天")
    String date() ;

    @Remark(value = "匹配的小时",description = "*表示任意，也可以1-23用逗号分割")
    String hour() ;

    @Remark(value = "匹配的分钟",description = "*表示任意，也可以0-59用逗号分割")
    String minute();

    @Remark(value = "匹配的秒",description = "【0-59】必须指定，我们不允许每秒执行1次的任务")
    int second() default 1 ;

    @Remark("最发触发次数")
    int maxTriggerCount();
}
