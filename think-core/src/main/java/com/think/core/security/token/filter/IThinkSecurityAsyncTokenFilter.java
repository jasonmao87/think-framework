package com.think.core.security.token.filter;

import com.think.core.annotations.Remark;
import com.think.core.security.token.ThinkSecurityToken;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/24 22:44
 * @description :  异步 或者后端任务  获取 token的接口
 */
public interface IThinkSecurityAsyncTokenFilter {

    /**
     * 异步线程 获取token
     * @return
     */
    @Remark("异步线程获取WEB token")
    ThinkSecurityToken getAsyncTokenFromWebRequestInfo();


    /**
     * filter 的优先级 ！
     * @return
     */
    @Remark(value = "优先级，order值越小，越先执行，默认为0",description = "存在多个filter 时候， 当得到ThinkSecurityToken 就会退出 ")
    int order();


}
