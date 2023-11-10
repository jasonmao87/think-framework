package com.think.data.multis.ddl.sqlType;

import com.think.common.util.StringUtil;
import com.think.core.annotations.bean.ThinkColumn;
import com.think.core.enums.DbType;
import com.think.data.model.ThinkColumnModel;
import com.think.data.model.ThinkJdbcTypeConverter;
import com.think.data.model.ThinkSqlType;
import com.think.data.multis.IThinkSqlTypeHelper;

import java.lang.reflect.Type;

public class SqlTypeDM implements IThinkSqlTypeHelper {

    private DbType dbType = DbType.DM;


    @Override
    public String sqlTypeString(ThinkSqlType sqlType, ThinkColumn tColumn) {
        String t = "" ;
        int len =  tColumn!=null?tColumn.length():36;
        boolean nullAble = tColumn!=null?tColumn.nullable():false;
        switch (sqlType) {
            case NONE:{
                t = null;
                break;
            }
            case VARCHAR:{
                if(tColumn!=null && tColumn.usingText()){
                    t = "LONGVARCHAR";
                }else{
                    t = "VARCHAR(" +len +")" ;
                }
                break;
            }
            case BIT:{
                t = "BIT";
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

    @Override
    public String sqlTypeString(Type type, ThinkColumn tColumn) {
        final ThinkSqlType thinkSqlType = this.sqlType(type);
        return this.sqlTypeString(thinkSqlType,tColumn);
    }


    @Override
    public String columnBuildSQL(ThinkColumnModel columnModel) {
        return this.columnBuildSQL(columnModel.getKey(),columnModel.getType(),columnModel.getColumnAnnotation());
    }

    @Override
    public String columnBuildSQL(String key, Type type, ThinkColumn annotation) {
        final ThinkSqlType thinkSqlType = this.sqlType(type);
        StringBuilder builder = new StringBuilder(" ");
        builder.append("\"").append(key).append("\" ")
                .append(this.sqlTypeString(thinkSqlType,annotation));
        String defValue = annotation!=null?annotation.defaultValue() :null;
        boolean nullAble = annotation!=null?annotation.nullable():false;
        if(StringUtil.isEmpty(defValue) ){
//            if(nullAble ==false){
//                builder.append(" DEFAULT '' ");
//            }
        }else{
            builder.append(" DEFAULT ").append(this.defaultValueString(thinkSqlType,defValue)).append(" ");
        }

//        if(nullAble){
//            builder.append(" NULL ");
//        }else {
//            builder.append(" NOT NULL ");
//        }
        return builder.toString();
    }

}
