package com.think.core.executor;

import com.think.core.security.ThinkToken;

/**
 * 异步任务 token 获取的filter ！建议在项目中自定义实现并且注入
 */
@Deprecated
public interface IAsyncTokenFilter {

    ThinkToken getToken();

//    void beforeExecute();
}
