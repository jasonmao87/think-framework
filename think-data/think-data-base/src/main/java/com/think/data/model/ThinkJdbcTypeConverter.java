package com.think.data.model;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.core.annotations.bean.ThinkColumn;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ThinkJdbcTypeConverter {

    /**
     * 是否需要在 sql中使用string 描述 默认值
     *  如 bit 不需要   >>> BIT(1) NOT NULL DEFAULT 0
     *  varchar 需要   >>> VARCHAR(12) NOT NULL DEFAULT 'AC'
     * @param sqlType
     * @return
     */
    public static boolean isUsingStringInSqlDefaultValue(ThinkSqlType sqlType){
        switch (sqlType){
            case NONE:{
                return false;
            }
            case BIT:{
                return false;
            }
            case FLOAT:{
                return false;
            }
            case BIGINT:{
                return false;
            }
            case DOUBLE:{
                return false;
            }
            case INTEGER:{
                return false;
            }
        }
        return true;
    }

    public static String defaultValueString(ThinkSqlType sqlType ,String defaultValue){
//        if(defaultValue!=null){
//            return defaultValue;
//        }
        String t = null;
        switch (sqlType){
            case TEXT:{
                return null;
            }
            case NONE:{
                return null;
            }
            case VARCHAR:{

                return sqlStringDefaultValue(defaultValue);
            }
            case BIT:{
                return null;
            }
            case CHAR:{
                return sqlStringDefaultValue(defaultValue);
            }
            case DATE:{

                return  sqlDateValue(defaultValue);
            }
            case TIME:{
                return "00:00:00";
            }
            case FLOAT:{
                return "0.0";
            }
            case BIGINT:{
                return "0";
            }
            case DOUBLE:{
                return "0.0";
            }
            case INTEGER:{
                return "0";
            }
            case DATETIME:{
                return sqlDateTimeValue(defaultValue);
            }
            case ENUM:{
                t = "";
                break;
            }
            default:{
                t = null;
            }
        }
        return t;
    }


    public static String toJdbcTypeAsString(ThinkSqlType sqlType ,int len , boolean nullAble){
        String t = "" ;
        switch (sqlType){
            case NONE:{
                t = null;
                break;
            }
            case VARCHAR:{
                t = "VARCHAR(" +len +")" ;
                break;
            }
            case BIT:{
                t = "BIT(1)";
                break;
            }
            case CHAR:{
                t = "VARCHAR(" + len + ")";
                break;
            }
            case DATE:{
                t= "DATE";
                break;
            }
            case TIME:{
                t = "TIME";
                break;
            }
            case FLOAT:{
                t = "DOUBLE";
                break;
            }
            case BIGINT:{
                t= "BIGINT";
                break;
            }
            case DOUBLE:{
                t = "DOUBLE";
                break;
            }
            case INTEGER:{
                t = "INT";
                break;
            }
            case DATETIME:{
                t = "DATETIME";
                break;
            }
            case ENUM:{
                t = "VARCHAR(32)";
                break;

            }
            default:{
                t = null;
            }

        }
        if(t == null){
            return null;
        }else {
            return (t + " " )+ (nullAble ? "NULL" : "NOT NULL");
        }
    }


    protected static String toJdbcTypeString(Type type, ThinkColumn tColumn){
//        log.info("{} - {} " , type,tColumn);
        String t = "" ;

        int len =  tColumn!=null?tColumn.length():36;
        boolean nullAble = tColumn!=null?tColumn.nullable():false;
        ThinkSqlType sqlType = getType(type);
        switch (sqlType){
            case NONE:{
                t = null;
                break;
            }
            case VARCHAR:{
                if(tColumn!=null && tColumn.usingText()){
                    //mediumtext
                    t = "mediumtext";
                }else{
                    t = "VARCHAR(" +len +")" ;
                }
                break;
            }
            case BIT:{
                t = "BIT(1)";
                break;
            }
            case CHAR:{
                t = "VARCHAR(" + len + ")";
                break;
            }
            case DATE:{
                t= "DATE";
                break;
            }
            case TIME:{
                t = "TIME";
                break;
            }
            case FLOAT:{
                t = "DOUBLE";
                break;
            }
            case BIGINT:{
                t= "BIGINT";
                break;
            }
            case DOUBLE:{
                t = "DOUBLE";
                break;
            }
            case INTEGER:{
                t = "INT";
                break;
            }
            case DATETIME:{
                t = "DATETIME";
                break;
            }
            case ENUM:{
                t = "VARCHAR(32)";
                break;

            }
            default:{
                t = null;
            }

        }
        if(t == null){
            return null;
        }else {
            return (t + " " )+ (nullAble ? "NULL" : "NOT NULL");
        }
    }

    protected static final ThinkSqlType getType(Type type){
        return typeMap.getOrDefault( type, ThinkSqlType.NONE);
    }



    private static Map<Class, ThinkSqlType> typeMap ;
    static {
        typeMap = new HashMap();
        typeMap.put(Date.class, ThinkSqlType.DATETIME);
        typeMap.put(java.sql.Date.class, ThinkSqlType.DATE);
        typeMap.put(Time.class, ThinkSqlType.TIME);

        typeMap.put(String.class, ThinkSqlType.VARCHAR);

        typeMap.put(Character.class , ThinkSqlType.CHAR);
        typeMap.put(char.class, ThinkSqlType.CHAR);

        typeMap.put(Boolean.class, ThinkSqlType.BIT);
        typeMap.put(boolean.class, ThinkSqlType.BIT);

        typeMap.put(Integer.class, ThinkSqlType.INTEGER);
        typeMap.put(int.class, ThinkSqlType.INTEGER);

        typeMap.put(Long.class, ThinkSqlType.BIGINT);
        typeMap.put(long.class, ThinkSqlType.BIGINT);

        typeMap.put(Double.class, ThinkSqlType.DOUBLE);
        typeMap.put(double.class, ThinkSqlType.DOUBLE);
        /*为了精度 等问题 ，再数据库我们统一使用double*/
        typeMap.put(Float.class, ThinkSqlType.DOUBLE);
        typeMap.put(float.class, ThinkSqlType.DOUBLE);

        /*为了精度 等问题 ，再数据库我们统一使用 int */
        typeMap.put(Short.class, ThinkSqlType.INTEGER);
        typeMap.put(short.class, ThinkSqlType.INTEGER);

        /*专门用于存储 二进制 数据*/
        typeMap.put(BigInteger.class,ThinkSqlType.BIT);


        /*专门用于处理枚举 */
        typeMap.put(Enum.class,ThinkSqlType.ENUM);

        /** */








    }

    private static final String sqlDateTimeValue(String def){
        return sqlDateTimeValue( DateUtil.valueOfString(def));
    }

    private static final String sqlDateTimeValue(Date def){
        return DateUtil.toFmtString( def,"yyyy-MM-dd HH:mm:ss") ;
    }

    private static final String sqlDateValue(String def){
        return sqlDateValue(DateUtil.valueOfString(def));
    }

    private static final String sqlDateValue(Date def){
        return  DateUtil.toFmtString(def,"yyyy-MM-dd");
    }


    private static String sqlStringDefaultValue(String def){
        if(StringUtil.isEmpty(def)){
            return "";
        }
        return def;
    }


    public static final String sqlDefaultValueString(Type type){
        return sqlDefaultValueString(getType(type));
    }

    /**>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     *  you hace to check here later !
     *<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<*/
    public static final String sqlDefaultValueString(ThinkSqlType thinkSqlType){
        switch (thinkSqlType){
            case ENUM: return "";
            case BIT: return  "0";
            case INTEGER: return "0";
            case BIGINT: return "0";
            case CHAR: return "";
            case VARCHAR: return "";
            case TEXT: return "";
            case TIME: return "00:00:00";
            case DATE:  return sqlDateValue(DateUtil.zeroDate());
            case DATETIME:  return  sqlDateTimeValue(DateUtil.zeroDate()) ;
            case FLOAT: return "0.0";
            case DOUBLE: return "0.0";
            default: {
                return null;
            }

        }
//            pdca 命名 ：  动词 + 名次 + 指标


    }



    public static void main(String[] args) {
        System.out.println(DateUtil.valueOfString("2021-1-1"));
        System.out.println(sqlDateValue(""));
        System.out.println(sqlStringDefaultValue("dadda'dadad'"));

    }
}
