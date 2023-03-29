package com.think.data.bean.api;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.core.bean.SimpleRefEntity;
import com.think.data.dao.ThinkSplitRefDao;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @Date :2021/8/18
 * @Name :ThinkSplitRefBeanApiImpl
 * @Description : 请输入
 */
@Slf4j
public class ThinkSplitRefBeanApiImpl<T extends SimpleRefEntity>  implements ThinkSplitRefBeanApi<T>{

    private ThinkSplitRefDao<T> dao;

    public ThinkSplitRefBeanApiImpl(ThinkSplitRefDao<T> dao) {
        this.dao = dao;
    }

    @Override
    public Class targetClass() {
        return dao.targetClass();
    }
    @Override
    public T get(long id, long rootPrimaryId) {
        return dao.findOne(id,rootPrimaryId);
    }

    @Override
    public T findDeleted(long id, long rootPrimaryId) {
        return dao.findDeleted(id,rootPrimaryId);
    }

    @Override
    public ThinkResult<T> create(T t, long rootPrimaryId) {
        t.setRootPrimaryId(rootPrimaryId);

        return dao.insert(t);
    }

    //    @Override
    @Deprecated
    public ThinkResult<Integer> update(T t, long id, long rootPrimaryId) {
        T source = dao.findOne(id,rootPrimaryId);
        t.setCreateTime(source.getCreateTime())
                .setCreateUserId(source.getCreateUserId())
                .setUpdateUserId(source.getUpdateUserId())
                .setLastUpdateTime(source.getLastUpdateTime())
                .setPartitionRegion(source.getPartitionRegion())
                .setVersion(source.getVersion());
        if(source == null){
            return ThinkResult.fail("无法找到对象", ResultCode.REQUEST_PARAM_ERROR);
        }
        return dao.update(t);
    }


    @Override
    public ThinkResult<Integer> update(ThinkUpdateMapper<T> updateMapper, long rootPrimaryId) {
        return dao.update(updateMapper,rootPrimaryId);
    }

    @Override
    public List<T> fullRefList(long rootPrimaryId, int limit) {
        return dao.list(rootPrimaryId,limit);
    }

    @Override
    public long fullRefCount(long rootPrimaryId) {
        return dao.count(rootPrimaryId);
    }

    @Override
    public List<T> list(ThinkSqlFilter<T> sqlFilter, long rootPrimaryId) {
        return dao.list(sqlFilter,rootPrimaryId);
    }

    @Override
    public List<Map<String, Object>> mapList(ThinkSqlFilter<T> sqlFilter, long rootPrimaryId) {
        return dao.mapList(sqlFilter,rootPrimaryId);
    }

    @Override
    public long count(ThinkSqlFilter<T> sqlFilter, long rootPrimaryId) {
        return dao.count(sqlFilter,rootPrimaryId);
    }


    @Override
    public ThinkResult<Integer> physicalDelete(Long[] ids, long rootPrimaryId) {
        return dao.physicalDelete(ids,rootPrimaryId);
    }

    @Override
    public ThinkResult<Integer> physicalDelete(long[] ids, long rootPrimaryId) {
        return dao.physicalDelete(ids,rootPrimaryId);
    }

    @Override
    public ThinkResult<Integer> physicalDelete(Long id, long rootPrimaryId) {
        return dao.physicalDelete(id,rootPrimaryId);
    }

    @Override
    public ThinkResult<Integer> batchCreate(List<T> list, long rootPrimaryId) {
        list.forEach(t->{t.setRootPrimaryId(rootPrimaryId);});
        return dao.batchInsert(list);
    }

    /**
     * 通过 json 字符串 获取 list ，必须 指定 filter 封装时候，初始化 的action 事件
     *
     * @param filter
     * @param action
     * @return
     */
    @Override
    public List<T> listByStringSqlFilter(String filter,long rootPrimaryId, ThinkBeanFilterAction action) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.parseFromJSON(filter,targetClass());
        if(action!=null) {
            action.action(sqlFilter);
        }
        return this.list(sqlFilter,rootPrimaryId);
    }

    /**
     * 通过 json 字符串 获取 count ，必须 指定 filter 封装时候，初始化 的action 事件
     *
     * @param filter
     * @param action
     * @return
     */
    @Override
    public long countByStringSqlFilter(String filter,long rootPrimaryId, ThinkBeanFilterAction action) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.parseFromJSON(filter,targetClass());
        if(action!=null) {
            action.action(sqlFilter);
        }
        return this.count(sqlFilter,rootPrimaryId);
    }


    @Override
    public ThinkSqlFilter<T> emptySqlFilter(int limit) {
        return ThinkSqlFilter.build(targetClass(),limit);
    }
}
