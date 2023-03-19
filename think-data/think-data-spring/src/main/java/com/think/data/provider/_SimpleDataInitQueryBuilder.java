package com.think.data.provider;

import com.think.core.bean.SimplePrimaryEntity;
import com.think.data.ThinkDataInitializationDataHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化数据 query 构建起器
 * @Date :2020/12/25
 * @Author :JasonMao
 * @LastUpdate :2020/12/25
 * @Description :
 */
@Slf4j
public class _SimpleDataInitQueryBuilder {

    protected static <T extends SimplePrimaryEntity> ThinkExecuteQuery initData(Class<T> targetClass ){
        if(ThinkDataInitializationDataHolder.containsData(targetClass)) {
            List<T> list = ThinkDataInitializationDataHolder.getInitData(targetClass);
            ThinkDataInitializationDataHolder.removeInitData(targetClass);
            if(list.isEmpty() ){

                return null;
            }
            if (list.size() > 1) {
                return ThinkUpdateQueryBuilder.batchInsertSQL(list);
            } else {
                return ThinkUpdateQueryBuilder.insertOneSQL(list.get(0));

            }
        }
        return null;
    }

    protected static <T extends SimplePrimaryEntity> List<ThinkExecuteQuery> initDataQueryList(Class<T> targetClass ){
        List<ThinkExecuteQuery> queryList = new ArrayList<>();
        if(ThinkDataInitializationDataHolder.containsData(targetClass)) {
            List<T> list = ThinkDataInitializationDataHolder.getInitData(targetClass);
            ThinkDataInitializationDataHolder.removeInitData(targetClass);
            for (T t : list) {
                ThinkExecuteQuery query = ThinkUpdateQueryBuilder.insertOneSQL(t);
                queryList.add(query);
            }
        }
        return queryList;
    }


}
