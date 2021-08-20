package com.think.data.provider;

import com.think.core.bean.SimplePrimaryEntity;

/**
 * @Date :2021/8/18
 * @Name :ThinkSplitDaoImpl
 * @Description : 请输入
 */
public class ThinkSplitDaoImpl<T extends SimplePrimaryEntity> extends ThinkSplitDaoProvider<T>{

    public ThinkSplitDaoImpl(Class<T> targetClass) {
        super(targetClass);
    }
}
