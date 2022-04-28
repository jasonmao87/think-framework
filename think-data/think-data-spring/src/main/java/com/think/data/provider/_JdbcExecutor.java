package com.think.data.provider;

import com.think.common.data.mysql.IThinkResultFilter;
import com.think.common.result.ThinkResult;
import com.think.common.util.ThinkMilliSecond;
import com.think.common.util.security.DesensitizationUtil;
import com.think.core.bean._Entity;
import com.think.data.Manager;
import com.think.data.ThinkDataRuntime;
import com.think.data.model.ThinkTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public abstract class _JdbcExecutor {
    public abstract <T extends _Entity> Class getTargetClass();
    public abstract JdbcTemplate getJdbcTemplate();

    private static String reportSqlTemplate = null;

    public Optional<ThinkDataRuntime> rt(){
        return Optional.ofNullable(Manager.getDataSrvRuntimeInfo()) ;
    }

    public <T extends _Entity> void executeTableInit(String tableName){

        this.checkTransactionAndLogPrint();
        if (Manager.isTableInitialized(tableName) == false) {
//            // TODO:  临时代码 ，协助系统自动完成 createUserName 和 updateUserName 字段的增加 ！  该代码应该在一定时间后予以删除 ！当然该代码有大概率执行不了，所以tryCatch掉
//            try{
//                String addCreateUserNameAndUpdateUserName  = "ALTER TABLE " + tableName + " " +
//                        "ADD COLUMN createUserName varchar(32) NOT NULL AFTER createUserId  ," +
//                        "ADD COLUMN updateUserName varchar(32) NOT NULL AFTER updateUserId " ;
//                getJdbcTemplate().update(addCreateUserNameAndUpdateUserName);
//            }catch (Exception e){
//
//            }



            try {

                String showTableExitsSql = "show tables like '" +tableName +"'";

                Map<String, Object> showTableExitsMap = new HashMap<>();
                try {
                    showTableExitsMap =getJdbcTemplate().queryForMap(showTableExitsSql);
                }catch (Exception r){

                }
                if (log.isTraceEnabled()) {
                    log.trace("检查表{}是否存在  --> {}::{}", tableName,showTableExitsSql ,showTableExitsMap);
                }
                if(showTableExitsMap.isEmpty()){
                    if(log.isTraceEnabled()){
                        log.trace("确定表{}不存在..",tableName);
                    }
                    String sql;
                    if (Manager.getModelBuilder().get(getTargetClass()).isYearSplitAble()) {
                        if (log.isTraceEnabled()) {
                            log.trace("即将创建表【时间拆分】 {}", tableName);
                        }
                        int splitYear = Integer.parseInt(tableName.split("_split_")[1]);
                        sql = ThinkDataDDLBuilder.createSpiltSQL(Manager.getModelBuilder().get(getTargetClass()), splitYear);
                    } else {
                        if (log.isTraceEnabled()) {
                            log.trace("即将创建普通表 {} ", tableName);
                        }
                        sql = ThinkDataDDLBuilder.createSQL(Manager.getModelBuilder().get(getTargetClass()));
                    }
                    long start = ThinkMilliSecond.currentTimeMillis();
                    getJdbcTemplate().update(sql);
                    long duration = ThinkMilliSecond.currentTimeMillis() - start;
                    Manager.recordTableInit(tableName);
                    if (log.isDebugEnabled()) {
                        log.debug("FINISH CREATE TABLE -> {}", tableName);
                    }
                    if (rt().isPresent()) {
                        rt().get().fireDDL(sql, duration);
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("表结构构建完成....");
                    }
                }else{
                    Manager.recordTableInit(tableName);



                    /*
                    //是否需要关联id支持
                    if(Manager.isThinkLinkedIdSupportAble()){
                        String key = "thinkLinkedId";
                        String checkKeyExitsSQL = "DESC tb_common_menu " +key;
                        Map<String, Object> checkKeyExitsMap = new HashMap<>();
                        try{
                            checkKeyExitsMap = getJdbcTemplate().queryForMap(checkKeyExitsSQL);
                        }catch (Exception e){}
                        if (log.isDebugEnabled()) {
                            log.debug("检查表{}是否存在{}  --> {}::{}", tableName,key,checkKeyExitsSQL ,checkKeyExitsMap);
                        }
                        if(checkKeyExitsMap.isEmpty()){
                            String initSQL = "ALTER TABLE  "+tableName+"  " +
                                    "ADD COLUMN "+key+" varchar(24) NOT NULL AFTER id, " +
                                    "ADD INDEX "+key+"("+key+") ";
                            getJdbcTemplate().update(initSQL);
                        }

                        CompletableFuture.runAsync(()->{
                           getJdbcTemplate().update("UPDATE " +tableName +" SET " + key +" = id where " +key + " = '' ");
                        });


                    }
                     */


                }






            }catch (Exception exception){
                if(log.isErrorEnabled()){
                    log.error("创建数据库表格抛出的异常信息: ",exception);
                }
            }
        }

        //必将执行初始化数据代码片段 ，每个服务，会执行，且执行一次 ！！！
        // 如果 表格 正常创建 ，那么调用初始化 程序
        List<ThinkExecuteQuery> queryList = _SimpleDataInitQueryBuilder.initDataQueryList(getTargetClass());
        for (ThinkExecuteQuery query : queryList) {
            try {
                ThinkResult result = this.executeUpdate(query, tableName);
                if (log.isDebugEnabled()) {
                    if (result.isSuccess()) {
                        log.debug("{}初始化数据,执行SQL: {}" ,tableName, query.getSql(tableName) );
                    }
                }

            }catch (Exception e){
                log.error("",e);

            }

        }
//
//        ThinkExecuteQuery init_query = _SimpleDataInitQueryBuilder.initData(getTargetClass());
//        if(init_query !=null) {
//            if (log.isDebugEnabled()) {
//                log.debug("初始化{} {}条初始化数据" ,tableName,init_query.getValues().length);
//            }
//            this.executeUpdate(init_query, tableName);
//        }else{
//        }
    }

    public void checkTransactionAndLogPrint(){
        if (log.isDebugEnabled()) {
            boolean actualTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            if(actualTransactionActive){
                String currentTransactionName = TransactionSynchronizationManager.getCurrentTransactionName();

                log.debug("**********************本次执有事务 -- {}********************************" ,currentTransactionName );
            }
        }
    }


    public Map<String,Object> executeOne( ThinkExecuteQuery executeQuery, String finalTableName){
        this.executeTableInit(finalTableName);

        Map<String,Object> result = null;
        if(executeQuery.isMayByEmpty()){
            result= new HashMap<>();
            if(executeQuery.getSql(finalTableName).toUpperCase().contains(" COUNT(*) ")){
                result.put("COUNT_RESULT",0L);
            }
            log.warn("SQL FILTER 存在 IN 空数据内容，不执行实际查询，直接返回 0 或者 空值 ");
            return result;
        }

        long duration = 0L;
        long start =0L;
        int affectedCount = 0;
        boolean success = false;
        Throwable throwable = null;
        String sql = executeQuery.getSql(finalTableName);


        try{
            start = ThinkMilliSecond.currentTimeMillis();
            result = getJdbcTemplate().queryForMap(sql,executeQuery.getValues());
            duration = ThinkMilliSecond.currentTimeMillis() - start;
            success = true;
            affectedCount =1 ;
        }catch (DataAccessException e){
            duration =ThinkMilliSecond.currentTimeMillis() - start;
            result = new HashMap<>();
            throwable = e;
        }finally {
            if(rt().isPresent()){
                rt().get().fireSelect(sql,success,affectedCount,duration,executeQuery.getValues()).throwInfo(throwable);
            }
            if(log.isDebugEnabled()){
                outputSQLInfo(sql,executeQuery.getValues(),success,affectedCount,duration);
                //log.debug("sql {}\n\t execute state ={} , execute duration :{} ms" ,sql,success,duration);
            }
        }
        if(result.isEmpty()){
            return result;
        }
        this.desensitizationDecode(result);
        List<IThinkResultFilter> resultFilters = executeQuery.getResultFilters();
        for (IThinkResultFilter resultFilter : resultFilters) {
            resultFilter.doFilter(result);
        }
        return result;
    }



    public List<Map<String,Object>> executeSelectList( ThinkExecuteQuery executeQuery, String finalTableName){
        if(executeQuery.isMayByEmpty()){
            log.warn("SQL FILTER 存在 IN 空数据内容，不执行实际查询，直接返回 0 或者 空值 ");
            return new ArrayList<>();
        }

        this.executeTableInit(finalTableName);
        List<Map<String,Object>> result = null;
        long duration = 0L;
        long start =0L;
        int affectedCount =0;
        boolean success = false;
        Throwable throwable = null;
        String sql = executeQuery.getSql(finalTableName);
        try{
            start = ThinkMilliSecond.currentTimeMillis();
            result = getJdbcTemplate().queryForList(sql,executeQuery.getValues());
            duration = ThinkMilliSecond.currentTimeMillis() - start;
            success =true;
            affectedCount = result.size();
        }catch (DataAccessException e){
            duration = ThinkMilliSecond.currentTimeMillis() - start;
            result = new ArrayList<>();
            throwable = e ;
        }finally {
            if(rt().isPresent()){
                rt().get().fireSelect(sql,success,affectedCount,duration,executeQuery.getValues()).throwInfo(throwable);
            }
            if(log.isDebugEnabled()){
                outputSQLInfo(sql,executeQuery.getValues(),success,affectedCount,duration);
                //log.debug("sql {}\n\t execute state ={} , execute duration :{} ms" ,sql,success,duration);
            }
        }

        List<IThinkResultFilter> resultFilters = executeQuery.getResultFilters();
        for(Map<String,Object> map : result){
            this.desensitizationDecode(map);
            for (IThinkResultFilter resultFilter : resultFilters) {
                resultFilter.doFilter(map);
            }

        }

        return result;
    }

    public ThinkResult executeUpdate(ThinkExecuteQuery executeQuery, String finalTableName){
        this.executeTableInit(finalTableName);
        long duration = 0 ;
        int result = 0;
        boolean success = false;
        Throwable throwable = null;
        if(executeQuery.isMayByEmpty()){
            return DaoExceptionTranslater.updateFilterEmpty().setData(0);
        }


        long start =0L;
        String sql = executeQuery.getSql(finalTableName);
        try{
            start =  ThinkMilliSecond.currentTimeMillis();
            result = getJdbcTemplate().update(sql,executeQuery.getValues());
            duration = ThinkMilliSecond.currentTimeMillis()- start;
            success = true;
            return ThinkResult.success(result);
        }catch (DataAccessException e){
            duration =ThinkMilliSecond.currentTimeMillis()- start;
            result =  -1;
            throwable = e ;
            return DaoExceptionTranslater.translate(e);
        }finally {
            if(rt().isPresent()){
                if(executeQuery.isInsert()){
                    rt().get().fireInsert(sql,success,result,duration,executeQuery.getValues()).throwInfo(throwable);
                }else if(executeQuery.isUpdate()){
                    rt().get().fireUpdate(sql,success,result,duration,executeQuery.getValues()).throwInfo(throwable);
                }else if(executeQuery.isDelete()){
                    rt().get().fireDelete(sql,success,result,duration,executeQuery.getValues()).throwInfo(throwable);
                }
            }
            if(log.isDebugEnabled()){
                outputSQLInfo(sql,executeQuery.getValues(),success,result,duration);
                /*
                log.debug("sql {}\n\t execute state ={} , execute duration :{} ms" ,sql,success,duration);

                log.debug("format sql  > ");

                 */
            }
        }
    }




    public void desensitizationDecode(Map<String,Object> map){
        ThinkTableModel tableModal = Manager.getModelBuilder().get(getTargetClass());
        try {
            for (String k : map.keySet()) {
                if (map.containsKey(k)) {
                    if (tableModal.getKey(k).isSensitive()) {
                        String v = DesensitizationUtil.decode((String) map.get(k));
                        map.put(k, v);
                    }
                }
            }
            //标记为 已经持久化的数据！！
            map.put("dbPersistent",true);
        }catch (Exception e){}
    }


    /**
     *
     * @param sql           sql
     * @param values        values
     * @param successState       执行是否成功
     * @param duration      持续时长
     * @param affectedCount 影响行数
     */
    public static final void outputSQLInfo(String sql , Serializable[] values , boolean successState , int affectedCount,long duration  ) {
        if (log.isDebugEnabled() && Manager.sqlPrintAble()) {
            if(Manager.isPrintExecutableSql()){
                outputExecutableSql(sql,values,successState,affectedCount,duration);
            }else {

                if (values != null && values.length > 0) {
                    for (Serializable v : values) {
                        String typeValue = "";
                        try {
                            typeValue = v.getClass().getSimpleName() ;// .getTypeName();
                        } catch (Exception e) {
                        }
                        String tv = v!=null?v.toString():null ;
                        try {
                            String temp  =sql.replaceFirst("\\?", "[type :: " + typeValue + " ,value :: " + tv + "]");
                            sql = temp;
                        }catch (Exception e){
                            sql = sql.replaceFirst("\\?", "[type :: " + typeValue + " ,value :: [Think-Data: JDK放弃解析的值TEXT] ]");
                        }
                        sql += "\n\t\t";
                    }
                }
                doPrint(sql,successState,affectedCount,duration);
            }

        }
    }



    public static final void outputExecutableSql(String sql , Serializable[] values , boolean successState,int affectedCount, long duration){
        if(values !=null && values.length > 0){
            for(Serializable v : values){
                sql = sql.replaceFirst("\\?",computeParamValue(v));
            }
        }
        doPrint(sql,successState,affectedCount,duration);

    }

    public static final void doPrint(String sql ,  boolean successState,int affectedCount, long duration){
        if (log.isDebugEnabled()) {
            if(reportSqlTemplate == null) {
                StringBuilder reportForPrint = new StringBuilder("")
                        .append("EXECUTED SQL :\n\t\t{}\n").append("\n")    // param:  sql
                        .append("EXECUTED RESULT REPORT >> STATE:{}  ROW AFFECTED :{} DURATION（毫秒）:{}\n") ;
                reportSqlTemplate = reportForPrint.toString().intern();
            }
            sql = sql.replaceFirst("where", "WHERE").replaceFirst("WHERE", "WHERE\n\t\t\t");
            if (log.isDebugEnabled()) {
                log.debug(reportSqlTemplate, sql, successState , affectedCount , duration);
            }
        }

    }


    public static final String computeParamValue(Serializable v){
        if(v instanceof Boolean ){
            return ((Boolean)v).booleanValue()?"1":"0";
        }
        if (v instanceof String){
            return "'"+v+"'";
        }
        if(v instanceof Date){
            StringBuilder vb = new StringBuilder("'").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( (Date) v))
                    .append("'");
            return vb.toString() ;

        }
        return v +"";
    }


}
