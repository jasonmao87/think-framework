package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimpleRefEntity;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ObjectUtil;
import com.think.data.dao.ThinkSplitRefDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class ThinkSplitRefDaoProvider<T extends SimpleRefEntity> extends _JdbcExecutor implements ThinkSplitRefDao<T> {


    private long lastCheckDb = 0L ;
    private Class<T> targetClass ;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public <T extends _Entity> Class getTargetClass() {
        return targetClass;
    }

    public ThinkSplitRefDaoProvider() {
        Type superClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            this.targetClass = (Class<T>) ((ParameterizedType) type).getRawType();
        } else {
            this.targetClass = (Class<T>) type;
        }
    }

    public ThinkSplitRefDaoProvider(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public String finalTableName(int  splitYear){
        return _DaoSupport.baseTableName( targetClass) + "_split_" + splitYear;

//        return _DaoSupport.baseTableName( targetClass) + "_" + splitYear;
    }

    @Override
    public List<String> showSplitTables() {
        List<String> showSplitTables = _showSplitTables(jdbcTemplate, targetClass);
        return showSplitTables;

//
//
//        if(ThinkMilliSecond.currentTimeMillis() - lastCheckDb   > (1000*60*5)) {
//            String showtables = "SHOW TABLES LIKE '" + _DaoSupport.baseTableName( targetClass) + "%'";
//            List<String> list = jdbcTemplate.queryForList(showtables, String.class);
//            for (String t : list) {
//                if (Manager.isTableInitialized(t) == false) {
//                    Manager.recordTableInit(t);
//                }
//            }
//            lastCheckDb =ThinkMilliSecond.currentTimeMillis();
//            return list;
//        }else{
//            return Manager.findInitializedTableNameList(_DaoSupport.baseTableName(targetClass));
//        }
    }
    @Override
    public Class<T> targetClass() {
        return targetClass;
    }



    @Override
    public T findOne(long id, long rootPrimaryId) {
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass)
                .eq("id",id)
                .eq("rootPrimaryId" , rootPrimaryId);
        ThinkExecuteQuery query  = ThinkQuery.build(sqlFilter).selectFullKeys(targetClass);
        Map map= executeOne(query,finalTableName(splitYear));
        if(map == null || map.isEmpty()) {
            return null;
        }
        return (T) ObjectUtil.mapToBean(map,targetClass);
    }

    @Override
    public T findDeleted(long id, long rootPrimaryId) {
        return findOne(-id,rootPrimaryId);
    }

    @Override
    public long count(long rootPrimaryId) {
       return this.count(ThinkSqlFilter.build(targetClass),rootPrimaryId);
    }

    @Override
    public long count(ThinkSqlFilter<T> sqlFilter, long rootPrimaryId) {
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        sqlFilter.eq("rootPrimaryId" ,rootPrimaryId);
        ThinkQuery query = ThinkQuery.build(sqlFilter);
        ThinkExecuteQuery executeQuery = query.countQuery(targetClass ) ;
        Map<String,Object> map = executeOne(ThinkQuery.build(sqlFilter).selectCount(),finalTableName(splitYear));
        return (long) map.getOrDefault("COUNT_RESULT", 0L);
    }

    @Override
    public List<T> list(long rootPrimaryId, int limit) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass,limit)
                .eq("rootPrimaryId" , rootPrimaryId) ;
        return this.list(sqlFilter,rootPrimaryId);
    }

    @Override
    public List<T> list(ThinkSqlFilter<T> sqlFilter, long rootPrimaryId) {
        List<Map<String, Object>> list = this.mapList(sqlFilter,rootPrimaryId);
        List<T> result = new ArrayList<T>();
        for(Map<String,Object> map : list){
            result.add((T) ObjectUtil.mapToBean(map,targetClass));
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> mapList(ThinkSqlFilter<T> sqlFilter, long rootPrimaryId) {
        sqlFilter.eq("rootPrimaryId", rootPrimaryId);
        ThinkQuery query = ThinkQuery.build(sqlFilter);
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        ThinkExecuteQuery executeQuery = query.selectFullKeys(targetClass);
        List<Map<String, Object>> list = executeSelectList(executeQuery, finalTableName(splitYear));
        return list;
    }

    @Override
    public ThinkResult<T> insert(T t) {
        _DaoSupport.callInsert(t);
        long rootPrimaryId =  t.getRootPrimaryId() ;
        if(rootPrimaryId <1 ){
            return ThinkResult.fail("拒绝操作,必指定主类Id（rootPrimaryId）", ResultCode.REQUEST_PARAM_ERROR);
        }
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.insertOneSQL(t);
        ThinkResult result = executeUpdate(query, finalTableName(_DaoSupport.computeSpiltYearById(rootPrimaryId)));
        if(result.isSuccess()){
            ObjectUtil.setDbPersistent(t);
            return ThinkResult.success(t);
        }
        return result;
    }

    @Override
    public ThinkResult<Integer> batchInsert(List<T> list) {
        if(list.size() <1){
            return ThinkResult.fail("无可插入数据", ResultCode.REQUEST_PARAM_ERROR);
        }
        long t_rootPrimaryId = list.get(0).getRootPrimaryId();
        for(T t : list){
            if(t.getRootPrimaryId() != t_rootPrimaryId){
                return ThinkResult.fail("非法的数据，从类型批量插入，必须保证rootPrimaryId相等", ResultCode.REQUEST_PARAM_ERROR);
            }
        }
        _DaoSupport.callInsert(list);
        ThinkExecuteQuery query =ThinkUpdateQueryBuilder.batchInsertSQL(list);
        int splitYear = _DaoSupport.computeSpiltYearById(t_rootPrimaryId);
        return executeUpdate(query,finalTableName(splitYear));
    }

    @Override
    public ThinkResult<Integer> update(T t) {
        _DaoSupport.callUpdate(t,null);
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.updateSql(t);
        int splitYear = _DaoSupport.computeSpiltYearById(t.getRootPrimaryId());
        return executeUpdate(query,finalTableName(splitYear));
    }

    @Override
    public <V extends BaseVo<T>> ThinkResult<Integer> update(V v, long id, long rootPrimaryId) {
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass)
                .eq("id",id)
                .eq("rootPrimaryId",rootPrimaryId) ;
        Map<String,Object> voMap = ObjectUtil.beanToMap(v);


        ThinkUpdateMapper<T> updaterMapper = ThinkUpdateMapper.build(targetClass)
                .putUpdateMap(voMap)
                .setFilter(sqlFilter);
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        return this.update(updaterMapper,splitYear);

     }

    @Override
    public ThinkResult<Integer> update(ThinkUpdateMapper<T> updaterMapper, long rootPrimaryId) {
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        _DaoSupport.callUpdate(null,updaterMapper);
        return this.executeUpdate(ThinkUpdateQueryBuilder.updateSql(updaterMapper),finalTableName(splitYear)) ;
    }

    @Override
    public ThinkResult<Integer> delete(long id, long rootPrimaryId) {
        ThinkUpdateMapper<T> updaterMapper = ThinkUpdateMapper.build(targetClass)
                .updateValue("id",-id)  ;
        /*
        //id 调整为 -id
//                .updateValue("deleteState",true)
//                .updateValue("deleteTime" , DateUtil.now());

         */
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass)
                .eq("id",id)
                .eq("rootPrimaryId",rootPrimaryId)
                .largeThan("id",0);

        updaterMapper.setFilter(sqlFilter);
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        return this.executeUpdate(ThinkUpdateQueryBuilder.updateSql(updaterMapper),finalTableName(splitYear)) ;
    }

    @Override
    public ThinkResult<Integer> batchDelete(long[] ids, long rootPrimaryId) {
        ThinkUpdateMapper<T> updaterMapper = ThinkUpdateMapper.build(targetClass)
                //id 调整为 -id
                .updateToKeyValue("id","-id");
//                .updateValue("deleteState",true)
//                .updateValue("deleteTime" , DateUtil.now());
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass)
                .in("id", ids)
                .eq("rootPrimaryId",rootPrimaryId)
                .largeThan("id",0);
        updaterMapper.setFilter(sqlFilter);
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        return this.executeUpdate(ThinkUpdateQueryBuilder.updateSql(updaterMapper),finalTableName(splitYear)) ;
     }

    @Override
    public ThinkResult<Integer> physicalDelete(long id, long rootPrimaryId) {
        return physicalDelete(new Long[]{id},rootPrimaryId);
    }

    @Override
    public ThinkResult<Integer> physicalDelete(Long[] ids, long rootPrimaryId) {
        int splitYear = _DaoSupport.computeSpiltYearById(rootPrimaryId);
        ThinkSqlFilter sqlFilter = ThinkSqlFilter.build(getTargetClass(),-1)
                .in("id",ids);
        ThinkExecuteQuery query = ThinkUpdateQueryBuilder.physicalDeleteSql(sqlFilter);
        return this.executeUpdate(query,finalTableName(splitYear));
    }

    @Override
    public ThinkResult<Integer> physicalDelete(long[] ids, long rootPrimaryId) {
        Long[] longIds = new Long[ids.length];
        for (int i = 0; i < ids.length; i++) {
            longIds[i] = ids[i];
        }
        return physicalDelete(longIds,rootPrimaryId);
    }
//
//    public static void main(String[] args) {
//
//        String sqlFilter = "filter: {\n" +
//                "\t\"limit\": 10,\n" +
//                "\t\"sortQuery\": {\"key\": \"id\" ,\"sort\": \"desc\"}," +
//                "   keyOrBody:{" +
//                "      id : 1 , id2 : 2 " +
//                "   }\n" +
//                "\t\"filterBody\": {\n" +
//                "\t\t\"id\": {\n" +
//                "\t\t\t\"op\": \"LG\",\n" +
//                "\t\t\t\"type\": \"number\",\n" +
//                "\t\t\t\"v\": 0\n" +
//                "\t\t}\n" +
//                "\t} \n" +
//                "}";
//
//        System.out.println("xx");
//        ThinkSqlFilter<SimplePrimaryEntity> simplePrimaryEntityThinkSqlFilter = ThinkSqlFilter.parseFromJSON(sqlFilter, SimplePrimaryEntity.class);
//
//        System.out.println(simplePrimaryEntityThinkSqlFilter.toString());
//        System.out.println("Xx");
//    }

}
