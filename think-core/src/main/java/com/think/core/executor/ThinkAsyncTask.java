package com.think.core.executor;

import com.think.core.annotations.Remark;

/**
 * @Date :2021/3/22
 * @Name :ThinkAsyncTask
 * @Description : think异步任务执行接口，用于实现需要异步执行的功能性代码
 */
public interface ThinkAsyncTask {
    @Remark("需要异步执行的实现方法")
    void execute();
}
