package com.think.core.data.cache;

import com.think.common.util.IdUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.common.util.TimeUtil;
import com.think.core.annotations.Remark;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/2 15:14
 * @description :  默认的缓存 容器 类
 */
@Slf4j
public class ThinkSimpleCacheStorage<T>  implements ThinkCacheStorage<T> {

    private final long id ;
    private final String name ;

    private Class<T> dataType ;

    @Remark("存储空间数量")
    int storageSize = 1;

    @Remark("最近缓存自动释放时间")
    private long lastCacheAutoReleaseTime = 0L;

    @Remark("当前缓存总数")
    private int cacheCount = 0;

    @Remark("最大缓存量,默认10240")
    private int cacheMaxLimit = 10240;

    @Remark("缓存次数")
    private long cacheWriteTimes;

    @Remark("读取次数")
    private long cacheReadTimes ;

    @Remark("读取缓存命中次数")
    private long cacheReadHitTimes;

    @Remark("允许操作标记")
    private volatile boolean opAble  = true;

    private Map<String,ThinkCache<T>>[] cacheStorages ;


    private int getStorageIndex(String key){
        if(storageSize == 1){
            return 0;
        }else{
            int i = key.hashCode();
            i = i<0?-i:i;
            return i%storageSize;
        }
    }


    private void initStorageArray(){
        if(cacheStorages == null) {
            cacheStorages = new HashMap[storageSize];
            for (int i = 0; i < storageSize; i++) {
                Map<String, ThinkCache<T>> storage = new HashMap<>();
                cacheStorages[i] = storage;
            }
        }else{
            throw new ThinkRuntimeException("已经初始化缓存容器仓库，禁止重新初始化缓存容器仓库");
        }
    }


    protected ThinkSimpleCacheStorage(Class<T> tClass, int storageSize) {
        if(storageSize < 1){
            throw new ThinkRuntimeException("构建缓存容器异常，缓存容器分区数量必须大于0");
        }
        this.name= "Think-Default-Cache-Storage-" + IdUtil.nextShortId();
        this.storageSize = storageSize;
        this.dataType = tClass;
        this.initStorageArray();
        this.id = IdUtil.nextId();
    }

    protected ThinkSimpleCacheStorage(String name, Class<T> dataType, int storageSize) {
        if(storageSize < 1){
            throw new ThinkRuntimeException("构建缓存容器异常，缓存容器分区数量必须大于0");
        }
        this.name = name;
        this.dataType = dataType;
        this.storageSize = storageSize;
        this.initStorageArray();
        this.id = IdUtil.nextId();
    }


    @Override
    public boolean put(String key, T data) {
        int cacheTryTime =0;
        while (!opAble  ){
            TimeUtil.sleep(2, TimeUnit.MILLISECONDS);
            cacheTryTime ++ ;
            if(cacheTryTime > 5 ){
                break;
            }
        }
        if(opAble) {
            if (log.isTraceEnabled()) {
                log.trace("缓存数据 ,当前缓存数量 {} ", cacheCount);
            }
            final ThinkCache<T> put = getStorageMap(key).put(key, new ThinkCache<>(data, 1));
            if (put == null) {
                cacheCount++;
                this.cacheWriteTimes ++ ;
            }
            return true;
        }
        return false;
    }

    @Override
    public T getCache(String key) {
        this.cacheReadTimes ++ ;
        final Map<String, ThinkCache<T>> storageMap = getStorageMap(key);
        final ThinkCache<T> cache = storageMap.get(key);
        if (cache == null || cache.isExpire()) {
            this.remove(key);
            return null;
        } else {
            this.cacheReadHitTimes ++ ;
            return cache.getData();
        }
    }

    @Override
    public boolean remove(String key) {
        int cacheTryTime =0;
        while (!opAble  ){
            TimeUtil.sleep(2, TimeUnit.MILLISECONDS);
            cacheTryTime ++ ;
            if(cacheTryTime > 5 ){
                break;
            }
        }
        if(opAble) {
            final ThinkCache<T> remove = getStorageMap(key).remove(key);
            if (remove != null) {
                cacheCount--;
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized void clearAll() {
        opAble = false;
        for (Map<String, ThinkCache<T>> cacheStorage : this.cacheStorages) {
            cacheStorage.clear();
        }
        cacheCount = 0;
        opAble =  true;
    }


    @Override
    public void checkAndReleaseSpace() {
        final double perV = 0.7D;
        long now = ThinkMilliSecond.currentTimeMillis();
        final long time_1_second = 1000L;
        if(now - lastCacheAutoReleaseTime > time_1_second) {
            this.lastCacheAutoReleaseTime = ThinkMilliSecond.currentTimeMillis();
            double x = 1.0 *  cacheCount  /cacheMaxLimit;
            if(x < perV){
                this.releaseExpire();
            }else{
                int round = 1 ;
                while (x > perV){
                    releaseSize(round);
                    x = 1.0 *  cacheCount  /cacheMaxLimit;
                    round ++ ;
                }
            }

        }
    }


    private void releaseSize(int round){
        final Set<String> removeSet =new HashSet<>();
        final long deleteSinceTime = ThinkMilliSecond.currentTimeMillis() - (round * 2L * 60L *1000L);
        for (Map<String, ThinkCache<T>> cacheStorage : cacheStorages) {
            final Iterator<Map.Entry<String, ThinkCache<T>>> iterator = cacheStorage.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, ThinkCache<T>> entry = iterator.next();
                final long expireTime = entry.getValue().getExpireTime();
                if(expireTime <= deleteSinceTime){
                    removeSet.add(entry.getKey());
                }
            }
        }
        for (String k : removeSet) {
            this.remove(k);
        }

    }

    private void releaseExpire(){
        final Set<String> removeSet =new HashSet<>();
        for (Map<String, ThinkCache<T>> cacheStorage : cacheStorages) {
            final Iterator<Map.Entry<String, ThinkCache<T>>> iterator =
                    cacheStorage.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, ThinkCache<T>> next = iterator.next();
                if(next.getValue().isExpire()){
                    removeSet.add(next.getKey());
//                    iterator.remove();
//                    cacheCount -- ;
                }
            }
        }
        for (String k : removeSet) {
            this.remove(k);
        }

    }



    private  Map<String,ThinkCache<T>> getStorageMap(final int index){
        if(index >= storageSize){
            return cacheStorages[0];
        }
        return cacheStorages[index];
    }

    private  Map<String,ThinkCache<T>> getStorageMap(final String key){
        final int storageIndex = this.getStorageIndex(key);
        return getStorageMap(storageIndex);
    }


    @Override
    public int getCachedDataCount(){
        return cacheCount;
    }

    @Override
    @Remark("读取缓存容器名称")
    public String getName() {
        return name;
    }


    @Override
    public long getCacheReadHitTimes() {
        return cacheReadHitTimes;
    }

    @Override
    public long getCacheReadTimes() {
        return cacheReadTimes;
    }

    @Override
    public long getCacheWriteTimes() {
        return cacheWriteTimes;
    }

    @Override
    public long getLastCacheAutoReleaseTime() {
        return lastCacheAutoReleaseTime;
    }

    @Override
    public String storageId() {
        return Long.toString(this.id,36);
    }
}
