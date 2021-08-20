package com.think.data.dao;

import com.think.common.result.ThinkResult;
import com.think.core.bean.SimplePrimaryEntity;

public interface TCache {

    /**
     * 获取缓存的对象
     * @param id
     * @param targetClass
     * @return
     */
    Object get(Long id, Class targetClass);

    /**
     * 缓存 或 更新 缓存
     * @param t
     * @param <T>
     * @return
     */
    <T extends SimplePrimaryEntity> ThinkResult<T> cache(T t);

    /**
     * 清空所有缓存
     */
    void clearAll();

    /**
     * 根据id移除缓存
     * @param id
     * @param targetClass
     */
    void remove(long id, Class targetClass);



    
}
