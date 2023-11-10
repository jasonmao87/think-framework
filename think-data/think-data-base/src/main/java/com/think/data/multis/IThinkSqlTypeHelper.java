package com.think.data.multis;

import com.think.core.annotations.bean.ThinkColumn;
import com.think.data.model.ThinkColumnModel;
import com.think.data.model.ThinkJdbcTypeConverter;
import com.think.data.model.ThinkSqlType;

import java.lang.reflect.Type;

public interface IThinkSqlTypeHelper {

    /**
     * 获取对应的 thinkSqlType
     * @param type
     * @return
     */
    default ThinkSqlType sqlType(Type type){
        return ThinkJdbcTypeConverter.getType(type);
    }

    /**
     * 是否使用String 类型的 默认值
     * @param sqlType
     * @return
     */
    default boolean isUsingStringInSqlDefaultValue(ThinkSqlType sqlType){
        return ThinkJdbcTypeConverter.isUsingStringInSqlDefaultValue(sqlType);
    }


    default String defaultValueString(ThinkSqlType sqlType ,String defaultValue){
        return ThinkJdbcTypeConverter.defaultValueString(sqlType,defaultValue);
    }


    String sqlTypeString(Type type, ThinkColumn tColumn);

    String sqlTypeString(ThinkSqlType sqlType, ThinkColumn tColumn);

    String columnBuildSQL(String key,Type type, ThinkColumn tColumn);


    String columnBuildSQL(ThinkColumnModel columnModel);


}
