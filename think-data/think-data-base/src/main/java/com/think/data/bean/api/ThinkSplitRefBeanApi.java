package com.think.data.bean.api;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.core.annotations.Remark;
import com.think.core.bean.SimpleRefEntity;

import java.util.List;
import java.util.Map;

/**
 * @Date :2021/8/18
 * @Name :ThinkSplitRefBeanApi
 * @Description : 请输入
 */
public interface ThinkSplitRefBeanApi <T extends SimpleRefEntity> {

    Class targetClass();


    T get(long id ,long rootPrimaryId);

    T findDeleted(long id,long rootPrimaryId) ;

    /**
     * 创建 从表 ，必须携带 主表得id
     * @param t
     * @return
     */
    ThinkResult<T> create(T t, long rootPrimaryId);



//    @Remark(value = "2021/1/28后，不推荐使用",description = "请使用更安全和更高效的的update方法，已经实现的方法请逐步调整，在2021/4/1日前废弃")
//    @Deprecated
//    ThinkResult<Integer> update(T t,long id,long rootPrimaryId);
//
//    @Remark("使用map映射来update对象！")
//    ThinkResult<Integer> update(Map<String, Serializable> map, long id , long rootPrimaryId);

    ThinkResult<Integer> update(ThinkUpdateMapper<T> updateMapper , long rootPrimaryId);

    /**
     * 关联主表得 全量数据
     * @param rootPrimaryId     主表id
     * @param limit             限制返回条目
     * @return
     */
    List<T> fullRefList(long rootPrimaryId, int limit);

    /**
     * 跟随主表得 统计数据
     * @param rootPrimaryId
     * @return
     */
    long fullRefCount(long rootPrimaryId);


    List<T> list(ThinkSqlFilter<T> sqlFilter , long rootPrimaryId);

    List<Map<String,Object>> mapList(ThinkSqlFilter<T> sqlFilter , long rootPrimaryId);

    long count(ThinkSqlFilter<T> sqlFilter ,long rootPrimaryId);

    /**
     * 物理删除
     * @param ids
     * @return
     */
    ThinkResult<Integer> physicalDelete(Long[] ids,long rootPrimaryId);

    ThinkResult<Integer> physicalDelete(long[] ids,long rootPrimaryId);

    ThinkResult<Integer> physicalDelete(Long id,long rootPrimaryId);


    ThinkResult<Integer> batchCreate(List<T> list,long rootPrimaryId);

    /**
     * 通过 json 字符串 获取 list ，必须 指定 filter 封装时候，初始化 的action 事件
     * @param filter
     * @param action
     * @return
     */
    @Remark("通过 json 字符串 获取 list ，必须 指定 filter 封装时候，初始化 的action 事件")
    List<T> listByStringSqlFilter(String filter,long rootPrimaryId,ThinkBeanFilterAction action);

    /**
     * 通过 json 字符串 获取 count ，必须 指定 filter 封装时候，初始化 的action 事件
     * @param filter
     * @param action
     * @return
     */
    @Remark("通过 json 字符串 获取 count ，必须 指定 filter 封装时候，初始化 的action 事件")
    long countByStringSqlFilter(String filter,long rootPrimaryId, ThinkBeanFilterAction action);


    ThinkSqlFilter<T> emptySqlFilter(int limit);


}
