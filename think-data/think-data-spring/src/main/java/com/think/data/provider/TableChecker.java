package com.think.data.provider;

import com.think.core.enums.DbType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TableChecker {


    /**
     * 检查表是否存在的
     * @param jdbcTemplate
     * @param tableName
     * @param dbType
     * @return
     */
    protected static boolean exitsTable(JdbcTemplate jdbcTemplate,String tableName, DbType dbType){
        if (log.isTraceEnabled()) {
            log.trace("--检查是否存在 {}--------类型{}",tableName ,dbType);
        }
        if (dbType.realType() == DbType.DM) {
            return dmExitsTable(jdbcTemplate,tableName);
        }
        return mysqlExitsTable(jdbcTemplate,tableName);
    }

    /**
     * 读取按年切分的表清单
     * @param jdbcTemplate
     * @param tablePrefix
     * @param dbType
     * @return
     */
    protected static List<String> showSplitTables(JdbcTemplate jdbcTemplate,String tablePrefix,DbType dbType){
        if (log.isTraceEnabled()) {
            log.trace("showSplitTables for {}------------{}" ,tablePrefix,dbType);
        }
        if (dbType.realType() == DbType.DM) {
            return dmShowSplitTables(jdbcTemplate,tablePrefix);
        }
        return mysqlShowSplitTables(jdbcTemplate,tablePrefix);
    }

    protected static List<String> showColumns(JdbcTemplate jdbcTemplate,String tableName,DbType dbType){
        if (log.isTraceEnabled()) {
            log.trace("showColumns for {}------------{}" ,tableName,dbType);
        }
        if (dbType.realType() == DbType.DM) {
            return dmShowColumns(jdbcTemplate,tableName);
        }
        return mysqlShowColumns(jdbcTemplate,tableName);
    }

    /*-------------------------private-------------------------*/

    private static boolean mysqlExitsTable(JdbcTemplate jdbcTemplate,String tableName){
        final String sql = DbType.MYSQL.showTableIfExist(tableName);
        Map<String, Object> map =null;
        try{
            map = jdbcTemplate.queryForMap(sql);
        }catch (Exception e){
            map = new HashMap<>();
        }
        return !map.isEmpty();
    }

    private static boolean dmExitsTable(JdbcTemplate jdbcTemplate,String tableName){
        final String sql = DbType.DM.showTableIfExist(tableName);
        final Map<String, Object> RESULT_COUNT = jdbcTemplate.queryForMap(sql);
        final Object count = RESULT_COUNT.get("RESULT_COUNT");
        return count != null && Integer.parseInt(count.toString()) > 0;
    }



    private static List<String> mysqlShowSplitTables(JdbcTemplate jdbcTemplate,String tablePrefix){
        final String sql = DbType.MYSQL.showSplitTables(tablePrefix);
        return jdbcTemplate.queryForList(sql,String.class);
    }

    private static List<String> dmShowSplitTables(JdbcTemplate jdbcTemplate,String tablePrefix){
        final String sql = DbType.DM.showSplitTables(tablePrefix);
        return jdbcTemplate.queryForList(sql,String.class);
    }


    private static List<String> mysqlShowColumns(JdbcTemplate jdbcTemplate,String tableName){
        final String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.columns WHERE table_name = " + tableName;
        return jdbcTemplate.queryForList(sql,String.class);
    }

    private static List<String> dmShowColumns(JdbcTemplate jdbcTemplate,String tableName){
        final String sql = "SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '" + tableName + "'";
        return jdbcTemplate.queryForList(sql,String.class);
    }


    //

}
