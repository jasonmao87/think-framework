package com.think.web;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/29 20:09
 * @description :  R 的 拦截器 ，处理注入
 */
public interface IThinkWebResultInterceptor {

    void afterInit(R r);
}
