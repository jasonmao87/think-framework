package com.think.data.provider;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.core.annotations.bean.ThinkStateColumn;
import com.think.data.exception.ThinkDataModelException;
import com.think.data.model.*;

import java.util.ArrayList;
import java.util.List;

public class ThinkDataDDLBuilder {
    String[] stateKeySuffix = new String[]{};


   public static final String createSpiltSQL(ThinkTableModel tableModal , int splitYear){
       List<String> fastMatchKeys = new ArrayList<>();
       StringBuilder sql  = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
       sql.append(DataModelBuilder.tableName(tableModal.getBeanClass()));
       if(splitYear>2000 && splitYear< (DateUtil.year() + 10 ) ){
           sql.append("_split_").append(splitYear);
       }
       sql.append(" (");
       //开始列
       //id 自动生成
       sql.append(" id ")
               .append(" BIGINT ")
               .append("  NOT NULL ");
       if(tableModal.isAutoIncPK()){
           sql.append("AUTO_INCREMENT ");
       }
       //开始明细列
       for(ThinkColumnModel cm : tableModal.getColumnModels()){
           if(cm.getKey().equalsIgnoreCase("id")){
               continue;
           }
           if(cm.isStateModel()){
               String dateSQL = ThinkJdbcTypeConverter.toJdbcTypeAsString(ThinkSqlType.DATETIME,0,false);
               String intSQL = ThinkJdbcTypeConverter.toJdbcTypeAsString(ThinkSqlType.INTEGER,0,false);
               String varCharSQL = ThinkJdbcTypeConverter.toJdbcTypeAsString(ThinkSqlType.VARCHAR,64,false);

               sql.append(",").append(cm.getKey()).append(ThinkStateColumn.flowStateSuffix_CancelTime)
                       .append(" ").append(dateSQL).append(" ").append( "COMMENT ").append( "'").append(cm.getComment()).append("_CancelTime" ).append("'");

               sql.append(",").append(cm.getKey()).append(ThinkStateColumn.flowStateSuffix_CompleteTime)
                       .append(" ").append(dateSQL).append(" ").append( "COMMENT ").append( "'").append(cm.getComment()).append("_CompleteTime" ).append("'");

               sql.append(",").append(cm.getKey()).append(ThinkStateColumn.flowStateSuffix_StartTime)
                       .append(" ").append(dateSQL).append(" ").append( "COMMENT ").append( "'").append(cm.getComment()).append("_StartTime" ).append("'");

               sql.append(",").append(cm.getKey()).append(ThinkStateColumn.flowStateSuffix_StateValue)
                       .append(" ").append(intSQL).append(" ").append( "COMMENT ").append( "'").append(cm.getComment()).append("_TryCount" ).append("'");

               sql.append(",").append(cm.getKey()).append(ThinkStateColumn.flowStateSuffix_TryCount)
                       .append(" ").append(intSQL).append(" ").append( "COMMENT ").append( "'").append(cm.getComment()).append("_TryCount" ).append("'");

               sql.append(",").append(cm.getKey()).append(ThinkStateColumn.flowStateSuffix_ResultMessage)
                       .append(" ").append(varCharSQL).append(" ").append( "COMMENT ").append( "'").append(cm.getComment()).append("_ResultMessage" ).append("'");





           }else{

               sql.append(" ,")
                       .append(cm.getKey())
                       .append(" ")
                       .append(cm.getSqlTypeString());
               if(StringUtil.isNotEmpty(cm.getComment())) {
                   sql.append(" COMMENT ").append("'").append(cm.getComment()).append("'");
               }
               if(cm.getDefaultValue()!=null){
                   sql.append(" DEFAULT ").append(cm.getDefaultValueForAlterAndCreate()).append(" ");
               }

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

//                   sql.append(" " ).append(cm.getDefaultValue());

               }


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

               String[] arr = indexModal.getKeys();

               sql.append(" INDEX")
                       .append("  ").append(indexModal.getIndexName());
               sql.append("(");
               for (int i = 0; i < arr.length; i++) {
                   String c = arr[i];
                   if (i > 0) {
                       sql.append(",");
                   }
                   sql.append(c);
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
       return sql.toString();


   }

    public static final String createSQL(ThinkTableModel tableModal){
       return createSpiltSQL(tableModal,-1);
    }


}
