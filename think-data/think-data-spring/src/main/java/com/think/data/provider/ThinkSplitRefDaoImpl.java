package com.think.data.provider;

import com.think.core.bean.SimpleRefEntity;

/**
 * @Date :2021/8/18
 * @Name :ThinkSplitRefDaoImpl
 * @Description : 请输入
 */
public class ThinkSplitRefDaoImpl<T extends SimpleRefEntity> extends ThinkSplitRefDaoProvider<T>{

    public ThinkSplitRefDaoImpl(Class<T> targetClass) {
        super(targetClass);
    }
}
