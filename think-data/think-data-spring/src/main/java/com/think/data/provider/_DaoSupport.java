package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.util.IdUtil;
import com.think.common.util.StringUtil;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.data.Manager;
import com.think.data.ThinkDataRuntime;
import com.think.data.filter.ThinkDataFilter;
import com.think.data.model.DataModelBuilder;
import com.think.data.model.ThinkTableModel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class _DaoSupport{
    private static final List<String> tableInitSQLList = new ArrayList<>();

    protected static <T extends _Entity> void callInsert(T t){
        ThinkDataRuntime rt = Manager.getDataSrvRuntimeInfo();
        doCallInsert(t,rt);
    }


    protected static <T extends _Entity> void callInsert(List<T> tList){
        ThinkDataRuntime rt = Manager.getDataSrvRuntimeInfo();
        for(T t :tList){
            doCallInsert(t,rt);
        }
    }

    private static  <T extends _Entity> void doCallInsert(T t ,ThinkDataRuntime rt ){
        for(ThinkDataFilter filter : Manager.filters()){
            filter.beforeExecuteInsert(t);
        }
        if( rt!=null){
            if(StringUtil.isNotEmpty(rt.getPartitionRegion()) && !ThinkDataRuntime.isNonePartitionRegion(rt.getPartitionRegion())){
                t.setPartitionRegion(rt.getPartitionRegion());
            }
        }
        //处理 thinkLinkedId
        if(StringUtil.isEmpty(t.getThinkLinkedId())){
            try{
                t.setThinkLinkedId(IdUtil.nextShortId());
            }catch (Exception e){}
        }
    }

    protected static <T extends _Entity> void callUpdate(T t, ThinkUpdateMapper mapper){
        for(ThinkDataFilter filter :Manager.filters()){
            if(t !=null){
                filter.beforeExecuteUpdateEntity(t);
            }else {
                filter.beforeExecuteUpdateMapper(mapper);
            }
        }
    }


    /**
     * 获取 基础表名称
     * @param tClass
     * @param <T>
     * @return
     */
    protected static <T extends _Entity> String baseTableName( Class<T> tClass){
        return finalSplitTableName(tClass,-1);
    }

    protected static <T extends _Entity>  String finalSplitTableName(Class<T> tClass ,int splitYear){
        String baseTableName =  DataModelBuilder.tableName(tClass) ;
        String tableName = null;
        if(splitYear > 2000) {
            tableName= baseTableName+ "_split_" + splitYear;
        }else{
            tableName= baseTableName;
        }
        return tableName;
    }


    /**
     * 通过id 计算 准确的分割年份
     * @param id
     * @return
     */
    protected static final int computeSpiltYearById(long id){
        //转交给split 工具 处理
        return _SplitTableSupport.computeSpiltYearById(id);
    }


    /**
     * 通过filter 计算数据可能存在表空间分割年份
     * @param sqlFilter
     * @param showSplitTables
     * @param <T>
     * @return
     */
    protected static final  <T extends SimplePrimaryEntity> int[] possibleSplitYears(ThinkSqlFilter<T> sqlFilter, List<String> showSplitTables){
        return _SplitTableSupport.possibleSplitYears(sqlFilter,showSplitTables);
    }

    /**
     * 提取 VO中的 KEY (如果vo中没有Id 字段，但是数据库查询语句中，会多出一个id key ，id 为必传KEY ，)
     * @param targetClass
     * @param voClass
     * @param <T>
     * @param <V>
     * @return
     */
    protected static final  <T extends _Entity ,V extends BaseVo<T>> String[] voKeys(Class<T> targetClass, Class<V> voClass){
        List<String> keys = new ArrayList<>();
        List<Field> list =  ClassUtil.getFieldList(voClass);
        boolean containsId = false;
        ThinkTableModel tableModal = Manager.getModelBuilder().get(targetClass);
        for(Field f : list){
            if(tableModal.containsKey(f.getName())){
                String k = f.getName();
                keys.add(k);
                if(k.equalsIgnoreCase("id")){
                    containsId = true;
                }
            }
        }
        if(containsId == false){
            keys.add(0,"id");
        }
        if(keys.size() > 0) {
            return keys.toArray(new String[keys.size()]);
        }else{
            return new String[]{"*"};
        }
    }


    protected static final <T extends _Entity> Long[] getIdArray(List<T> list){
        if(list!=null && list.size()>0) {
            List<Long> idList = new ArrayList<>(list.size());
            for (T t : list) {
                if (t.getId() != null) {
                    idList.add(t.getId());
                }
            }
            return idList.toArray(new Long[idList.size()]);
        }
        return new Long[]{};
    }


}
