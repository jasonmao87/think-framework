package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.common.util.security.DesensitizationUtil;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.data.Manager;
import com.think.data.exception.ThinkDataRuntimeException;
import com.think.data.model.ThinkColumnModel;
import com.think.data.model.ThinkTableModel;
import com.think.data.verification.ThinkDataValidator;

import java.io.Serializable;
import java.util.*;

public class ThinkUpdateQueryBuilder {

    private static String tableName(Class cls){
        return "#tbName#";
//        return DataModalBuilder.tableName(cls);
    }
    /**
     * Nomore 中所有的ID都是自增的！ 所以 inset 不会有id的信息
     * @param t
     * @return
     */
    protected static final <T extends _Entity> ThinkExecuteQuery insertOneSQL(T t) {
        ThinkTableModel modal =
                Manager.getModelBuilder().get(t.getClass());
        ThinkColumnModel[] clms = modal.getColumnModels();
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(tableName(modal.getBeanClass())).append("( ");
        List<Object> values = new ArrayList<>( );
        int i = 0 ;
        for(ThinkColumnModel cm : clms){
            Object v = ClassUtil.getProperty(t,cm.getKey());
            if(cm.getKey().equalsIgnoreCase("id")){
                if(v == null) {
                    continue;
                }
            }
            if(v == null){
                if(cm.isNullable()){}
                if(cm.getType() == String.class){
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

                sql.append(cm.getKey());

                /**
                 * 值 校验
                 */
                if(ThinkDataValidator.isEnable()){
                    //值 校验
                    ThinkDataValidator.verification(t.getClass(), cm.getKey(), v);
                }
                /**
                 * 脱敏处理
                 */
                if(cm.isSensitive() && v instanceof String ){
                    v = DesensitizationUtil.encode((String) v);
                }
                /**
                 * 脱敏处理
                 */

                values.add(v);
                i++;

                if(cm.isFastMatchAble()){
                    sql.append(", ").append(cm.getFastMatchKeyWhileExits());
                    String sv =  computeFastMatchKeyValue((String)v);
                    values.add(sv);
                    i++ ;
                    // second key
                    sql.append(", ").append(cm.getSecondaryFastMatchKeyWhileExits());
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
        return  new ThinkExecuteQuery(sql.toString(),values.toArray(new Serializable[values.size()]),null);
    }


    protected static final <T extends _Entity> ThinkExecuteQuery batchInsertSQL(List<T> list){
        T t = list.get(0);
        ThinkTableModel modal = Manager.getModelBuilder().get(t.getClass());
        String tableName =tableName(modal.getBeanClass());
        ThinkColumnModel[] clms = modal.getColumnModels();
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(tableName).append("( ");
        List<Object> values = new ArrayList<>( );
        int i = 0 ;
        int outIndex = 0 ;
        List<String> clmNames = new ArrayList<>();
        for(ThinkColumnModel cm : clms){
            Object v = ClassUtil.getProperty(t,cm.getKey());
            if(cm.getKey().equalsIgnoreCase("id")){
                if(v == null) {
                    continue;
                }
            }
            if(cm.isFastMatchAble()){

            }
            if(i >0){
                sql.append(", ");
            }
            sql.append(cm.getKey());
            clmNames.add(cm.getKey());
            i++;


        }

        // 快速匹配支持  >>>>>>>>>>>>>
        for(String k : modal.getSortKeyArray()){
            ThinkColumnModel columnModel = modal.getKey(k);

            String sortKeyName = columnModel.getFastMatchKeyWhileExits();  //modal.getSortKeyName(k);
            sql.append(" ," ).append(sortKeyName) ;
            String secondKeyName = columnModel.getSecondaryFastMatchKeyWhileExits();
            sql.append(" ,").append(secondKeyName);

        }
        // 快速排序支持  <<<<<<<<<<<<<<<<<<<<

        sql.append(") values");
        for(T tmp : list){
            if(outIndex >0){
                sql.append(",");
            }
            sql.append("(");
            for (int x = 0; x < i; x++) {
                if (x > 0) {
                    sql.append(",");
                }
                sql.append("? ");
                Object v = ClassUtil.getProperty(tmp,clmNames.get(x));
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
                     * 值校验
                     */

                    /**
                     * 脱敏处理
                     */
                    if (clms[x].isSensitive() && v instanceof String) {
                        v = DesensitizationUtil.encode((String) v);
                    }
                    /**
                     * 脱敏处理
                     */
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
        }// end of for
        return new ThinkExecuteQuery(sql.toString(),values.toArray(new Serializable[values.size()]),null);
    }

    protected static <T extends _Entity> ThinkExecuteQuery updateSql(T t){
        List<Serializable> valuesList = new ArrayList<>();
        ThinkTableModel modal = Manager.getModelBuilder().get(t.getClass());
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(tableName(modal.getBeanClass())).append(" SET");
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
                    if(ThinkDataValidator.isEnable()){
                        //值 校验
                        ThinkDataValidator.verification(t.getClass(),columnModal.getKey(),v);
                    }

                    /**
                     * 脱敏处理
                     */
                    if(columnModal.isSensitive() && v instanceof String ){
                        v = DesensitizationUtil.encode((String) v);
                    }
                    /**
                     * 脱敏处理
                     */


                    if(index > 0){
                        sql.append(",");
                    }
                    sql.append(" ").append(columnModal.getKey()).append(" = ?");
                    valuesList.add((Serializable)v);
                    index ++ ;

                    //如果支持排序支持 开始
                    if(columnModal.isFastMatchAble()){
                        String fastMatchKeyName= columnModal.getFastMatchKeyWhileExits();
                        sql.append(",").append( fastMatchKeyName).append(" = ? ");
                        valuesList.add(computeFastMatchKeyValue((String) v));
                        // second key
                        String secoondKey = columnModal.getSecondaryFastMatchKeyWhileExits();
                        sql.append(",").append( secoondKey).append(" = ? ");
                        valuesList.add(computeSecondaryFastMatchKeyValue((String) v));


                    }
                    //如果支持排序支持  结束


                }
            }
        }
        sql.append(" WHERE id = ?");
        valuesList.add(t.getId());
        return new ThinkExecuteQuery(sql.toString(),valuesList.toArray(new Serializable[valuesList.size()]),null);
    }


    protected static  <T extends _Entity> ThinkExecuteQuery updateSql(ThinkUpdateMapper<T> updaterMapper ){
        StringBuilder sql = new StringBuilder("UPDATE ")
                .append(tableName(updaterMapper.sqlFilter().gettClass())).append(" SET ");
        List<Object> values = new ArrayList<>();

        ThinkTableModel tableModal = Manager.getModelBuilder().get(updaterMapper.getTargetClass());
        int setIndex = 0 ;
        Map<String, Object> incMap = updaterMapper.getIncMapper();
        Map<String, Object> setMapper = updaterMapper.getSetMapper();
        Map<String, String> setKeyMapper = updaterMapper.getSetKeyMapper();
        for(String k : incMap.keySet() ){
            ThinkColumnModel columnModal = tableModal.getKey(k);
            if(columnModal== null || columnModal.isEditAble()==false ){
                continue;
            }


            if(setIndex >0){
                sql.append(", ");
            }
            sql.append( k).append(" = ").append(k).append("+? ");
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
            /**
             * 脱敏处理
             */
            if(columnModal!=null && columnModal.isSensitive() && v instanceof String ){
                v = DesensitizationUtil.encode((String) setMapper.get(k) );
            }
            /**
             * 脱敏处理
             */
            if(setIndex >0){
                sql.append(", ");
            }
            sql.append( k).append(" = ").append(" ? ");
            values.add( v );
            setIndex ++ ;

            //如果支持排序支持 开始
            if(columnModal.isFastMatchAble()){
                String fastMatchKeyName= columnModal.getFastMatchKeyWhileExits();
                sql.append(",").append(fastMatchKeyName).append(" = ? ");
                values.add(computeFastMatchKeyValue((String) v));

                // second key
                String secoondKey = columnModal.getSecondaryFastMatchKeyWhileExits();
                sql.append(",").append( secoondKey).append(" = ? ");
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
            sql.append( k).append(" = ").append(setKeyMapper.get(k)).append(" ");
            setIndex ++ ;
        }
        ThinkQuery query = ThinkQuery.build(updaterMapper.sqlFilter());
        sql.append(" ")
                .append(query.filterQuery());
        values.addAll(query.filterParamValues());
        if(query.filterParamValues().size() ==0){
            throw new ThinkDataRuntimeException("update 语句未指定任何条件，拒绝构建执行Query");

        }
        return new ThinkExecuteQuery(sql.toString(),values.toArray(new Serializable[values.size()]),null);
    }


    protected static <T extends SimplePrimaryEntity> ThinkExecuteQuery physicalDeleteSql(ThinkSqlFilter<T> sqlFilter){
        StringBuilder sql = new StringBuilder("DELETE FROM ")
                .append(tableName(sqlFilter.gettClass())).append(" ");
        ThinkQuery query = ThinkQuery.build(sqlFilter);
        sql.append(query.filterQuery());
        return new ThinkExecuteQuery(sql.toString(),query.filterParamValueArray(),null);
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

