package com.think.data.multis.proxy;

import com.think.core.enums.DbType;
import com.think.data.multis.IDDLBuilder;
import com.think.data.multis.IThinkSqlTypeHelper;
import com.think.data.multis.ddl.DDLBuilderForDM;
import com.think.data.multis.ddl.DDLBuilderForMysql;
import com.think.data.multis.ddl.sqlType.SqlTypeDM;
import com.think.data.multis.ddl.sqlType.SqlTypeMysql;

import java.util.HashMap;
import java.util.Map;

public class DataTypeProxy {
    private static final Map<DbType,DataTypeProxy> holder = new HashMap<>();
    private DataTypeProxy(DbType dbType) {
        this.dbType = dbType;
    }
    public static DataTypeProxy getProxy(DbType dbType){
        if (dbType == null){
            throw new IllegalArgumentException("数据库类型不能为NULL");
        }
        if (holder.containsKey(dbType)){
            return holder.get(dbType);
        }
        DataTypeProxy proxy = new DataTypeProxy(dbType);
        holder.put(dbType,proxy);
        return proxy;
    }

    private DbType dbType;
    private IDDLBuilder ddlBuilder;


    public IThinkSqlTypeHelper sqlTypeHelper(){
        switch (this.dbType){
            case DM:{
                return new SqlTypeDM();
            }
            case MYSQL:{
                return new SqlTypeMysql();
//                return new SqlTypeMysql();
            }

        }
        throw new IllegalArgumentException("不支持的数据库类型");

    }

    public IDDLBuilder getDdlBuilder() {
        if(ddlBuilder!=null){
            return ddlBuilder;
        }
        synchronized (this) {
            switch (this.dbType) {
                case DM: {
                    this.ddlBuilder = new DDLBuilderForDM();
                    break;
                }
                case MYSQL: {
                    this.ddlBuilder = new DDLBuilderForMysql();
                    break;
                }
            }
        }
        return this.ddlBuilder;
    }
}
