package com.think.data.multis.ddl;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.core.annotations.bean.ThinkIndex;
import com.think.core.annotations.bean.ThinkIndexes;
import com.think.core.annotations.bean.ThinkTable;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.enums.DbType;
import com.think.data.Manager;
import com.think.data.exception.ThinkDataModelException;
import com.think.data.model.*;
import com.think.data.multis.IDDLBuilder;
import com.think.data.multis.IThinkSqlTypeHelper;
import com.think.data.multis.proxy.DataTypeProxy;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DDLBuilderForDM implements IDDLBuilder {
    private DbType dbType = DbType.DM;

    @Override
    public List<String> createTableDDL(ThinkTableModel tableModal, int splitYear) {
        List<String> fastMatchKeys = new ArrayList<>();
        String finalTableName =DataModelBuilder.tableName(tableModal.getBeanClass());
        if(splitYear>2000 && splitYear< (DateUtil.year() + 10 ) ){
            finalTableName = finalTableName + "_split_" + splitYear;
        }
        StringBuilder sql  = new StringBuilder("CREATE TABLE  ");
        sql.append(dbType.fixKey(finalTableName));
        sql.append(" (");
        //开始列
        //id 自动生成
        sql.append(" ").append(dbType.fixKey("id"))
                .append(" BIGINT ")
                .append("  NOT NULL ");
        if(tableModal.isAutoIncPK()){
            sql.append("AUTO_INCREMENT ");
        }
        //开始明细列
        final IThinkSqlTypeHelper typeHelper = DataTypeProxy.getProxy(dbType).sqlTypeHelper();
        for(ThinkColumnModel cm : tableModal.getColumnModels()){
            if(cm.getKey().equalsIgnoreCase("id")){
                continue;
            }

            final String s = typeHelper.columnBuildSQL(cm);
            System.out.println( " >>>>>>>>>>>" + s );

            sql.append(" ,").append(s );
//                    .append(dbType.fixKey(cm.getKey()))
//                    .append(" ")
//                    .append(typeHelper.sqlTypeString(cm.getType(),cm.getColumnAnnotation()));
//
//            if(cm.getDefaultValue()!=null && cm.isUsingText() ==false){
//                sql.append(" DEFAULT ").append(cm.getDefaultValueForDDL()).append(" ");
//            }

            /**
             * 支持高效排序  ，且列不包含索引 ,且 列 类型是 String/ boolean  类型的 ，那么创建 附加 索引
             */
            if(cm.isFastMatchAble()){
                if(cm.isUsingText()){
                    throw new ThinkDataModelException( cm.getKey()+"为text类型，不允许使用fastMatchAble的支持");
                }
                fastMatchKeys.add(cm.getFastMatchKeyWhileExits());
                String typeString = "VARCHAR(" + ( cm.getLength() + 32 ) + ") NOT NULL";
                sql.append(" , ")
                        .append( dbType.fixKey(cm.getFastMatchKeyWhileExits()))
                        .append(" ")
                        .append(typeString);
//                            .append(" COMMENT 'match key of ").append(cm.getKey()).append("'");
                //添加 第二校验 键
                String secondTypeString = "VARCHAR(" + ( cm.getLength() * 3 + 32 ) + ") NOT NULL";
                sql.append(" , ")
                        .append( dbType.fixKey(cm.getSecondaryFastMatchKeyWhileExits()))
                        .append(" ")
                        .append(secondTypeString);

            }

        }
        //列结束 开始 索引ddl
        //先id
        sql.append(" ,PRIMARY KEY ( \"id\" )");




        sql.append(")");

        List<String> ddls=new ArrayList<>();
        ddls.add(sql.toString());
        //遍历索引
        if(tableModal.getIndexModels() !=null) {
            for (ThinkIndexModel indexModal : tableModal.getIndexModels()) {
                StringBuilder indexSql  =new StringBuilder("CREATE ");
                if (indexModal.isUk()) {
                    indexSql.append(" UNIQUE");
                }
                indexSql.append(" INDEX  ")
                        .append("  ").append(dbType.fixKey(indexModal.getIndexName() +"_"+ StringUtil.randomStr(6) ))
                        .append(" ON ")
                        .append(dbType.fixKey(finalTableName));
                indexSql.append(" (");
                String[] arr = indexModal.getKeys();
                for (int i = 0; i < arr.length; i++) {
                    String c = arr[i];
                    if (i > 0) {
                        indexSql.append(",");
                    }
                    indexSql.append(dbType.fixKey(c));
                }
                indexSql.append(")  ");
                ddls.add(indexSql.toString());
            }
        }
        if(tableModal.getIndexModels() !=null || fastMatchKeys.size()>0) {
            // 快速排序索引的创建吗
            fastMatchKeys.forEach(k->{
                String indexName = "fs_index_" + k;
                StringBuilder indexSql  =new StringBuilder("CREATE ");
                indexSql.append(" INDEX")
                        .append("  ").append(dbType.fixKey(indexName+"_"+ StringUtil.randomStr(6)));
                indexSql.append("(").append(dbType.fixKey(indexName));
                indexSql.append(") ");
                ddls.add(indexSql.toString());
            });


        }

        return ddls;
    }

    public static void main(String[] args) {

        final ThinkTableModel thinkTableModel = Manager.getModelBuilder().get(TbAdmin.class);
        System.out.println(thinkTableModel);
        DDLBuilderForDM ddlBuilderForDM = new DDLBuilderForDM();
        for (String s : ddlBuilderForDM.createTableDDL(thinkTableModel)) {
            System.out.println(s.replace(",",",\n\t") + ";");
        }


    }
}

@Data
@ThinkTable(value = "tb_admin_Aaddasda",comment = "管理员表",dbType = DbType.DM )
@ThinkIndexes(indexes = {
        @ThinkIndex( keys = {"name"},unique = false),
        @ThinkIndex( keys = {"uniqueId","deleteState"},unique = true),
})
class TbAdmin extends SimplePrimaryEntity {

    @Override
    public Long getId() {
        return super.getId();
    }

    private String uniqueId ;

    private boolean deleteState ;

    private String name ;

    private Date userDate ;

    private boolean adminState;

    private int score ;




}