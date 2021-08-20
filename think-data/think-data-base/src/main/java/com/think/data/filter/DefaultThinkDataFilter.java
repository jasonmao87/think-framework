package com.think.data.filter;

import com.think.common.util.DateUtil;
import com.think.core.bean._Entity;
import com.think.common.data.mysql.ThinkUpdateMapper;

public class DefaultThinkDataFilter implements ThinkDataFilter {
    @Override
    public <T extends _Entity> void beforeExecuteInsert(T t) {
        t.setCreateTime(DateUtil.now());
        t.setLastUpdateTime(DateUtil.now());
    }

    @Override
    public <T extends _Entity> void beforeExecuteUpdateEntity(T t) {
        t.setLastUpdateTime(DateUtil.now());
    }

    @Override
    public <T extends _Entity> void beforeExecuteUpdateMapper(ThinkUpdateMapper<T> updaterMapper) {
        updaterMapper.updateValue("lastUpdateTime" ,DateUtil.now());
    }
}
