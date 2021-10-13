package com.think.data.bean.api;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.core.annotations.Remark;
import com.think.core.bean.SimplePrimaryEntity;

import java.util.List;

public interface ThinkSplitBeanApi<T extends SimplePrimaryEntity> {

    Class targetClass();

    /**
     * 获取对象，会自动匹配 到对应到年度切分表
     * @param id
     * @return
     */
    T get(long id);

    T findDeleted(long id) ;


    /**
     * 创建对象， 会默认 insert到本年度切分表
     * @param t
     * @return
     */
    ThinkResult<T> create(T t);

    ThinkResult<Integer> createMany(List<T> list);


    @Remark("使用updateMappe更新")
    ThinkResult<Integer> update(ThinkUpdateMapper<T> updateMapper, long id );
    /**
     * 获取list ，会自动匹配 跨表 等复杂逻辑， 无需过多考虑，性能上略微会逊色一些
     * @param sqlFilter
     * @return
     */
    List<T> list(ThinkSqlFilter<T> sqlFilter);

    /**
     * 获取count ，会自动匹配 跨表 等复杂逻辑， 无需过多考虑，性能上略微会逊色一些
     * @param sqlFilter
     * @return
     */
    long count(ThinkSqlFilter<T> sqlFilter);

    /**
     * 指定年份 得 list
     * @param sqlFilter
     * @param splitYear
     * @return
     */
    List<T> list(ThinkSqlFilter<T> sqlFilter,int splitYear);

    /**
     * 指定年份得count
     * @param sqlFilter
     * @param splitYear
     * @return
     */
    long count(ThinkSqlFilter<T> sqlFilter,int splitYear);

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
     * 物理删除
     * @param ids
     * @return
     */
    ThinkResult<Integer> physicalDelete(Long[] ids);

    ThinkResult<Integer> physicalDelete(long[] ids);

    ThinkResult<Integer> physicalDelete(Long id);

    ThinkResult<Integer> delete(long id);




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



}
