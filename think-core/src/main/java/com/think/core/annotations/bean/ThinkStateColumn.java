package com.think.core.annotations.bean;

import com.think.core.annotations.Remark;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkStateColumn {

    static final String splitFlag = "_TF_FLG_";
    public static final String flowStateSuffix_CompleteTime = splitFlag +"completeTime";
    public static final String flowStateSuffix_StartTime = splitFlag +"startTime";
    public static final String flowStateSuffix_CancelTime = splitFlag +"cancelTime";
    public static final String flowStateSuffix_ResultMessage = splitFlag +"resultMessage";
    public static final String flowStateSuffix_TryCount = splitFlag +"tryCount";
    public static final String flowStateSuffix_StateValue = splitFlag +"stateValue";

//    public static final boolean allow(String k){}

    @Remark(value = "意义备注描述")
    String comment() default "";


}
