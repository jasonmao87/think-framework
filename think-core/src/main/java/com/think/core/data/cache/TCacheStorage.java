package com.think.core.data.cache;

import com.think.core.annotations.Remark;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/2 15:20
 * @description : TODO
 */
public interface TCacheStorage<T> {

    @Remark("缓存内容")
    void cache(String key , T data);

    @Remark("读取缓存")
    T getCache(String key );

    @Remark("移除缓存")
    void remove(String key);

    @Remark("清空缓存")
    void clearAll();


}
