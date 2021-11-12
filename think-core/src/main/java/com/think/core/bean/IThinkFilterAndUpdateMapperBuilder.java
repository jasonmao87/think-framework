package com.think.core.bean;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.core.annotations.Remark;

import java.io.Serializable;

public interface IThinkFilterAndUpdateMapperBuilder<T extends _Entity> extends Serializable {


    @Remark("构建一个空的filter")
    ThinkSqlFilter<T> buildEmptyFilter(int limit)  ;

    @Remark("构建一个空的filter")
    ThinkSqlFilter<T> buildEmptyFilter(int limit, Class<T> tClass);

    @Remark("构建一个空的updateMapper")
    ThinkUpdateMapper<T> buildEmptyUpdateMapper();

    @Remark("构建一个空的updateMapper")
    ThinkUpdateMapper<T> buildEmptyUpdateMapper(Class<T> tClass);

    /** @deprecated */
    @Deprecated
    @Remark(
            value = " 构建包含当前id的 updateMapper ，无法在设置 filter",
            description = "如果id不存在，返回空的updateMapper"
    )
    ThinkUpdateMapper<T> buildUpdateMapperWithCurrentId();



}
