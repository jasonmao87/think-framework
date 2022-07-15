package com.think.data.bean.api;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.core.annotations.Remark;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;

import java.io.Serializable;
import java.util.List;

public interface ThinkBeanApi<T extends SimplePrimaryEntity>  {

    /**
     * 获取 该 api 对应的 数据对象类型
     * @return
     */
    Class<T> targetClass();

    /**
     *  获取 对象 详情
     * @param id
     * @return
     */
    T get(long id );


    /**
     * 读取自定义的 VIEW
     * @param id
     * @param viewClass
     * @param <V>
     * @return
     */
    <V extends BaseVo<T>> V getView(long id,Class<V> viewClass);
    /**
     * 获取 被删除的对象 详情数据
     * @param id
     * @return
     */
    T findDeleted(long id );


    public T findFirstOneByKey(String key , Serializable value);

    /**
     * 通过一个key 查找
     * @param key
     * @param value
     * @param limit
     * @return
     */
    List<T> findListByKey(String key ,Serializable value ,int limit );


    ThinkResult<T> create(T t);

    ThinkResult<Integer> createMany(List<T> list);


    ThinkResult<Integer> update(ThinkUpdateMapper<T> updateMapper);

    List<T> list(ThinkSqlFilter<T> sqlFilter);

    <V extends BaseVo<T>> List<V> list(ThinkSqlFilter<T> sqlFilter, Class<V> vo);

    long count(ThinkSqlFilter<T> sqlFilter);

    /**
     * 非必须， 启用
     * @param id
     * @return
     */
    ThinkResult<Integer> enable(long id);

    /**
     * 非必须 ，禁用
     * @param id
     * @return
     */
    ThinkResult<Integer> disable(long id);


    /**
     * 逻辑删除，非必要误用！
     * @param id
     * @return
     */
    ThinkResult<Integer> delete(long id);

    /**
     * 根据条件删除
     * @param sqlFilter
     * @return
     */
    ThinkResult<Integer> delete(ThinkSqlFilter<T> sqlFilter);

    /**
     * 物理删除
     * @param ids
     * @return
     */
    ThinkResult<Integer> physicalDelete(Long[] ids);

    ThinkResult<Integer> physicalDelete(long[] ids);

    ThinkResult<Integer> physicalDelete(Long id);


    /**
     * 通过 json 字符串 获取 list ，必须 指定 filter 封装时候，初始化 的action 事件
     * @param filter
     * @param action
     * @return
     */
    @Remark("通过 json 字符串 获取 list ，必须 指定 filter 封装时候，初始化 的action 事件")
    List<T> listByStringSqlFilter(String filter,ThinkBeanFilterAction action);

    /**
     * 通过 json 字符串 获取 count ，必须 指定 filter 封装时候，初始化 的action 事件
     * @param filter
     * @param action
     * @return
     */
    @Remark("通过 json 字符串 获取 count ，必须 指定 filter 封装时候，初始化 的action 事件")
    long countByStringSqlFilter(String filter, ThinkBeanFilterAction action);


    @Remark("目标对象的流程状态变更为开始 ")
    ThinkResult<Integer> tFlowResultChangeToStart(long id ,String mainKey);

    @Remark("目标对象的流程状态变更为取消 ")
    ThinkResult<Integer> tFlowResultChangeToCancel(long id ,String mainKey);



    @Remark(value = "目标对象的流程状态变更为结束",description = "result 代表结果，true代表通过")
    ThinkResult<Integer> tFlowResultChangeToComplete(long id ,String mainKey,boolean result ,String message);


    @Remark(value = "目标对象的流程状态重置" )
    ThinkResult<Integer> tFlowResultChangeToClearState(long id , String mainKey );


    ThinkSqlFilter<T> emptySqlFilter(int limit);




}
