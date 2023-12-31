package com.think.data.filter;

import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.core.bean._Entity;

public interface ThinkDataFilter {

    <T extends _Entity> void beforeExecuteInsert(T t);

    <T extends _Entity> void beforeExecuteUpdateEntity(T t);

    <T extends _Entity> void beforeExecuteUpdateMapper(ThinkUpdateMapper<T> t);
}
