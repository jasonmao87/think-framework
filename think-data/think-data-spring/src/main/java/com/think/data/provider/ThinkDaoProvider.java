package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.common.util.IdUtil;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ObjectUtil;
import com.think.data.Manager;
import com.think.data.dao.ThinkDao;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class ThinkDaoProvider<T extends SimplePrimaryEntity>  extends _JdbcExecutor  implements ThinkDao<T>  {

    private JdbcTemplate jdbcTemplate;
    protected Class<T> targetClass = null;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T extends _Entity> Class getTargetClass() {
        return this.targetClass;
    }
    @Override
    public Class<T> targetClass() {
        return targetClass;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }


    public ThinkDaoProvider() {
        Type superClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            this.targetClass = (Class<T>) ((ParameterizedType) type).getRawType();
        } else {
            this.targetClass = (Class<T>) type;
        }
        if(Manager.getModelBuilder().get(targetClass).isYearSplitAble()){
            throw new ThinkRuntimeException(targetClass.getName() +" 是时间分割对象，请使用 com.think.data.dao.ThinkSplitDao");
        }
    }

    public ThinkDaoProvider(Class<T> targetClass){
        this.targetClass =targetClass;
        if(Manager.getModelBuilder().get(targetClass).isYearSplitAble()){
            throw new ThinkRuntimeException(targetClass.getName() +" 是时间分割对象，请使用 com.think.data.dao.ThinkSplitDao");
        }
    }

    public String finalTableName(){
        return _DaoSupport.baseTableName(targetClass);
    }

    @Override
    public boolean exists(long id) {
        Map<String, Object> map = findOne(id, "id");
        return map!=null && (!map.isEmpty()) ;
    }

    @Override
    public boolean exists(String key, Serializable value)    {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(this.targetClass ,1)
                .eq(key,value);
        if(Manager.getModelBuilder().get(targetClass).containsKey(key) == false) {
            if (log.isWarnEnabled()) {
                log.warn("{}中不存在{} ，判断是否存在数据无法保证", targetClass.getName(), key);
            }
        }
        return this.list(sqlFilter).size() > 0 ;
     }

     public ThinkResult<Integer> checkIdBeforeBatchInsert(List<T> list) {

         if (list.size() > 100) {
             List<T> tempList = new ArrayList<>();
             for (T t : list) {
                 tempList.add(t);
                 if (tempList.size() == 50) {
                     ThinkResult<Integer> tempResult = this.checkIdBeforeBatchInsert(tempList);
                     if (tempResult.isNotSuccess()) {
                         return tempResult;
                     }
                     tempList.clear();
                 }
             }
             if (tempList.isEmpty() == false) {
                 return this.checkIdBeforeBatchInsert(tempList);
             }
         } else {
             //检查 id 是否 冲突
             Long[] idArray = _DaoSupport.getIdArray(list);
             if (idArray.length > 0) {
                 ThinkSqlFilter<T> checkIdSqlFilter = ThinkSqlFilter.build(targetClass, -1).in("id", idArray);
                 List<Map<String, Object>> findExistIdList = this.list(checkIdSqlFilter, "id");
                 if (findExistIdList.size() > 0) {
                     StringBuilder idErrorInfo = new StringBuilder("以下id可能存在冲突：");
                     int i = 0;
                     for (Map<String, Object> map : findExistIdList) {
                         if (i > 0) {
                             idErrorInfo.append(",");
                         }
                         idErrorInfo.append(map.get("id"));
                         i++;
                     }
                     idErrorInfo.append("操作被拒绝");
                     return ThinkResult.forbidden(idErrorInfo.toString());
                 }
             }
         }
         return ThinkResult.success();
     }


    @Override
    public ThinkResult<T> insert(T t) {
         _DaoSupport.callInsert(t);
        if(false == Manager.getModelBuilder().get(t.getClass()).isAutoIncPK()){
            if(t.getId() == null  || t.getId()< 1L){
                t.setId(IdUtil.nextId());
            }
        }
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.insertOneSQL(t);
        ThinkResult result= executeUpdate(query,finalTableName());
        if(result.isSuccess()){
            ObjectUtil.setDbPersistent(t);
            return ThinkResult.success(t);
        }
        return result;
    }

    @Override
    public ThinkResult<Integer> batchInsert(List<T> list) {
        _DaoSupport.callInsert(list);
        if(list.size() <2){
            return ThinkResult.fail("批量插入的对象数量不足。最小要求2", ResultCode.REQUEST_PARAM_ERROR);
        }
        //新增自动初始化Id
        if( false == Manager.getModelBuilder().get(list.get(0).getClass()).isAutoIncPK() ){
            if (list.get(0).getId() == null || list.get(0).getId() < 1L) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setId(IdUtil.nextId());
                }
            }
        }
//        ThinkResult<Integer> checkIdBeforeBatchInsertResult = this.checkIdBeforeBatchInsert(list);
//        if(checkIdBeforeBatchInsertResult.isNotSuccess()){
//            return checkIdBeforeBatchInsertResult;
//        }


        int size = list.size();
        if(size > 60) {
            Queue<T> queue = new LinkedList<>();
            for (T t : list) {
                queue.offer(t);
            }
            List<T> tempList = new ArrayList<T>();
            while (queue.peek() != null) {
                tempList.add(queue.poll());
                if (tempList.size() == 50) {
                    ThinkResult<Integer> result =this.batchInsert(tempList);
                    if ( result.isNotSuccess() ) {
                        return this.rollbackInsert(list,result);
                    }
                    tempList.clear();
                }
            }
            if(tempList.size()>0) {
                if (tempList.size() > 1) {
                    ThinkResult<Integer> result = this.batchInsert(tempList);
                    if ( result.isNotSuccess() ) {
                        return this.rollbackInsert(list,result);
                    }
                } else {
                    ThinkResult<T> result = this.insert(tempList.get(0));
                    if ( result.isNotSuccess()) {
                        return this.rollbackInsert(list,result);
                    }
                }
            }
            return ThinkResult.success(size);
        }

        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.batchInsertSQL(list);
        return executeUpdate(query,finalTableName());
    }





    public ThinkResult<Integer> rollbackInsert(List<T> list ,ThinkResult result){
        Long[] idArray = list.stream()
                .map(T::getId)
                .collect(Collectors.toList())
                .toArray(new Long[list.size()]);
        this.physicalDelete(idArray);
        return result;
    }



    @Override
    public ThinkResult<Integer> update(T t) {
        _DaoSupport.callUpdate(t,null);
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.updateSql(t);
        return executeUpdate(query,finalTableName());

    }


    @Override
    public <V extends BaseVo<T>> ThinkResult<Integer> update(V v, long id) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter
                .build(getTargetClass(),-1)
                .eq("id",id);
        return this.update(v,sqlFilter);
    }

    @Override
    public <V extends BaseVo<T>> ThinkResult<Integer> update(V v, ThinkSqlFilter<T> sqlFilter) {
        Map<String,Object> voMap = ObjectUtil.beanToMap(v);
        ThinkUpdateMapper<T> updateMapper = ThinkUpdateMapper.build(getTargetClass())
                .putUpdateMap(voMap)
                .setFilter(sqlFilter);
        return this.update(updateMapper);
    }

    @Override
    public ThinkResult<Integer> update(ThinkUpdateMapper<T> updaterMapper ) {
        _DaoSupport.callUpdate(null,updaterMapper);
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.updateSql(updaterMapper);
        return executeUpdate(query,finalTableName());
    }


    @Override
    public ThinkResult<Integer> delete(long id) {
        return delete(new Long[]{id});
    }

    @Override
    public ThinkResult<Integer> delete(Long[] ids) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass);
        if(ids.length ==0){
            return ThinkResult.success(0).appendMessage("未删除任何数据");
            //return ThinkResult.fail("非法的参数，必须指定id",ResultCode.REQUEST_NO_RESOURCE);
        }else if(ids.length>1){
            sqlFilter.in("id",ids);
        }else{
            sqlFilter.eq("id",ids[0]);
        }
        return this.delete(sqlFilter);
//        ThinkUpdaterMapper<T> updaterMapper = ThinkUpdaterMapper.build(targetClass)
//                .setFilter(sqlFilter)
//                .updateToKeyValue("id","-id")
//                .updateValue("deleteState" ,true)
//                .updateValue("deleteTime", DateUtil.now());
//        return this.update(updaterMapper);
    }

    @Override
    public ThinkResult<Integer> delete(ThinkSqlFilter<T> sqlFilter) {
        sqlFilter.largeThan("id",0);
       ThinkUpdateMapper updaterMapper = ThinkUpdateMapper.build(targetClass)
               .setFilter(sqlFilter)
               // 删除 策略 调整 为 id 变为： 0-id ！避免使用deleteState
               .updateToKeyValue("id","-id");
//              移除 删除相关字段
//               .updateValue("deleteState" ,true)
//               .updateValue("deleteTime", DateUtil.now());
        return this.update(updaterMapper);
    }

    @Override
    public ThinkResult<Integer> physicalDelete(long id) {
        if(log.isWarnEnabled()) {
            Map t = this.findOne(id ,"id");
            if(t == null){
                log.warn("不存在id = {}的数据，放弃物理删除！" ,id );
                return ThinkResult.success(0).appendMessage("未删除任何数据");
//                return ThinkResult.fail("不存在指定数据,放弃执行物理删除",ResultCode.REQUEST_NO_RESOURCE);
            }else {
                if(log.isWarnEnabled()) {
                    log.warn("即将物理删除数据 class ={} ，id ={} ,TABLE =  {} ", targetClass.getName(), id, finalTableName());
                }
            }
        }
        return this.physicalDelete(new Long[]{id});
    }

    @Override
    public ThinkResult<Integer> physicalDelete(long[] ids) {
        Long[] longIds = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
            longIds[i] = ids[i];
        }
        return physicalDelete(longIds);
    }

    @Override
    public ThinkResult<Integer> physicalDelete(Long[] ids) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass,ids.length);
        if(ids.length ==0){
            return ThinkResult.success(0);
//            return ThinkResult.fail("非法的参数，必须指定id",ResultCode.REQUEST_NO_RESOURCE);
        }else if(ids.length>1){
            sqlFilter.in("id",ids);
        }else{
            sqlFilter.eq("id",ids[0]);
        }
        if(log.isWarnEnabled()) {
            List<T> list = this.list(sqlFilter);

            if(list.size()==0){
                log.warn("不存在id在{}内的数据，放弃物理删除！" ,  Arrays.toString(ids));
                return ThinkResult.success(0).appendMessage("未删除任何数据");
//                return ThinkResult.fail("不存在指定数据,放弃执行物理删除",ResultCode.REQUEST_NO_RESOURCE);
            }else {
                if(log.isWarnEnabled()) {
                    for (int i = 0; i < list.size(); i++) {
                        log.warn("即将物理删除数据 {} of {} class ={} ，id ={} ,DATA : {} ",
                                (1 + i), list.size(), targetClass.getName(), list.get(i).getId(), list.get(i).toString());
                    }
                }
            }
        }
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.physicalDeleteSql(sqlFilter);
        return this.executeUpdate(query,finalTableName());
        //return ThinkResult.fail("未删除任何数据或其他问题，请检查参数是否存在");
    }

    @Override
    public T findOne(long id) {
        Map<String, Object> map = this.findOne(id,"*");
        if(map ==null  || map.isEmpty() ){
            return null;
        }
        T  t  = (T) ObjectUtil.mapToBean(map,targetClass);
        return t;

    }

    @Override
    public T findDeleted(long id) {
        return findOne(-id);
    }

    @Override
    public <V extends BaseVo<T>> V findOne(long id, Class<V> voClass) throws ThinkRuntimeException {
        String[] keys =  _DaoSupport.voKeys(targetClass,voClass);
        Map<String, Object> map = this.findOne(id,keys);
        if(map==null || map.isEmpty() ){
            return null;
        }
        return (V) ObjectUtil.mapToBean(map,voClass);

    }

    @Override
    public Map<String, Object> findOne(long id, String... keys) {
        ThinkSqlFilter sqlFilter = ThinkSqlFilter.build(targetClass)
                .eq("id",id);
        ThinkExecuteQuery query  =ThinkQuery.build(sqlFilter)
                .selectForKeys(targetClass,keys);
        return executeOne(query,finalTableName());
    }

    @Override
    public List<T> list(ThinkSqlFilter<T> sqlFilter) {
        List<Map<String, Object>> list = this.list(sqlFilter, "*");
        List<T> resultList = new ArrayList<>(list.size());
        for(Map<String, Object> map : list){
            T t= (T) ObjectUtil.mapToBean(map,targetClass);
            resultList.add(t);
        }
        return resultList;
    }

    @Override
    public <V extends BaseVo<T>> List<V> list(ThinkSqlFilter<T> sqlFilter, Class<V> voClass) {
        List<Map<String, Object>> list = this.list(sqlFilter,_DaoSupport.voKeys(targetClass,voClass));
        List<V> resultList = new ArrayList<>(list.size());
        for(Map<String, Object> map : list){
            V v= (V) ObjectUtil.mapToBean(map,voClass);
            resultList.add(v);
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> list(ThinkSqlFilter<T> sqlFilter, String... keys) {
        if(sqlFilter.mayBeEmptyResult()){
            return new ArrayList<>();
        }

        ThinkQuery query = ThinkQuery.build(sqlFilter);
        ThinkExecuteQuery executeQuery = query.selectForKeys(targetClass,keys) ;
        return executeSelectList(executeQuery,finalTableName());
    }

    @Override
    public long count(ThinkSqlFilter sqlFilter) {
        ThinkQuery query = ThinkQuery.build(sqlFilter);
        ThinkExecuteQuery executeQuery = query.countQuery(targetClass);
        Map<String,Object> map = executeOne(executeQuery,finalTableName());
        return (long) map.getOrDefault("COUNT_RESULT", 0L);
    }

    @Override
    public T findByThinkLinkedId(String thinkLinkedId) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(getTargetClass(), 1).eq("thinkLinkedId", thinkLinkedId);
        List<T> list = this.list(sqlFilter);
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }


}
