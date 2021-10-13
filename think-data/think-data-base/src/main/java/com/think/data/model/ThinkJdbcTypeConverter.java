package com.think.data.model;

import com.think.common.util.ByteUtil;
import com.think.core.annotations.bean.ThinkColumn;
import com.think.core.bean.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ThinkJdbcTypeConverter {

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

    private static final ThinkSqlType getType(Type type){
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


    byte[] nn = new byte[2];
}
