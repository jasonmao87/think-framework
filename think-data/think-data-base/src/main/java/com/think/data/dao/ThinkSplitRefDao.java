package com.think.data.dao;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimpleRefEntity;

import java.util.List;

/**
 * 专用于  时间分割分区业务子表/从表 的Dao ，提供受限制的 功能
 * @param <T>
 */
public interface ThinkSplitRefDao<T extends SimpleRefEntity> {

    /**
     * 返回所有 被分割的 表
     * @return
     */
    List<String> showSplitTables();

    /**
     * 返回所有 被分割的 时间分割分区
     * @return
     */
//    List<Integer> showSplitYears();

    T findOne(long id ,long rootPrimaryId );

    T findDeleted(long id ,long rootPrimaryId);

    long count(long rootPrimary);

    long count(ThinkSqlFilter<T> sqlFilter ,long rootPrimaryId);

    List<T> list(long rootPrimaryId,int limit );

    List<T> list(ThinkSqlFilter<T> sqlFilter ,long rootPrimaryId);

    ThinkResult<T> insert(T t);

    ThinkResult<Integer> batchInsert(List<T> list);

    ThinkResult<Integer> update(T t);

    <V extends BaseVo<T>> ThinkResult<Integer> update(V v, long id ,long rootPrimaryId);

    ThinkResult<Integer> update(ThinkUpdateMapper<T> updaterMapper, long rootPrimaryId);


    ThinkResult<Integer> delete(long id,long rootPrimaryId);

    ThinkResult<Integer> batchDelete(long[] ids,long rootPrimaryId);


    ThinkResult<Integer> physicalDelete(long id,long rootPrimaryId);

    ThinkResult<Integer> physicalDelete(Long[] ids,long rootPrimaryId);

    ThinkResult<Integer> physicalDelete(long[] ids,long rootPrimaryId);

    Class<T> targetClass();

}
