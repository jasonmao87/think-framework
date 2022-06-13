package com.think.core.data.cache;

import com.think.core.annotations.Remark;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/2 15:20
 * @description : TODO
 */
public interface ThinkCacheStorage<T> {

    @Remark("缓存内容")
    boolean put(String key , T data);

    @Remark("读取缓存")
    T getCache(String key );

    @Remark("移除缓存")
    boolean remove(String key);

    @Remark("清空缓存")
    void clearAll();


    @Remark("检查并释放空间，即释放部分空间")
    void checkAndReleaseSpace();


    @Remark("缓存总数")
    int getCachedDataCount();

    @Remark("缓存读取命中数")
    long getCacheReadHitTimes();

    @Remark("缓存读取次数")
    long getCacheReadTimes();

    @Remark("缓存写入次数")
    long getCacheWriteTimes();

    @Remark("缓存容器名称")
    String getName();

    @Remark("最近一次触发自动释放空间的时间")
    long getLastCacheAutoReleaseTime();

    @Remark("返回容器Id")
    String storageId();

}
