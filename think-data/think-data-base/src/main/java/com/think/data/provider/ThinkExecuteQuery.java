package com.think.data.provider;

import com.think.common.data.mysql.IThinkResultFilter;
import com.think.common.util.ThinkCollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

public class ThinkExecuteQuery {

    private boolean mayByEmpty = false;
    private boolean select = false;
    private boolean update = false;
    private boolean insert = false;
    private boolean delete = false;
    private String sql ;
    private Serializable[] values ;

    List<IThinkResultFilter> resultFilters;
    private Class targetClass ;


    public Class getTargetClass() {
        return targetClass;
    }

    public ThinkExecuteQuery(String sql, Serializable[] values , List<IThinkResultFilter> resultFilters , boolean mayByEmpty, Class targetClass ) {
        this.targetClass = targetClass;
        this.sql = sql;
        this.values = values;
        this.mayByEmpty = mayByEmpty;

        for (int i = 0; i < values.length; i++) {
            if(values[i] instanceof Enum){
                values[i] = values[i].toString();
            }
        }

        String checkStr =  this.sql.toUpperCase().trim() ;
        if(checkStr.startsWith("SELECT")){
            this.select = true;
        }else if(checkStr.startsWith("UPDATE")){
            this.update = true;
        }else if(checkStr.startsWith("INSERT")){
            this.insert = true;
            this.update = true;
        }else if(checkStr.startsWith("DELETE")){
            this.delete = true;
            this.update = true;
        }
        this.resultFilters = resultFilters;
    }

    protected Serializable[] getValues() {
        return values;
    }



    protected String getSql(String tableName){
        return sql.replaceAll("#tbName#",tableName);
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isInsert() {
        return insert;
    }

    public boolean isUpdate() {
        return update;
    }

    public boolean isSelect() {
        return select;
    }


    protected List<IThinkResultFilter> getResultFilters() {

        return resultFilters!=null?resultFilters: ThinkCollectionUtil.emptyList();
    }

    public boolean isMayByEmpty() {
        return mayByEmpty;
    }
}
