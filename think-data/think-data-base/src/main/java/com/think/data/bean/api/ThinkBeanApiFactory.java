package com.think.data.bean.api;

import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean.SimpleRefEntity;

public interface ThinkBeanApiFactory {

    ThinkApiUpdateMethodFilter getThinkApiUpdateMethodFilter();

    void setThinkApiUpdateMethodFilter(ThinkApiUpdateMethodFilter thinkApiUpdateMethodFilter);

    <T extends SimplePrimaryEntity> ThinkSplitBeanApi<T> getSplitApi(Class<T> targetClass);

    <T extends SimplePrimaryEntity> ThinkBeanApi<T> getApi(Class<T> targetClass);

    <T extends SimpleRefEntity> ThinkSplitRefBeanApi getRefApi(Class<T> targetClass);
}
