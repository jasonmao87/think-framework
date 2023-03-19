package com.think.data.provider;

import com.think.common.util.StringUtil;
import com.think.core.bean._Entity;
import com.think.data.Manager;
import com.think.data.extra.StructAlterSqlLogger;
import com.think.data.model.MysqlStructColumnBean;
import com.think.data.model.ThinkColumnModel;
import com.think.data.model.ThinkTableModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/8/25 17:16
 * @description : 同步表结构工具
 */
@Slf4j
public class _SyncTableStructureUtil {
    private StructAlterSqlLogger structAlterSqlLogger = null;

    public StructAlterSqlLogger getAlterLogger(JdbcTemplate jdbcTemplate){
        if(structAlterSqlLogger != null){
            return structAlterSqlLogger;
        }else{
            structAlterSqlLogger = new StructAlterSqlLogger(jdbcTemplate);
            return structAlterSqlLogger;
        }
    }

    protected static  _SyncTableStructureUtil syncUtil = new _SyncTableStructureUtil();

    public <T extends _Entity> void doExecuteSync(Class<T> tClass,String tableName ,JdbcTemplate template){
        try {
            List<MysqlStructColumnBean> list = readStructure(template, tableName, tClass);
            List<ThinkColumnModel> newKeys = newKeys(list, tClass);
            if(newKeys.size() > 0) {
                log.info("需要调整{}的表结构，需要新增{}个字段 ",tableName,newKeys.size());
                String alterSql = this.alterSql(newKeys, tableName);
                String reverseSql = this.reverseSql(newKeys,tableName);
                log.info("即将执行结构调整脚本SQL---->>:  {}" , alterSql );
                log.info("如果遇到错误需要回滚的SQL-->>:  {}" ,reverseSql);
                int updateResult = template.update(alterSql);
                getAlterLogger(template).afterAlter(tClass,tableName,alterSql,reverseSql);

                log.info("执行结果 ： the number of rows affected = {}" ,updateResult);
            }else{
                log.info("无需调整{}的表结构",tableName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public final  <T extends _Entity> List<MysqlStructColumnBean> readStructure(JdbcTemplate template, String tableName , Class<T> tClass) {
        List<MysqlStructColumnBean> list = new ArrayList<>();
        final List<Map<String, Object>> maps = template.queryForList("show COLUMNS from  " + tableName);
        for (Map<String, Object> map : maps) {
//            log.info("readStructure ----of  ----- {}  >>> {}" ,tableName,map);
            MysqlStructColumnBean bean = new MysqlStructColumnBean(map);
            list.add(bean);
        }
        return list;
    }

    public final  <T extends _Entity>   List<ThinkColumnModel> newKeys(List<MysqlStructColumnBean> sourceKeys,Class<T> tClass){
        ThinkTableModel thinkTableModel = Manager.getModelBuilder().get(tClass);
        List<ThinkColumnModel> newKeysList =new ArrayList<>();
        for (ThinkColumnModel columnModel : thinkTableModel.getColumnModels()) {
            boolean contains = false;
            for (MysqlStructColumnBean sourceKey : sourceKeys) {
                if (sourceKey.getField().equalsIgnoreCase(columnModel.getKey())) {
                    contains =true;
                }
            }
            if(contains ==false) {
                newKeysList.add(columnModel);
            }
        }
        return newKeysList;
    }


    public final String reverseSql(List<ThinkColumnModel> newKeys ,String tableName){
        StringBuilder sql = new StringBuilder("ALTER TABLE ")
                .append(tableName).append(" ");
        for (int i = 0; i < newKeys.size(); i++) {
            if(i>0) {
                sql.append(" ,");
            }
            ThinkColumnModel columnModel = newKeys.get(i);
            sql.append(" DROP COLUMN  '").append(columnModel.getKey()).append("'");
        }
        sql.append(" ");
        return sql.toString();
    }


    public final String alterSql(List<ThinkColumnModel> newKeys ,String tableName){
        StringBuilder sql = new StringBuilder("ALTER TABLE ")
                .append(tableName).append(" ");
        for (int i = 0; i < newKeys.size(); i++) {
            if(i>0) {
                sql.append(" ,");
            }
            ThinkColumnModel columnModel = newKeys.get(i);

            sql.append(" ADD COLUMN ").append(columnModel.getKey()).append(" ")
                    .append(columnModel.getSqlTypeString()).append(" ");
            if(StringUtil.isNotEmpty(columnModel.getComment())){
                sql.append(" COMMENT '").append(columnModel.getComment()).append("' ");
            }
            if(columnModel.isNullable() == false){
                sql.append(" DEFAULT ").append( columnModel.getDefaultValueForAlterAndCreate()).append(" ");
            }else{
            }
        }

        sql.append(" ");

//        if (log.isDebugEnabled()) {
//            log.debug("同步表结构 ---for {} ，sql = {}",tableName,sql);
//        }
        return sql.toString();
    }




}
