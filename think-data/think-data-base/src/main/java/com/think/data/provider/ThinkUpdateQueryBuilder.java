package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.common.util.security.DesensitizationUtil;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.core.enums.DbType;
import com.think.data.Manager;
import com.think.data.exception.ThinkDataRuntimeException;
import com.think.data.model.ThinkColumnModel;
import com.think.data.model.ThinkTableModel;
import com.think.data.verification.ThinkDataValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ThinkUpdateQueryBuilder {

    private static String tableName(Class cls){
        return "#tbName#";
//        return DataModalBuilder.tableName(cls);
    }


    /**
     * 拼接状态流程控制字段
     * @param state
     * @param columnModel
     * @param sqlBuilder
     * @param values
     */


    /**
     * Nomore 中所有的ID都是自增的！ 所以 inset 不会有id的信息
     * @param t
     * @return
     */
    protected static final <T extends _Entity> ThinkExecuteQuery insertOneSQL(T t) {
        ThinkTableModel modal =
                Manager.getModelBuilder().get(t.getClass());
        final DbType dbType = modal.getDbType();
        ThinkColumnModel[] clms = modal.getColumnModels();
        StringBuilder sql = new StringBuilder("insert into ");
        String tableName = tableName(modal.getBeanClass());
        tableName = dbType.fixKey(tableName);
        sql.append(tableName).append("( ");
        List<Object> values = new ArrayList<>( );
        int i = 0 ;
        for(ThinkColumnModel cm : clms){

            Object v = ClassUtil.getProperty(t,cm.getKey());
            //id 没有值，跳过
            if(cm.getKey().equalsIgnoreCase("id")){
                if(v == null) {
                    continue;
                }
            }

            if(v == null){
                if(cm.isNullable()){

                }else if(cm.getType() == String.class){
                    v = cm.getDefaultValue();
                }else if(cm.getType() == Date.class || cm.getType() == java.sql.Date.class){
                    v = DateUtil.valueOfString(cm.getDefaultValue());
                }

            }
            //再次判断，如果不允许null的 已经设置了默认值 ！
            if(v != null) {
                if(i >0){
                    sql.append(", ");
                }

                sql.append(dbType.fixKey(cm.getKey()));

                /**
                 * 值 校验
                 */
                if(ThinkDataValidator.isEnable()){
                    //值 校验
                    ThinkDataValidator.verification(t.getClass(), cm.getKey(), v);
                }


                /**
                 * 处理脱敏，并 添加 到valueList ，
                 */
                if(cm.isSensitive() && v instanceof String ){
                    values.add(DesensitizationUtil.encode((String) v));
                }else{
                    values.add(v);
                }

                i++;

                if(cm.isFastMatchAble()){
                    final String firstKey = cm.getFastMatchKeyWhileExits();
                    sql.append(", ").append(dbType.fixKey(firstKey));
                    String sv =  computeFastMatchKeyValue((String)v);
                    values.add(sv);
                    i++ ;
                    // second key
                    final String secondKey = cm.getSecondaryFastMatchKeyWhileExits();
                    sql.append(", ").append(dbType.fixKey(secondKey));
                    String fv =  computeSecondaryFastMatchKeyValue((String)v);
                    values.add(fv);
                    i++ ;

                }


            }
        }
//
//        // 快速排序支持  >>>>>>>>>>>>>
//        for(String k : modal.getSortKeyArray()){
//            String sortKeyName = modal.getSortKeyName(k);
//            ThinkColumnModel cm = modal.getKey(k);
//            sql.append(" ," ).append(sortKeyName) ;
//            Object v = ClassUtil.getProperty(t,cm.getKey());
//            if(v instanceof String){
//                String kValue = (String) v;
//                kValue = StringUtil.getShortPinyinWithoutSymbol(kValue);
//                int maxLen = 24 + cm.getLength();
//                values.add(kValue);
//            }
//        }
//        // 快速排序支持  <<<<<<<<<<<<<<<<<<<<

        sql.append(") values( ");
        for(int x =0 ;x < i ;x++){
            if(x>0){
                sql.append(",");
            }
            sql.append("? ");
        }


        sql.append(") ");
        return  new ThinkExecuteQuery(sql.toString(),values.toArray(new Serializable[values.size()]),null,false,t.getSelfClass());
    }


    protected static final <T extends _Entity> ThinkExecuteQuery batchInsertSQL(List<T> list){
        //状态控制对象的数量
        int stateKeyCount = 0;
        T t = list.get(0);
        ThinkTableModel modal = Manager.getModelBuilder().get(t.getClass());
        final DbType dbType = modal.getDbType();
        String tableName =tableName(modal.getBeanClass());
        tableName = dbType.fixKey(tableName);
        ThinkColumnModel[] clms = modal.getColumnModels();
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(tableName).append("( ");
        List<Object> values = new ArrayList<>( );
        int i = 0 ;
        int outIndex = 0 ;
        List<String> clmNames = new ArrayList<>();
        for(ThinkColumnModel cm : clms){
            final String key = cm.getKey();
            Object v = ClassUtil.getProperty(t, key);
            if(key.equalsIgnoreCase("id")){
                if(v == null) {
                    continue;
                }
            }

            if(i >0){
                sql.append(", ");
            }
            sql.append(dbType.fixKey(key));
            clmNames.add(key);
            i++;





        }
//
//        // 快速匹配支持  >>>>>>>>>>>>>
        for(String k : modal.getSortKeyArray()){
            ThinkColumnModel columnModel = modal.getKey(k);
            String firstKey = columnModel.getFastMatchKeyWhileExits();  //modal.getSortKeyName(k);

            sql.append(" ," ).append(dbType.fixKey(firstKey)) ;
            i++ ;
            String secondKeyName = columnModel.getSecondaryFastMatchKeyWhileExits();
            sql.append(" ,").append(dbType.fixKey(secondKeyName));
            i ++;
        }
//        // 快速排序支持  <<<<<<<<<<<<<<<<<<<<

        sql.append(") values");
        for(T tmp : list){
            if(outIndex >0){
                sql.append(",");
            }
            sql.append("(");
            for (int x = 0; x < clms.length; x++) {


                if (x > 0) {
                    sql.append(",");
                }

                String key =clmNames.get(x);

                sql.append("? ");
                Object v = ClassUtil.getProperty(tmp,key);
                if(v == null){
                    if(clms[x].getType() == String.class){
                        v = clms[x].getDefaultValue();
                    }
                }

                if(v !=null) {
                    /**
                     * 值校验
                     */
                    if(ThinkDataValidator.isEnable()){
                        //值 校验
                        ThinkDataValidator.verification(t.getClass(),clmNames.get(x),v);
                    }
                    /**
                     * 脱敏处理
                     */
                    if (clms[x].isSensitive() && v instanceof String) {
                        v = DesensitizationUtil.encode((String) v);
                    }
                 }
                values.add(v);




            }// 内部正常循环结束
            // 快速排序支持  >>>>>>>>>>>>>
            for(String k : modal.getSortKeyArray()){
                ThinkColumnModel cm = modal.getKey(k);
                Object v = ClassUtil.getProperty(tmp,cm.getKey());
                if(v instanceof String){
                    sql.append(",? ");

                    String fastShortKeyValue = computeFastMatchKeyValue((String) v);
                    values.add(fastShortKeyValue);
                    // second key value ;
                    sql.append(",? ");
                    String fastFullKeyValue = computeSecondaryFastMatchKeyValue( (String) v);
                    values.add(fastFullKeyValue);

                }
            }
            // 快速排序支持  <<<<<<<<<<<<<<<<<<<<
            sql.append(") ");
            outIndex ++ ;
        }
        return new ThinkExecuteQuery(sql.toString(),values.toArray(new Serializable[values.size()]),null ,false,t.getClass());
    }

    protected static <T extends _Entity> ThinkExecuteQuery updateSql(T t){
        List<Serializable> valuesList = new ArrayList<>();
        ThinkTableModel modal = Manager.getModelBuilder().get(t.getClass());
        final DbType dbType = modal.getDbType();
        String tableName = tableName(modal.getBeanClass());
        tableName = dbType.fixKey(tableName);
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(tableName).append(" SET");
        int index = 0 ;
        for(ThinkColumnModel columnModal : modal.getColumnModels()){
            if(columnModal.getKey().equalsIgnoreCase("id")){
                continue;
            }else if(columnModal.isEditAble() == false){
                //不允许被修改的 key ，直接跳过
                continue;
            }else{
                Object v = ClassUtil.getProperty(t,columnModal.getKey());
                if(v !=null){

                    /**
                     * 值校验
                     */
                    final String key = columnModal.getKey();
                    if(ThinkDataValidator.isEnable()){
                        //值 校验
                        ThinkDataValidator.verification(t.getClass(), key,v);
                    }



                    if(index > 0){
                        sql.append(",");
                    }
                    sql.append(" ").append(dbType.fixKey(key)).append(" = ?");
                    /**
                     * 脱敏处理 并 添加值 到list
                     */
                    if(columnModal.isSensitive() && v instanceof String ){
                        valuesList.add(DesensitizationUtil.encode((String) v));
                    }else{
                        valuesList.add((Serializable)v);
                    }
                    /**
                     * 脱敏处理
                     */


                    index ++ ;

                    //如果支持排序支持 开始
                    if(columnModal.isFastMatchAble()){
                        String fastMatchKeyName= columnModal.getFastMatchKeyWhileExits();
                        sql.append(",").append( dbType.fixKey(fastMatchKeyName)).append(" = ? ");
                        valuesList.add(computeFastMatchKeyValue((String) v));
                        // second key
                        String secondKey = columnModal.getSecondaryFastMatchKeyWhileExits();
                        sql.append(",").append( dbType.fixKey(secondKey)).append(" = ? ");
                        valuesList.add(computeSecondaryFastMatchKeyValue((String) v));


                    }
                    //如果支持排序支持  结束


                }
            }
        }
        sql.append(" WHERE ").append(dbType.fixKey("id")).append(" = ?");
        valuesList.add(t.getId());
        return new ThinkExecuteQuery(sql.toString(),valuesList.toArray(new Serializable[valuesList.size()]),null,false,t.getClass());
    }


    protected static  <T extends _Entity> ThinkExecuteQuery updateSql(ThinkUpdateMapper<T> updaterMapper ){
        final ThinkTableModel tableModel = Manager.getModelBuilder().get(updaterMapper.sqlFilter().gettClass());
        final DbType dbType = tableModel.getDbType();
        String tableName = tableName(updaterMapper.sqlFilter().gettClass());
        tableName = dbType.fixKey(tableName);
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(tableName).append(" SET ");
        List<Object> values = new ArrayList<>();

        ThinkTableModel tableModal = Manager.getModelBuilder().get(updaterMapper.getTargetClass());
        int setIndex = 0 ;
        final Map<String, Object> incMap = updaterMapper.getIncMapper();
        final Map<String, Object> setMapper = updaterMapper.getSetMapper();
        final Map<String, String> setKeyMapper = updaterMapper.getSetKeyMapper();
        final Map<String, Map<String, Double>> divMapper = updaterMapper.getDivMapper();
        final Map<String, Map<String, Double>> multiplyMapper = updaterMapper.getMultiplyMapper();
        final Map<String, Map<String, String>> keyDivKeyMapper = updaterMapper.getKeyDivKeyMapper();
        final Map<String, Map<String, String>> keyMultiplyKeyMapper = updaterMapper.getKeyMultiplyKeyMapper();
        for(String k : incMap.keySet() ){
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }
            if(setIndex >0){
                sql.append(", ");
            }
            sql.append( dbType.fixKey(k)).append(" = ").append(k).append("+? ");
            setIndex ++ ;
            values.add(incMap.get(k));
        }
        for(String k : setMapper.keySet() ){
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }

            Object v = setMapper.get(k);

            /**
             * 值 校验
             */
            if(ThinkDataValidator.isEnable()){
                //值 校验
                ThinkDataValidator.verification(updaterMapper.getTargetClass(),k,v);
            }

            if(setIndex >0){
                sql.append(", ");
            }
            sql.append(dbType.fixKey(k)).append(" = ").append(" ? ");

            /**
             * 脱敏处理
             */
            if(columnModal!=null && columnModal.isSensitive() && v instanceof String ){
                values.add( DesensitizationUtil.encode((String) setMapper.get(k) ));
            }else{
                values.add( v );
            }
            /**
             * 脱敏处理
             */

            setIndex ++ ;

            //如果支持排序支持 开始
            if(columnModal.isFastMatchAble()){
                String fastMatchKeyName= columnModal.getFastMatchKeyWhileExits();
                sql.append(",").append(dbType.fixKey(fastMatchKeyName)).append(" = ? ");
                values.add(computeFastMatchKeyValue((String) v));

                // second key
                String secoondKey = columnModal.getSecondaryFastMatchKeyWhileExits();
                sql.append(",").append( dbType.fixKey(secoondKey)).append(" = ? ");
                values.add(computeSecondaryFastMatchKeyValue((String) v));
            }
            //如果支持排序支持  结束

        }
        for(String k : setKeyMapper.keySet() ){
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }
            if(setIndex >0){
                sql.append(", ");
            }
            sql.append(dbType.fixKey(k)).append(" = ").append(setKeyMapper.get(k)).append(" ");
            setIndex ++ ;
        }


        for (String k :divMapper.keySet()) {
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }
            if(setIndex >0){
                sql.append(", ");
            }
            final Map<String, Double> stringDoubleMap = divMapper.get(k);
            for (Map.Entry<String, Double> entry : stringDoubleMap.entrySet()) {
                sql.append(dbType.fixKey(k)).append("=").append(entry.getKey()).append(" / ").append(entry.getValue()).append(" ");
                break;
            }
        }
        for (String k :multiplyMapper.keySet()) {
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }
            if(setIndex >0){
                sql.append(", ");
            }
            final Map<String, Double> stringDoubleMap = multiplyMapper.get(k);
            for (Map.Entry<String, Double> entry : stringDoubleMap.entrySet()) {
                sql.append(dbType.fixKey(k)).append("=").append(entry.getKey()).append(" * ").append(entry.getValue()).append(" ");
                break;
            }
        }

        for (String k :keyDivKeyMapper.keySet()) {
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }
            if(setIndex >0){
                sql.append(", ");
            }
            final Map<String, String> stringDoubleMap = keyDivKeyMapper.get(k);
            for (Map.Entry<String, String> entry : stringDoubleMap.entrySet()) {
                sql.append(dbType.fixKey(k)).append("=").append(entry.getKey()).append(" / ").append(entry.getValue()).append(" ");
                break;
            }

        }

        for (String k :keyMultiplyKeyMapper.keySet()) {
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }
            if(setIndex >0){
                sql.append(", ");
            }
            final Map<String, String> stringDoubleMap = keyDivKeyMapper.get(k);
            for (Map.Entry<String, String> entry : stringDoubleMap.entrySet()) {
                sql.append(dbType.fixKey(k)).append("=").append(entry.getKey()).append(" * ").append(entry.getValue()).append(" ");
                break;
            }

        }



        ThinkQuery query = ThinkQuery.build(updaterMapper.sqlFilter());
        sql.append(" ")
                .append(query.filterQuery());
        values.addAll(query.filterParamValues());
        if(query.filterParamValues().size() ==0){
            throw new ThinkDataRuntimeException("update 语句未指定任何条件，拒绝构建执行Query");
        }
        if( updaterMapper.getUpdateLimit() >0){

            final String sortKey = updaterMapper.sqlFilter().getSortKey();
            sql.append(" ORDER BY ").append(dbType.fixKey(sortKey)).append( updaterMapper.sqlFilter().isDesc()?" DESC" :" ASC");
            sql.append( " LIMIT ?") ;//.append( updaterMapper.getUpdateLimit());
            values.add(updaterMapper.getUpdateLimit());

        }


        return new ThinkExecuteQuery(sql.toString(),values.toArray(new Serializable[values.size()]),null, query.isMaybyEmpty(), tableModal.getBeanClass());
    }


    protected static <T extends SimplePrimaryEntity> ThinkExecuteQuery physicalDeleteSql(ThinkSqlFilter<T> sqlFilter){
        String tableName = tableName(sqlFilter.gettClass());
        tableName = sqlFilter.getDbType().fixKey(tableName);
        StringBuilder sql = new StringBuilder("DELETE FROM ")
                .append(tableName).append(" ");
        ThinkQuery query = ThinkQuery.build(sqlFilter);
        sql.append(query.filterQuery());
        return new ThinkExecuteQuery(sql.toString(),query.filterParamValueArray(),null, query.isMaybyEmpty(),sqlFilter.gettClass());
    }


    /**
     * 计算 快速排序需要支持的 新值
     * @param sourceValue
     * @return
     */
    public static final String computeFastMatchKeyValue(String sourceValue){
        String tempString = StringUtil.getShortPinyinReplaceSymbolWithSpecialCode(sourceValue,"#");
        String[] partStr = StringUtil.extractNumbersAndOtherAsArray(tempString);
        StringBuilder buffer = new StringBuilder("");
        int index = 0 ;
        for (String t : partStr) {
            index ++ ;
            if (StringUtil.isAllNumber(t) && index > 1) {
                while (t.length()<6){
                    t = "0"+t;
                }
            }
            buffer.append(t);
        }
        return buffer.toString();

    }


    public static final String computeSecondaryFastMatchKeyValue(String sourceValue){
        String tempString = StringUtil.getFullPinyinReplaceSymbolWithSpecialCode(sourceValue,"#");
        String[] partStr = StringUtil.extractNumbersAndOtherAsArray(tempString);
        StringBuilder stringBuilder = new StringBuilder("");
        int index = 0 ;
        for (String t : partStr) {
            index ++ ;
            StringBuilder builder =new StringBuilder(t);
            if (StringUtil.isAllNumber(t) && index > 1) {
                while (builder.length()<6){
                    builder.insert(0,"0");
                }
            }
            stringBuilder.append(builder.toString());
        }
        return stringBuilder.toString();
    }

}

