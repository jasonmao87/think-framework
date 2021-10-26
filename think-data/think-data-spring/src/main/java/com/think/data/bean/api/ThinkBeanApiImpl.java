package com.think.data.bean.api;

import com.think.common.data.mysql.TFlowStateUpdate;
import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean.util.ObjectUtil;
import com.think.data.Manager;
import com.think.data.dao.ThinkDao;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

/**
 * @Date :2021/8/18
 * @Name :ThinkBeanApiImpl
 * @Description : 请输入
 */
@Slf4j
public class ThinkBeanApiImpl<T extends SimplePrimaryEntity> implements ThinkBeanApi<T> {


    private ThinkDao<T> dao;

    public ThinkBeanApiImpl(ThinkDao<T> dao ) {
        this.dao = dao;
    }

    /**
     * 获取 该 api 对应的 数据对象类型
     *
     * @return
     */
    @Override
    public Class<T> targetClass() {
        return dao.targetClass();
    }

    @Override
    public T get(long id) {
        return dao.findOne(id);
    }

    @Override
    public T findDeleted(long id) {
        return dao.findDeleted(id);
    }

    @Override
    public T findFirstOneByKey(String key, Serializable value) {
        ThinkSqlFilter sqlFilter = ThinkSqlFilter.build(targetClass())
                .eq(key,value);
        List<T> list = dao.list(sqlFilter);
        return list.size()>0?(T)list.get(0):null;
    }

    @Override
    public ThinkResult<T> create(T t) {
        return dao.insert(t);
    }

    @Override
    public ThinkResult<Integer> createMany(List<T> list)  {
        if (list.size() == 1) {
            ThinkResult<T> result = this.create(list.get(0));
            if(result.isSuccess()){
                return ThinkResult.success(1);
            }else{
                return result.intResult();
            }
        }


        return dao.batchInsert(list);
    }

    //    @Override
    @Deprecated
    public ThinkResult<Integer> update(T t,long id) {

        Map<String, Object> stringObjectMap = ObjectUtil.beanToMap(t);
        Map<String,Serializable> executeMap = new HashMap<>();
        for (Map.Entry<String, Object> ent : stringObjectMap.entrySet()) {
            if(ent.getValue() instanceof Serializable){
                executeMap.put(ent.getKey(),(Serializable) ent.getValue());
            }
        }
        return update(executeMap,id);


    }

    //    @Override
    @Deprecated
    public ThinkResult<Integer> update(Map<String, Serializable> map, long id) {

        if (log.isDebugEnabled()) {
            log.debug("注意：所有的boolean 值 和敏感属性都会被通用的update 方法过滤掉，请尽量通过updateMapper来修改！");
        }
        T source = dao.findOne(id);
        if(source == null){
            return ThinkResult.fail("无法找到对象",ResultCode.REQUEST_PARAM_ERROR);
        }
        //map = UpdateMapFilter.doFilter(map);
        if(ThinkBeanApiFactoryImpl.thinkApiUpdateMethodFilter !=null) {
            ThinkBeanApiFactoryImpl.thinkApiUpdateMethodFilter.doFilter(map);
        }
        if(map.isEmpty()){
            return ThinkResult.fail("未检测到任何有效修改请求",ResultCode.REQUEST_PARAM_ERROR);
        }
        ThinkSqlFilter sqlFilter = ThinkSqlFilter.build(targetClass(),1)
                .eq("id",id);
        ThinkUpdateMapper updateMapper = ThinkUpdateMapper.build(targetClass())
                .setFilter(sqlFilter)
                .putUpdateMap(map);
        return update(updateMapper);
    }

    @Override
    public ThinkResult<Integer> update(ThinkUpdateMapper<T> updateMapper) {
        return dao.update(updateMapper);
    }

    @Override
    public List<T> list(ThinkSqlFilter<T> sqlFilter) {
        return dao.list(sqlFilter);
    }

    @Override
    public <V extends BaseVo<T>> List<V> list(ThinkSqlFilter<T> sqlFilter, Class<V> vo) {
        return dao.list(sqlFilter,vo);
    }

    @Override
    public long count(ThinkSqlFilter<T> sqlFilter) {
        return dao.count(sqlFilter);
    }

    @Override
    public ThinkResult<Integer> enable(long id) {
        return _changeEnableState(id,true);
    }

    @Override
    public ThinkResult<Integer> disable(long id) {
        return _changeEnableState(id,false);
    }

    public ThinkResult<Integer> _changeEnableState(long id ,boolean enable){
        if(Manager.getModelBuilder().get(targetClass()).containsKey("enable")) {

            if (this.dao.findOne(id) != null) {
                ThinkSqlFilter sqlFilter = ThinkSqlFilter.build(targetClass())
                        .eq("id", id);
                ThinkUpdateMapper updaterMapper = ThinkUpdateMapper.build(targetClass())
                        .updateValue("enable", enable)
                        .setFilter(sqlFilter);
                return dao.update(updaterMapper);
            } else {
                return ThinkResult.fail("目标对象不存在", ResultCode.REQUEST_PARAM_ERROR);
            }
        }else {
            return ThinkResult.fail("不支持此方法", ResultCode.SERVER_NOT_SUPPORT);
        }
    }

    @Override
    public ThinkResult<Integer> delete(long id) {
        return dao.delete(id);
    }


    @Override
    public ThinkResult<Integer> physicalDelete(Long[] ids) {
        return dao.physicalDelete(ids);
    }

    @Override
    public ThinkResult<Integer> physicalDelete(long[] ids) {
        return dao.physicalDelete(ids);
    }

    @Override
    public ThinkResult<Integer> physicalDelete(Long id) {
        return dao.physicalDelete(id);
    }

    /**
     * 通过 json 字符串 获取 list ，必须 指定 filter 封装时候，初始化 的action 事件
     *
     * @param filter
     * @param action
     * @return
     */
    @Override
    public List<T> listByStringSqlFilter(String filter, ThinkBeanFilterAction action) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.parseFromJSON(filter,targetClass());
        if(action!=null) {
            action.action(sqlFilter);
        }
        return this.list(sqlFilter);
    }

    /**
     * 通过 json 字符串 获取 count ，必须 指定 filter 封装时候，初始化 的action 事件
     *
     * @param filter
     * @param action
     * @return
     */
    @Override
    public long countByStringSqlFilter(String filter, ThinkBeanFilterAction action) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.parseFromJSON(filter,targetClass());
        if(action!=null) {
            action.action(sqlFilter);
        }
        return this.count(sqlFilter);
    }


    @Override
    public ThinkResult<Integer> tFlowResultChangeToStart(long id, String mainKey) {
        TFlowStateUpdate update = TFlowStateUpdate.buildStart(mainKey);
        return this.tFlowResultChange(id,update);

    }

    @Override
    public ThinkResult<Integer> tFlowResultChangeToCancel(long id, String mainKey) {
        TFlowStateUpdate update = TFlowStateUpdate.buildCancel(mainKey);
        return this.tFlowResultChange(id,update);

    }

    @Override
    public ThinkResult<Integer> tFlowResultChangeToComplete(long id, String mainKey, boolean result, String message) {
        TFlowStateUpdate update = TFlowStateUpdate.buildComplete(mainKey,result,message);
        return this.tFlowResultChange(id,update);
    }

    @Override
    public ThinkResult<Integer> tFlowResultChangeToClearState(long id, String mainKey) {
        TFlowStateUpdate update = TFlowStateUpdate.buildClear(mainKey);

        return this.tFlowResultChange(id,update);

    }

    public ThinkResult<Integer> tFlowResultChange(long id ,TFlowStateUpdate update){
        ThinkUpdateMapper<T> updateMapper = ThinkUpdateMapper.build(targetClass());
        updateMapper.setTargetDataId(id)
                .updateTFlowState(update);
        return this.update(updateMapper);
    }


    @Override
    public ThinkSqlFilter<T> emptySqlFilter(int limit) {
        return ThinkSqlFilter.build(targetClass(),limit);
    }
}
