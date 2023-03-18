package com.think.core.data.cache;

import com.think.core.annotations.Remark;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/2 15:13
 * @description : TODO
 */
public class ThinkCacheManager {

    private final List<ThinkCacheStorage> storageList;
    /**
     * 单例静态构建类
     */
    private static class singletonHolder {
        private static ThinkCacheManager instance = new ThinkCacheManager();
    }
    private ThinkCacheManager() {
        storageList =new ArrayList<>();
    }

    private static ThinkCacheManager getInstance() {
        return singletonHolder.instance;
    }

    public static class Builder{

        @Remark("构建简单的缓存容器")
        public static final  <T> ThinkCacheStorage<T> newSimpleCacheStorage(Class dataType){
            return newSimpleCacheStorage(dataType,1);
        }

        @Remark(value = "构建指定分区数量的简单缓存容器",description = "指定分区数量,降低单个分区的负载量，性能会相对稳定，但是空间会浪费")
        public static final <T> ThinkCacheStorage<T> newSimpleCacheStorage(Class dataType ,int storageSize){
            ThinkCacheStorage<T> cacheStorage = new ThinkSimpleCacheStorage<>(dataType,storageSize);
            getInstance().storageList.add(cacheStorage);
            return cacheStorage;
        }

    }

    public static class Monitor{
        public static final int getCacheStorageCount(){
            return getInstance().storageList.size();
        }






    }






    public final <T> ThinkCacheStorage<T> newSimpleCacheStorage(Class dataType){
        return this.newSimpleCacheStorage(dataType,1);
    }

    @Remark("构建指定容器分区数量的缓存容器")
    public final <T> ThinkCacheStorage<T> newSimpleCacheStorage(Class dataType ,int storageSize){
        ThinkCacheStorage<T> cacheStorage = new ThinkSimpleCacheStorage<>(dataType,storageSize);
        this.storageList.add(cacheStorage);
        return cacheStorage;
    }

}
