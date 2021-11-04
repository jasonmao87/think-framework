package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.common.util.IdUtil;
import com.think.common.util.StringUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ObjectUtil;
import com.think.data.Manager;
import com.think.data.dao.ThinkSplitPrimaryDao;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public abstract class ThinkSplitDaoProvider<T extends SimplePrimaryEntity> extends _JdbcExecutor implements ThinkSplitPrimaryDao<T> {

    private JdbcTemplate jdbcTemplate;
    private Class<T> targetClass ;
    private long lastCheckDb = 0L ;
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.showSplitTables();
    }

    @Override
    public <T extends _Entity> Class getTargetClass() {
        return targetClass;
    }

    @Override
    public Class<T> targetClass() {
        return targetClass;
    }

    @Override
    public int computeSplitYearById(long id) {
        return _DaoSupport.computeSpiltYearById(id);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    public ThinkSplitDaoProvider(){
        Type superClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            this.targetClass = (Class<T>) ((ParameterizedType) type).getRawType();
        } else {
            this.targetClass = (Class<T>) type;
        }
        if(!Manager.getModelBuilder().get(targetClass).isYearSplitAble()){
            throw new ThinkRuntimeException(targetClass.getName() +" 不是是时间分割对象，请使用 com.think.data.dao.ThinkDao");
        }
    }

    public ThinkSplitDaoProvider(Class<T> targetClass) {
        this.targetClass = targetClass;
        if(!Manager.getModelBuilder().get(targetClass).isYearSplitAble()){
            throw new ThinkRuntimeException(targetClass.getName() +" 不是是时间分割对象，请使用 com.think.data.dao.ThinkDao");
        }
    }

    public String finalTableName(int  splitYear){
        return _DaoSupport.baseTableName( targetClass) + "_split_" + splitYear;
    }

    @Override
    public List<String> showSplitTables() {
//        log.info("检查 split 表清单  {} ， {}" ,ThinkMilliSecond.currentTimeMillis() , lastCheckDb );
        if(ThinkMilliSecond.currentTimeMillis() - lastCheckDb   > (1000*60*5)) {

            String showTablesSql = "SHOW TABLES LIKE '" + _DaoSupport.baseTableName( targetClass) + "%'";
//            log.info("{}",showTablesSql);
            List<String> list = jdbcTemplate.queryForList(showTablesSql, String.class);
            for (String t : list) {
                if (Manager.isTableInitialized(t) == false) {
                    Manager.recordTableInit(t);
                }
            }
            if(!showTablesSql.contains("nonePart")) {
                lastCheckDb = ThinkMilliSecond.currentTimeMillis();
            }
            return list;
        }else{
            return Manager.findInitializedTableNameList(_DaoSupport.baseTableName(  targetClass));
        }
    }


    @Override
    public T findOne(long id) {
        long k = id>0?id:-id;
        int splitYear = _DaoSupport.computeSpiltYearById(k);
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass)
                .eq("id",id);
        ThinkExecuteQuery query  = ThinkQuery.build(sqlFilter).selectFullKeys(targetClass);
        Map map= executeOne(query,finalTableName(splitYear));
        if(map==null ||map.isEmpty()){
            return null;
        }
        return (T) ObjectUtil.mapToBean(map,targetClass);
    }

    @Override
    public T findDeleted(long id) {
        return findOne(-id);
    }

    @Override
    public <V extends BaseVo> V findOne(long id, Class<V> voClass) {
        int splitYear = _DaoSupport.computeSpiltYearById(id);
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass)
                .eq("id",id);
        ThinkExecuteQuery query  = ThinkQuery.build(sqlFilter).selectForVo(targetClass,voClass);
        Map map= executeOne(query,finalTableName(splitYear));
        if(map == null || map.isEmpty()) {
            return null;
        }
        return (V) ObjectUtil.mapToBean(map,voClass);
     }

    @Override
    public <V extends BaseVo> List<V> autoVoList(ThinkSqlFilter<T> sqlFilter ,Class<V> voClass) {
        List<V> result = new ArrayList<>();
        List<Map<String,Object>> list = this._autoMapList(sqlFilter,_DaoSupport.voKeys(targetClass,voClass));
        if(list.size() > 0){
            for(Map<String,Object> map : list){
                result.add((V) ObjectUtil.mapToBean(map,voClass));
            }
        }
        return result;
    }

    @Override
    public List<T> autoList(ThinkSqlFilter<T> sqlFilter)   {
        List<Map<String,Object>> list = this._autoMapList(sqlFilter,"*");
        List<T> result = new ArrayList<>(list.size());
        if(list.size() > 0){
            for(Map<String,Object> map : list){
                result.add((T) ObjectUtil.mapToBean(map,targetClass));
            }
        }
        return result;
    }

    public List<Map<String,Object>> _autoMapList(ThinkSqlFilter<T> sqlFilter ,String... keys) {
        int[] possibleSplits = _DaoSupport.possibleSplitYears(sqlFilter ,this.showSplitTables());
        if(possibleSplits.length == 0){
            return new ArrayList<Map<String,Object>>();
        }
        int limit = sqlFilter.getLimit();
        List<Map<String,Object>> results = new ArrayList<Map<String,Object>>( limit>0?limit:12 );
        List<Map<String,Object>> list = this._simpleMapList(sqlFilter,possibleSplits[0],keys);
        results.addAll(list);

        int loop = 1 ;
        while (limit> results.size() && possibleSplits.length > loop ){
            Long lastId = null;
            if(results.size() > 0){
                lastId = (Long) results.get(results.size() -1).get("id");
            }
            sqlFilter.updateLimit( limit - list.size());
            if(sqlFilter.isDesc()){
                sqlFilter.lessThan("id",lastId);
            }else{
                sqlFilter.largeThan("id",lastId);
            }
            List<Map<String,Object>> loopList = this._simpleMapList(sqlFilter,possibleSplits[loop],keys);
            results.addAll(loopList);
            loop ++ ;
        }
        return results;
    }


    @Override
    public List<T> simpleList(ThinkSqlFilter<T> sqlFilter, int splitYear) {
        List<Map<String,Object>> list = this._simpleMapList(sqlFilter,splitYear,"*");
        List<T> result = new ArrayList<T>(list.size());
        if(list.size() > 0){
            for(Map<String,Object> map : list){
                result.add((T) ObjectUtil.mapToBean(map,targetClass));
            }
        }
        return result;
    }

    public List<Map<String,Object>> _simpleMapList(ThinkSqlFilter<T> sqlFilter, int splitYear,String... keys){
        ThinkQuery query = ThinkQuery.build(sqlFilter);
        ThinkExecuteQuery executeQuery = query.selectForKeys(targetClass ,keys ) ;
        List<Map<String,Object>> list = this.executeSelectList(executeQuery,finalTableName(splitYear));
        return list;
    }


    @Override
    public long autoCount(ThinkSqlFilter<T> sqlFilter)  {
        int[] possibleSplits = _DaoSupport.possibleSplitYears(sqlFilter,this.showSplitTables());
        long total = 0L ;
        for(int splitYear : possibleSplits){
            total += simpleCount(sqlFilter,splitYear);
        }
        return total;
    }

    @Override
    public long simpleCount(ThinkSqlFilter<T> sqlFilter, int splitYear) {
        ThinkQuery query = ThinkQuery.build(sqlFilter);
        ThinkExecuteQuery executeQuery = query.countQuery(targetClass ) ;
        Map map = this.executeOne(executeQuery,finalTableName(splitYear));
        return (long) map.getOrDefault("COUNT_RESULT", 0L);
    }

    @Override
    public ThinkResult<T> insert(T t) {
        _DaoSupport.callInsert(t);
        if(t.getId() == null || t.getId()<1){
            t.setId(IdUtil.nextId());
        }
        int splitYear = _DaoSupport.computeSpiltYearById(t.getId());
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.insertOneSQL(t);
        try {
            ThinkResult result = this.executeUpdate(query, finalTableName(splitYear));
            if(result.isSuccess()){
                ObjectUtil.setDbPersistent(t);
                return ThinkResult.success(t);
            }
            return result;
        }catch (DataAccessException e){
            return DaoExceptionTranslater.translate(e);
        }

    }

    public ThinkResult<Integer> _update(ThinkExecuteQuery query,int splitYear )  {
        return executeUpdate(query,finalTableName(splitYear));
    }


    @Override
    public ThinkResult<Integer> update(T t) {
        _DaoSupport.callUpdate(t,null);
        int splitYear = _DaoSupport.computeSpiltYearById(t.getId());
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.updateSql(t);
        return this._update(query,splitYear);
    }

    @Override
    public ThinkResult<Integer> update(ThinkUpdateMapper<T> updaterMapper, int splitYear) {
        _DaoSupport.callUpdate(null,updaterMapper);
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.updateSql(updaterMapper);
        return _update(query,splitYear);
    }


    @Override
    public <V extends BaseVo<T>> ThinkResult<Integer> update(V v, long id) {
        //调用链已经有了  _DaoSupport.callUpdate(null,updaterMapper); ，所以无需再次调用
        Map<String,Object> voMap = ObjectUtil.beanToMap(v);
        ThinkUpdateMapper<T> updateMapper = ThinkUpdateMapper.build(getTargetClass())
                .putUpdateMap(voMap)
                .setTargetDataId(id);
        int splitYear = _DaoSupport.computeSpiltYearById(id);
        return update(updateMapper,splitYear);
    }

    @Override
    public <V extends BaseVo<T>> ThinkResult<Integer> update(V v, ThinkSqlFilter<T> sqlFilter) {
        //调用链已经有了  _DaoSupport.callUpdate(null,updaterMapper); ，所以无需再次调用
        int[] possibleSplits = _DaoSupport.possibleSplitYears(sqlFilter ,this.showSplitTables());
        Map<String,Object> voMap = ObjectUtil.beanToMap(v);
        ThinkUpdateMapper<T> updateMapper = ThinkUpdateMapper.build(getTargetClass())
                .putUpdateMap(voMap)
                .setFilter(sqlFilter);

        int  updateCount = 0 ;
        for (int i = 0; i < possibleSplits.length; i++) {
            int splitYear = possibleSplits[i];
            ThinkResult<Integer> oneResult = this.update(updateMapper,splitYear);
            if(oneResult.isSuccess()){
                updateCount+= oneResult.getResultData();
            }


        }
        if(updateCount >0){
            return ThinkResult.success(updateCount);
        }

        return ThinkResult.fastFail();
    }

    @Override
    public ThinkResult<Integer> batchInsert(List<T> list) {
        _DaoSupport.callInsert(list);
        // set 中 用 int 不安全
        Map<String ,List<T>> waitMap =new HashMap<>();
        for(T t : list){
            if(t.getId() == null || t.getId()<1){
                t.setId(IdUtil.nextId());
            }
            int splitYear = _DaoSupport.computeSpiltYearById(t.getId());
            String k =String.valueOf(splitYear) ;
            if(!waitMap.containsKey(k)){
                List<T> tList = new ArrayList<>();
                tList.add(t);
                waitMap.put(k,tList);
            }else {
                List<T> tList = waitMap.get(k);
                tList.add(t);
                waitMap.put( k , tList);
            }
        }
        int total  = 0 ;
        for(String k : waitMap.keySet()){
            List<T> tempList = waitMap.get(k);
            if(tempList.size() >1) {
                ThinkResult<Integer> result = this._batchInsert(tempList, Integer.parseInt(k));
                if (result.isSuccess()) {
                    total += result.getResultData();
                }
            }else if(tempList.size() == 1){
                ThinkResult<T> result = this.insert(tempList.get(0));
                if(result.isSuccess()) {
                    total += 1;
                }
            }
        }
        if(total>0){
            return ThinkResult.success(total);
        }
        return ThinkResult.fail("批量插入失败，插入0条数据",ResultCode.SERVER_ERROR);
    }

    public ThinkResult<Integer> _batchInsert(List<T> list ,int splitYear){
        if(list.size() >0) {
            if(list.size() >1) {
                ThinkExecuteQuery query = ThinkUpdateQueryBuilder.batchInsertSQL(list);
                return this.executeUpdate(query, finalTableName(splitYear));
            }else{
                return this.insert(list.get(0)).intResult();
            }
        }
        return ThinkResult.fail("无可插入数据", ResultCode.REQUEST_PARAM_ERROR);
    }

    @Override
    public ThinkResult<Integer> delete(long id) {
        return batchDelete(new long[]{id});
    }

    @Override
    public ThinkResult<Integer> batchDelete(long[] ids) {
        Map<String ,List<Long>> waitMap =new HashMap<>();
        for(long id : ids){
            int splitYear = _DaoSupport.computeSpiltYearById(id);
            String k = String.valueOf(splitYear );
            if(!waitMap.containsKey(k)){
                List<Long> tList = new ArrayList<>();
                tList.add(id);
                waitMap.put(k,tList);
            }else {
                List<Long> tList = waitMap.get(k);
                tList.add(id);
                waitMap.put( k , tList);
            }
        }
        int total = 0;
        for(String k : waitMap.keySet()){
            ThinkResult<Integer> result = this._batchDelete(waitMap.get(k),Integer.parseInt(k));
            if(result.isSuccess()){
                total += result.getResultData();
            }
        }

        if(total>0){
            return ThinkResult.success(total);
        }else {
            return ThinkResult.success(0).appendMessage("未删除任何数据");
            //return ThinkResult.fail("未匹配到任何数据，或其他错误",ResultCode.REQUEST_NO_RESOURCE);
        }
    }

    public ThinkResult _batchDelete(List<Long> idList,int splitYear){
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass);
        if(idList.size() ==0){
            return ThinkResult.success(0);
//            return ThinkResult.fail("非法的参数，必须指定id",ResultCode.SERVER_FORBIDDEN);
        }else if(idList.size()>1){
            sqlFilter.in("id",idList.toArray(new Long[idList.size()]));
        }else{
            sqlFilter.eq("id",idList.get(0));
        }
        sqlFilter.largeThan("id",0);
        ThinkUpdateMapper<T> updaterMapper = ThinkUpdateMapper.build(targetClass)
                //id 调整为 -id
                .updateToKeyValue("id","-id")
                .setFilter(sqlFilter);
//                .updateValue("deleteState" ,true)
//                .updateValue("deleteTime", DateUtil.now());
        return this.update(updaterMapper,splitYear);
    }

    /** 以下为新增，可能存在 一些异常 ？、？、 */

    @Override
    public ThinkResult<Integer> physicalDelete(long id) {
        return physicalDelete(new Long[]{id});
    }

    @Override
    public ThinkResult<Integer> physicalDelete(Long[] ids) {
        Map<String ,List<Long>> waitMap =new HashMap<>();
        for(long id : ids){
            int splitYear = _DaoSupport.computeSpiltYearById(id);
            String k = String.valueOf(splitYear);
            if(!waitMap.containsKey(k)){
                List<Long> tList = new ArrayList<>();
                tList.add(id);
                waitMap.put(k,tList);
            }else {
                List<Long> tList = waitMap.get(k);
                tList.add(id);
                waitMap.put( k , tList);
            }
        }
        int total = 0;
        for(String k : waitMap.keySet()){
            ThinkResult<Integer> result = this._physicalBatchDelete(waitMap.get(k),Integer.parseInt(k));
            if(result.isSuccess()){
                total += result.getResultData();
            }
        }
        if(total>0){
            return ThinkResult.success(total);
        }else {
            return ThinkResult.success(1).appendMessage("未删除任何数据");
            //return ThinkResult.fail("未匹配到任何数据，或其他错误",ResultCode.REQUEST_NO_RESOURCE);
        }
    }

    @Override
    public ThinkResult<Integer> physicalDelete(long[] ids) {
        Long[] longIds = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
            longIds[i] = ids[i];
        }
        return physicalDelete(longIds);
    }


    public ThinkResult _physicalBatchDelete(List<Long> idList,int splitYear){
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass);
        if(idList.size() ==0){
            return ThinkResult.success(0).appendMessage("未删除任何数据");
//            return ThinkResult.fail("非法的参数，必须指定id",ResultCode.SERVER_FORBIDDEN);
        }else if(idList.size()>1){
            sqlFilter.in("id",idList.toArray(new Long[idList.size()]));
        }else{
            sqlFilter.eq("id",idList.get(0));
        }
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.physicalDeleteSql(sqlFilter);
        return this._update(query,splitYear);
    }
}
