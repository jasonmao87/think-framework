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

public class DDLBuilderForMysql implements IDDLBuilder {
    private DbType dbType = DbType.MYSQL;

    @Override
    public List<String> createTableDDL(ThinkTableModel tableModal, int splitYear) {
        List<String> fastMatchKeys = new ArrayList<>();
        StringBuilder sql  = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(DataModelBuilder.tableName(tableModal.getBeanClass()));
        if(splitYear>2000 && splitYear< (DateUtil.year() + 10 ) ){
            sql.append("_split_").append(splitYear);
        }
        sql.append(" (");
        //开始列
        //id 自动生成
        sql.append(" id ").append(" BIGINT ").append("  NOT NULL ");
        if(tableModal.isAutoIncPK()){
            sql.append("AUTO_INCREMENT ");
        }
        //开始明细列
        final IThinkSqlTypeHelper typeHelper = DataTypeProxy.getProxy(dbType).sqlTypeHelper();
        for(ThinkColumnModel cm : tableModal.getColumnModels()){
            if(cm.getKey().equalsIgnoreCase("id")){
                continue;
            }
            System.out.println(cm.getKey() + " >>>>>>>>>>>" + cm.getType() );
            final String columDDLPart = typeHelper.columnBuildSQL(cm);
            System.out.println(columDDLPart);

            sql.append(" ,").append(columDDLPart);
//
//                    .append(cm.getKey())
//                    .append(" ")
//                    .append(cm.getSqlTypeString());
//            if(StringUtil.isNotEmpty(cm.getComment())) {
//                sql.append(" COMMENT ").append("'").append(cm.getComment()).append("'");
//            }
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
                        .append( cm.getFastMatchKeyWhileExits())
                        .append(" ")
                        .append(typeString)
                        .append(" COMMENT 'match key of ").append(cm.getKey()).append("'");
                //添加 第二校验 键
                String secondTypeString = "VARCHAR(" + ( cm.getLength() * 3 + 32 ) + ") NOT NULL";

                sql.append(" , ")
                        .append( cm.getSecondaryFastMatchKeyWhileExits())
                        .append(" ")
                        .append(secondTypeString)
                        .append(" COMMENT 'match Secondary key of ").append(cm.getKey()).append("'");

            }
        }

        //列结束 开始 索引ddl
        //先id
        sql.append(" ,PRIMARY KEY ( id )");
        //遍历索引
        if(tableModal.getIndexModels() !=null) {
            for (ThinkIndexModel indexModal : tableModal.getIndexModels()) {
                sql.append(",");
                if (indexModal.isUk()) {
                    sql.append(" UNIQUE");
                }
                String[] indexKeyArray = indexModal.getKeys();
                sql.append(" INDEX").append("  ").append(indexModal.getIndexName());
                sql.append("(");
                for (int i = 0; i < indexKeyArray.length; i++) {
                    String indexKey = indexKeyArray[i];
                    if (i > 0) {
                        sql.append(",");
                    }
                    sql.append(indexKey);
                }
                sql.append(") USING BTREE ");
            }
        }
        // 快速排序索引的创建吗
        fastMatchKeys.forEach(k->{
            sql.append(",");

            sql.append(" INDEX")
                    .append("  ").append("fs_index_").append(k);
            sql.append("(").append(k);
            sql.append(") USING BTREE ");
        });
        sql.append(") ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci  ");
        List<String> ddlList = new ArrayList<>();
        ddlList.add(sql.toString());
        return ddlList;
    }

    public static void main(String[] args) {


        final ThinkTableModel thinkTableModel = Manager.getModelBuilder().get(TbAdmin.class);
        System.out.println(thinkTableModel);
        DDLBuilderForMysql ddlBuilderForMysql = new DDLBuilderForMysql();
        for (String s : ddlBuilderForMysql.createTableDDL(thinkTableModel)) {
            System.out.println(s.replace(",",",\n\t") + ";");
        }

    }
}
@Data
@ThinkTable(value = "tb_admin_mysql",comment = "管理员表",dbType = DbType.MYSQL )
@ThinkIndexes(indexes = {
        @ThinkIndex( keys = {"name"},unique = false),
        @ThinkIndex( keys = {"uniqueId","deleteState"},unique = true),
})
class TbAdminMysql extends SimplePrimaryEntity {

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
