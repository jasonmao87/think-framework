package com.think.core.data.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ExpireAbleCache<K,T> {
    private ExpiredRemoveSubscriber<K,T> expiredRemoveSubscriber;
    private final Map<K,CacheEntry> cacheEntryMap;
    Class<T> cachedClass;
    Class<K> keyClass;
    int defaultExpireTime = 15;
    TimeUnit defaultTimeUnit = TimeUnit.MINUTES;
    public ExpireAbleCache(Class<K> keyClass, Class<T> cachedClass, int defaultExpireTime, TimeUnit timeUnit) {
        this.keyClass = keyClass;
        this.cachedClass = cachedClass;
        this.defaultExpireTime = defaultExpireTime;
        this.defaultTimeUnit = timeUnit;
        this.cacheEntryMap = new HashMap<>();
    }

    public void subscribe(ExpiredRemoveSubscriber<K,T> expiredRemoveSubscriber){
        this.expiredRemoveSubscriber = expiredRemoveSubscriber;
    }


    public void cache(K k , T t){
        this.cacheEntryMap.put(
                k,
                new CacheEntry(t,System.currentTimeMillis() + defaultTimeUnit.toMillis(defaultExpireTime))
        );
    }

    public T get(K k){
        final CacheEntry cacheEntry = this.cacheEntryMap.get(k);
        if (cacheEntry!=null && !cacheEntry.isExpired()){
            return cacheEntry.getValue();
        }
        return null;
    }

    public Optional<T> getOptional(K k){
        return Optional.ofNullable(get(k));
    }

    public void remove(K k){
        this.cacheEntryMap.remove(k);
    }

    public void clearExpired(){
        this.cacheEntryMap.entrySet().removeIf(entry ->{
            boolean expired = entry.getValue().isExpired();
            if(expired && expiredRemoveSubscriber !=null){
                expiredRemoveSubscriber.onExpiredRemove(entry.getKey(),entry.getValue().getValue());
            }
            return expired;
        });
    }



    class CacheEntry{
        T value;
        long expireTime;
        public CacheEntry(T value,long expireTime){
            this.value = value;
            this.expireTime = expireTime;
        }
        public T getValue() {
            return value;
        }
        public boolean isExpired(){
            return System.currentTimeMillis() > expireTime;
        }
    }


    public static interface ExpiredRemoveSubscriber<K,T>{
        void onExpiredRemove(K k,T t);
    }

}

