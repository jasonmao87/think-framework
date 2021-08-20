package com.think.mongo;

import com.think.common.data.IFilterChecker;
import com.think.common.data.mongo.ThinkMongoQueryFilter;
import com.think.core.bean.SimpleMongoEntity;
import com.think.mongo.model.ThinkMongoModel;

import java.util.HashMap;
import java.util.Map;

public class ThinkMongoManager {

    private static final Map<Class, ThinkMongoModel> modalCache = new HashMap<>();
    static {
        ThinkMongoQueryFilter.setiFilterChecker(new IFilterChecker() {
            @Override
            public boolean checkKey(String key, Class targetClass) {
                return getModal(targetClass).containsKey(key);
            }
        });

    }

    public static final <T extends SimpleMongoEntity> ThinkMongoModel getModal(Class<T> tClass){
        if(modalCache.containsKey(tClass)  == false){
            ThinkMongoModel modal = ThinkMongoModel.build(tClass);
            modalCache.put(tClass,modal);
        }
        return modalCache.get(tClass);
    }


}
