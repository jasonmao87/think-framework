package com.think.data.dao;

import com.think.common.result.ThinkResult;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.data.exception.ThinkDataException;
import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;

import java.util.List;

public interface ThinkSplitPrimaryDao<T extends SimplePrimaryEntity> {

    /**
     * 返回所有 被分割的 表
     * @return
     */
    List<String> showSplitTables();

    /**
     * 通过id 找到对象
     * @param id
     * @return
     */
    T findOne(long id) ;

    /**
     * 通过id 找到被删除的对象
     * @param id
     * @return
     */
    T findDeleted(long id);

    <V extends BaseVo> V findOne(long id ,Class<V> voClass) ;

    /**
     * 自动的查找 list ，会根据filter 定位表 ，同时
     * @param sqlFilter
     * @return
     */
    List<T> autoList(ThinkSqlFilter<T> sqlFilter );

    <V extends BaseVo>  List<V> autoVoList(ThinkSqlFilter<T> sqlFilter ,Class<V> voClass);

    /**
     * 指定分割年份的 list
     * @param sqlFilter
     * @param splitYear
     * @return
     */
    List<T> simpleList(ThinkSqlFilter<T> sqlFilter,int splitYear );


    /**
     * 自动 的count ，会根据filter 定位指定表，可能存在多张表 的查询需求，需要慎重使用
     * @param sqlFilter
     * @return
     */
    long autoCount(ThinkSqlFilter<T> sqlFilter)  ;

    /**
     * 指定分割年份的count
     * @param sqlFilter
     * @param splitYear
     * @return
     */
    long simpleCount(ThinkSqlFilter<T> sqlFilter,int splitYear);


    /**
     * insert， b 参数以便 确认分区 。
     * @param t
     * @return
     */
    ThinkResult<T> insert(T t );

    /**
     * update，需要 多传一次 id 参数以便 确认分区 。
     * @param t
     * @return
     */
    ThinkResult<Integer> update(T t );


    /**
     * 通过此update 必须指定 时间分割分区 ，避免性能上的浪费，如果需要update 多个分区，请多次调用。
     * @param updaterMapper
     * @param splitYear
     * @return
     */
    ThinkResult<Integer> update(ThinkUpdateMapper<T> updaterMapper, int splitYear);


    <V extends BaseVo<T>> ThinkResult<Integer> update(V v,long id );

    <V extends BaseVo<T>> ThinkResult<Integer> update(V v,ThinkSqlFilter<T> sqlFilter );




    /**
     * 批量Insert
     * @param list
     * @return
     */
    ThinkResult<Integer> batchInsert(List<T> list );

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    ThinkResult<Integer> delete(long id );

    /**
     * 逻辑删除
     * @param ids
     * @return
     */
    ThinkResult<Integer> batchDelete(long[] ids );




    Class<T> targetClass();

    /**
     *
     * 2020-10-28  临时新增
     * 计算 id 对应的年份
     * @param id
     * @return
     */
    int computeSplitYearById(long id);



    ThinkResult<Integer> physicalDelete(long id);

    ThinkResult<Integer> physicalDelete(Long[] ids);

    ThinkResult<Integer> physicalDelete(long[] ids);


}
