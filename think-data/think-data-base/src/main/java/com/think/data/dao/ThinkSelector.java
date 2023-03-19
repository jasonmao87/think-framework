package com.think.data.dao;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.exception.ThinkRuntimeException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ThinkSelector<T extends SimplePrimaryEntity> {

    boolean exists(long id);

    boolean exists( String key,Serializable value) ;

    T findOne(long id) ;

    /**
     * 查找 被删除的对象
     * @param id
     * @return
     */
    T findDeleted(long id );

    <V extends BaseVo<T>> V findOne(long id, Class<V> voClass) throws ThinkRuntimeException;

    Map<String,Object> findOne(long id, String... keys);

    List<T> list(ThinkSqlFilter<T> sqlFilter);

    <V extends BaseVo<T>>List<V>  list(ThinkSqlFilter<T> filter, Class<V> voClass);

    List<Map<String,Object>> list(ThinkSqlFilter<T> filter, String... keys);

    long count(ThinkSqlFilter sqlFilter);

    Class<T> targetClass();

    T findByThinkLinkedId(String thinkLinkedId);


}
