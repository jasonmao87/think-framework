package com.think.core.data.cache;

import com.think.core.annotations.Remark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/2 15:14
 * @description : TODO  ----需要实现 ？？？
 */
public class ThinkLRUCacheHolder<T>  implements TCacheStorage<T> {

    @Remark("存储空间数量")
    int storageSize = 1;
    private List<Map<String,ThinkCache<T>>> storageList ;


    private int getStorageIndex(String key){
        if(storageSize == 1){
            return 0;
        }else{
            return key.hashCode()%storageSize;
        }
    }


    protected ThinkLRUCacheHolder(int storageSize) {
        this.storageSize = storageSize;
        storageList = new ArrayList<>();
        for (int i = 0; i < storageSize;  i++) {
            Map<String,ThinkCache<T>> storage = new HashMap<>();
            storageList.add(storage);
        }
    }


    @Override
    public void cache(String key, T data) {
        final int storageIndex = this.getStorageIndex(key);
        storageList.get(storageIndex).put(key,new ThinkCache<>(data,1));

    }

    @Override
    public T getCache(String key) {
        return null;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void clearAll() {

    }
}
