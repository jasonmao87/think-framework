package com.think.core.enums;

import com.think.common.result.ThinkMiddleState;
import com.think.common.util.StringUtil;
import com.think.common.util.TVerification;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO 长期维护
 *      VERSION 0 : MYSQL
 * 2023-10-31
 *      VERSION : DA_MENG ,
 */
//数据库枚举
@Slf4j
public enum DbType {
    DEFAULT,
    DM,
    MYSQL;



    public DbType realType(){
        if(this == DEFAULT){
            return defaultDbTypeValue();
        }
        return this;
    }

    private static DbType defaultValueSet = null;

    public static synchronized void setDefaultValue(DbType dbType){
        if(defaultValueSet == null) {
            if (dbType == null) {
                throw new IllegalArgumentException("默认数据库类型不能指定为NULL");
            }
            if (dbType == DEFAULT) {
                throw new IllegalArgumentException("不可选择默认税局库类型为DEFAULT");
            }
            defaultValueSet = dbType;
        }else{
            throw new IllegalArgumentException("已经设置默认数据库类型为"+defaultValueSet + "，不可重复设置。");
        }
    }

    public static DbType defaultDbTypeValue(){
        if (defaultValueSet == null){
            throw new IllegalArgumentException("未设置默认数据库类型");
        }
        return defaultValueSet;
    }



    public String fixKey(String key){
        if(key.equals("*")){
            return key;
        }
        switch (this){
            case DM:{
                //通过双引号包裹
                return StringUtil.wrappedBy(key,"\"");
            }
            case DEFAULT:{
                if(defaultValueSet!=null){
                    return defaultValueSet.fixKey(key);
                }else {
                    throw new IllegalArgumentException("未设置默认数据库类型");
                }

            }
            default:{
                return key;
            }
        }
    }


    public String showSplitTables(String tablePrefix){
        tablePrefix += "%";
        switch (this){
            case DM:{
                return "SELECT TABLE_NAME FROM USER_TABLES WHERE TABLE_NAME LIKE " + StringUtil.wrappedBy(tablePrefix,"'");
            }
            case DEFAULT:{
                if(defaultValueSet!=null){
                    return defaultValueSet.showSplitTables(tablePrefix);
                }else {
                    throw new IllegalArgumentException("未设置默认数据库类型");
                }

            }
            default:{
                return "show tables like " + StringUtil.wrappedBy(tablePrefix,"'");
            }
        }

    }

    public String showTableIfExist(String tableName){
        switch (this){
            case DM:{
                return "SELECT COUNT(*) AS RESULT_COUNT FROM USER_TABLES WHERE TABLE_NAME = " + StringUtil.wrappedBy(tableName,"'");
            }
            case DEFAULT:{
                if(defaultValueSet!=null){
                    return defaultValueSet.showTableIfExist(tableName);
                }else {
                    throw new IllegalArgumentException("未设置默认数据库类型");
                }

            }
            default:{
                return "show tables like " + StringUtil.wrappedBy(tableName,"'");
            }
        }
    }


}
