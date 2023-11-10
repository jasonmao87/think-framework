package com.think.core.enums;

import com.think.common.result.ThinkMiddleState;
import com.think.common.util.StringUtil;
import com.think.common.util.TVerification;

/**
 * TODO 长期维护
 *      VERSION 0 : MYSQL
 * 2023-10-31
 *      VERSION : DA_MENG ,
 */
//数据库枚举
public enum DbType {
    DEFAULT,
    DM,
    MYSQL;




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
                System.out.println(" >>>>>>>>>>>>>>>>> " + key);
                return key;
            }
        }


    }
}
