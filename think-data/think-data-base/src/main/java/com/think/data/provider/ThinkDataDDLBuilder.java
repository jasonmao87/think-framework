package com.think.data.provider;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.core.enums.DbType;
import com.think.data.exception.ThinkDataModelException;
import com.think.data.model.*;
import com.think.data.multis.IDDLBuilder;
import com.think.data.multis.proxy.DataTypeProxy;

import java.util.ArrayList;
import java.util.List;

public class ThinkDataDDLBuilder {
   public static final List<String> createSpiltSQL(ThinkTableModel tableModal , int splitYear){
       final DbType dbType = tableModal.getDbType();
       final IDDLBuilder ddlBuilder = DataTypeProxy.getProxy(dbType).getDdlBuilder();
       return ddlBuilder.createTableDDL(tableModal,splitYear);
   }

    public static final List<String>  createSQL(ThinkTableModel tableModal){
       return createSpiltSQL(tableModal,-1);
    }



    public static String addColumn(ThinkTableModel tableModal , ThinkColumnModel columnModel ,String tableName ){
        final IDDLBuilder ddlBuilder = DataTypeProxy.getProxy(tableModal.getDbType()).getDdlBuilder();
        return ddlBuilder.addColumn(tableModal, columnModel, tableName);
    }

}
