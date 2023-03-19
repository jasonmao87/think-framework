package com.think.common.data.mysql;

import com.think.core.annotations.Remark;

import java.util.Map;

public interface IThinkResultFilter {
    /**
     * 在返回对象前对 对象做处理
     */
    @Remark("返回前可以针对对象做个性化的处理")
    public void doFilter(Map<String,Object> resultDataMap);
}
