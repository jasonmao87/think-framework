package com.think.data.dao;

import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean.SimpleRefEntity;

public interface ThinkDaoFactory {
    <T extends SimplePrimaryEntity> ThinkDao<T> getDao(Class<T> target);

    <T extends SimpleRefEntity>ThinkSplitRefDao<T> getSplitRefDao(Class<T> target);

    <T extends SimplePrimaryEntity> ThinkSplitPrimaryDao<T> getSplitDao(Class<T> target);
}
