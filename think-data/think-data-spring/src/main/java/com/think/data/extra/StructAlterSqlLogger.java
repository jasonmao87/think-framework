package com.think.data.extra;

import com.think.data.dao.ThinkDao;
import com.think.data.model.TbStructAlterSqlLog;
import com.think.data.provider.ThinkDaoImpl;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/8/29 10:51
 * @description :
 */
public class StructAlterSqlLogger {

    private ThinkDao<TbStructAlterSqlLog> dao ;

    public StructAlterSqlLogger(JdbcTemplate jdbcTemplate) {
        this.dao =ThinkDaoImpl.staticBuild(TbStructAlterSqlLog.class,jdbcTemplate);
    }

    /**
     * 执行alter 完成 后 ，修改
     * @param tableName
     * @param alterSql
     * @param reverseSql
     */
    public void afterAlter(Class targetClass ,String tableName  ,String alterSql ,String reverseSql ){
        if(targetClass != dao.targetClass()) {
            TbStructAlterSqlLog log = new TbStructAlterSqlLog();
            log.setTbObjectClass(targetClass.getName());
            log.setAlterSql(alterSql);
            log.setReverseSql(reverseSql);
            log.setTableName(tableName);
            this.dao.insert(log);
        }
    }




}
