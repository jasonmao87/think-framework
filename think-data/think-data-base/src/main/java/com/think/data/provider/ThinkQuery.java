package com.think.data.provider;

import com.think.common.data.IThinkQueryFilter;
import com.think.common.data.mysql.ThinkFilterBean;
import com.think.common.data.ThinkFilterOp;
import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.util.security.DesensitizationUtil;
import com.think.core.bean.BaseVo;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.core.executor.ThinkThreadExecutor;
import com.think.data.Manager;
import com.think.data.model.ThinkColumnModel;
import com.think.data.model.ThinkTableModel;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class ThinkQuery {

    private String queryStr;

    private boolean maybyEmpty = false;

    private List<ThinkFilterBean>  perfectList ;
    private List<ThinkFilterBean> simpleList ;
    private List<ThinkFilterBean> badList ;

    /**
     * 额外需要支持的 快速排序支持的 键 和 原键
     */
    private List<Map<String,String>> extendFastMatchKeyList;

    private List<Serializable> paramValues;

    private int filterSize = 0 ;

    private ThinkSqlFilter filter ;

    private ThinkQuery() {
        /*这里可能不是唯一入口*/
        if (ThinkThreadExecutor.isDataRegionChange()) {
            String currentRegion =ThinkThreadExecutor.getChangedDataRagionAndRemove();

            if (log.isDebugEnabled()) {

                log.debug("需要调整新的数据分区，原因应该异步任务的线程数据分区更新通知--- 调整为 ：：：{}" , currentRegion);
            }
            Manager.unsafeChangeDataSrv(currentRegion);
        }


        this.perfectList = new ArrayList<>();
        this.simpleList = new ArrayList<>();
        this.badList = new ArrayList<>();
     }
    private void init(ThinkSqlFilter filter){
        this.maybyEmpty = filter.mayBeEmptyResult();
        if(filter.getKeyCondition("enable")==null && filter.getEnableRequired()!=null){
            switch (filter.getEnableRequired()) {
                case MATCH_ENABLE:  {
                    filter.eq("enable",true);
                    break;

                }
                case MATCH_DISABLE:{
                    filter.eq("enable",false);
                    break;
                }
                case MATCH_ALL:{
                    break;
                }
            }
        }
        this.filter = filter ;
        this.optimization();
        this.filterSize = simpleList.size() + badList.size() + perfectList.size();
        this.buildQuery();

    }

    public boolean isMaybyEmpty() {
        return maybyEmpty;
    }

    /**
     * 简单快速 重排 filterBean
     */
    private void optimization(){
        List<ThinkFilterBean> thinkSqlFilterBeanList = filter.getBeans();
        //第一遍检查 需要 快速排序支持的 字段 参数
        for (int i = 0; i < thinkSqlFilterBeanList.size(); i++) {
            this.checkFastMatchAble(thinkSqlFilterBeanList.get(i));
        }

        //暂存待处理列表
        List<ThinkFilterBean> tempList = new ArrayList<>();
        //可能利用到索引的参数集合
        Set<String> indexAbleTempSet = new HashSet<>();
        for(ThinkFilterBean bean: thinkSqlFilterBeanList){
            if(ThinkFilterOp.indexValue(bean.getOp())<50){
                badList.add(bean);
            }else{
                tempList.add(bean);
                indexAbleTempSet.add(bean.getKey());
            }
        }
        // 处理待处理列表
        for(ThinkFilterBean bean : tempList){
            if(this.checkIndexAbleAndSensitive(bean, indexAbleTempSet)){
                this.perfectList.add(bean);
            }else{
                this.simpleList.add(bean);
            }
        }
    }


    /**
     * 检查  快速排序支持的 属性 参数 检查
     * @param bean
     * @return
     */
    private void checkFastMatchAble(ThinkFilterBean bean){
        String k =bean.getKey();
        ThinkTableModel tableModal = Manager.getModelBuilder().get(filter.gettClass()) ;
        ThinkColumnModel columnModal = tableModal.getKey(k);

//        TVerification.valueOf(columnModal.isStateModel()).throwIfTrue("流程状态字段"+columnModal.getKey()+"不能作为查询参数");
        if(columnModal!=null) {


            if (columnModal.isFastMatchAble()) {
                bean.setFastMatchAble(true);
                if (this.extendFastMatchKeyList == null) {
                    this.extendFastMatchKeyList = new ArrayList<>();
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("k", columnModal.getKey());
                map.put("fk", columnModal.getFastMatchKeyWhileExits());
                map.put(columnModal.getFastMatchKeyWhileExits(), columnModal.getKey());
                this.extendFastMatchKeyList.add(map);
            }
        }else{
            if (log.isTraceEnabled()) {
                log.trace("the key : [{}] cannot find column annotation in field  " , k);
            }
        }

    }
    /**
     * 检查 列 索引 是否 有效，即在查询中是否可以利用到索引
     *  同时检查是否脱敏支持
     * @param indexAbleTempSet
     * @return
     */
    private boolean checkIndexAbleAndSensitive(ThinkFilterBean bean ,  Set<String> indexAbleTempSet){
        String k =bean.getKey();
        ThinkTableModel tableModal = Manager.getModelBuilder().get(filter.gettClass()) ;
        ThinkColumnModel columnModal = tableModal.getKey(k);

        if(columnModal.isSensitive()){
            bean.setSensitive();
        }
        if(columnModal.isPk()){
            return true;
        }
        if(columnModal.isHasIndex()){
            if(log.isTraceEnabled()) {
                log.trace("check {} hasIndex {}",columnModal.getKey(),columnModal.isHasIndex());
            }
            String leftKey =columnModal.leftIndexKey();
            if(log.isTraceEnabled()){
                log.trace("try to find {} left key  ->{}",k,leftKey);
            }
            if(leftKey.equalsIgnoreCase(k)){
                return true;
            }else{
                while (false == leftKey.equalsIgnoreCase( tableModal.getKey(leftKey).leftIndexKey())){
                    leftKey =  tableModal.getKey(leftKey).leftIndexKey() ;
                    if(!indexAbleTempSet.contains(leftKey)){
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static ThinkQuery build(ThinkSqlFilter filter){
        ThinkQuery query = new ThinkQuery();
        IThinkQueryFilter thinkQueryFilter = Manager.getThinkQueryFilter();
        if(thinkQueryFilter !=null){
            thinkQueryFilter.translateSqlFilter(filter);
        }
        query.init(filter);
        return query;
    }

    private void buildQuery(){
        this.queryStr = null;
        this.paramValues = new ArrayList<>();
        StringBuilder sb = new StringBuilder(" ");
        if(filterSize > 0){
            sb.append("WHERE ");
        }

        //append prefect
        this.appendFilterParamList(perfectList,sb,false);
        //append fastMatch
        this.appendFastMatchSupport(sb,paramValues.size()>0);
        //append simple
        this.appendFilterParamList(simpleList,sb,paramValues.size()>0);
        //append bad
        this.appendFilterParamList(badList,sb,paramValues.size()>0);
        sb.append(" ");
        /**
         * 处理KEY OR的逻辑
         */
        this._appendKeyOr(sb);
        queryStr = sb.toString();
    }


    private void _appendKeyOr(StringBuilder sb){
        List<ThinkFilterBean> filterKeyOrBeans = filter.getKeyOrBeans();
        if(!filterKeyOrBeans.isEmpty() && filterKeyOrBeans.size() >1){
            if(paramValues.size()>0){
                sb.append("AND ");
            }
            int index = 0 ;
            sb.append("( ");

            for (ThinkFilterBean filterBean : filterKeyOrBeans) {
                if(index>0){
                    sb.append("OR ");
                }
                String k  = filterBean.getKey();
                Serializable v =  filterBean.getValues()[0];




                String sv = null;
                try{
                    ThinkColumnModel columnModel = Manager.getModelBuilder().get(filter.gettClass()).getKey(k);

                    if(v instanceof String  && columnModel.isSensitive()){
                        sv = (String) v;
                        sv =DesensitizationUtil.encodeWithIgnore((String) v, '%');
                    }else{
                        sv = (String) v;
                    }
                }catch (Exception e){}
                if(  v instanceof String  && filter.isKeyOrTypeUsingLike()){
                    this.checkFastMatchAble(filterBean);
                    sb.append(" ").append(k).append(" LIKE ? ");
                    paramValues.add(sv);
                    if (filterBean.isFastMatchAble()) {
                        sb.append("OR fs_").append(k).append(" LIKE ? ");
                        sb.append("OR fss_").append(k).append(" LIKE ? ");
                        paramValues.add(reDoStringAsFastMatchForQuery((String) v,false));
                        paramValues.add(reDoStringAsFastMatchForQuery((String) v,true));
                    }
                }else{
                    sb.append(filterBean.getQueryPart());
                    paramValues.add(v);
                }


                index ++ ;



            }//end of for
            sb.append(") ");



        }



//
//        // 处理 key or 逻辑 ....
//        Map<String, Serializable> keyOrMap = this.filter.getKeyOrMap();
//        boolean op_keyOrLike = false;
//        if(!keyOrMap.isEmpty() && keyOrMap.size()>1) {
//
//            if(paramValues.size()>0){
//                sb.append(" AND ");
//            }
//            sb.append(" (");
//            int index = 0;
//
//
//
//            for (Map.Entry<String, Serializable> kv : keyOrMap.entrySet()) {
//
//                if(index>0){
//                    sb.append("OR ");
//                }
//                if( filter.isKeyOrTypeUsingLike()){
//                    sb.append(" ").append(kv.getKey()).append(" LIKE ").append("? ");
//                }else {
//                    sb.append(" ").append(kv.getKey()).append("=").append("? ");
//                }
//                Serializable v = kv.getValue();
//                try{
//                    ThinkColumnModel columnModel = Manager.getModelBuilder().get(filter.gettClass()).getKey(kv.getKey());
//                    if(columnModel.isSensitive()){
//                        v =DesensitizationUtil.encodeWithIgnore((String) v, '%');
//                    }
//                }catch (Exception e){}
//                paramValues.add(v);
//
//                index++;
//            }
//            sb.append(") ");
//        }

    }


    private void appendFastMatchSupport(StringBuilder sb, boolean startWithAnd){

        if(this.extendFastMatchKeyList != null && this.extendFastMatchKeyList.size()>0){
            int index = 0 ;
            for (Map<String, String> map : this.extendFastMatchKeyList) {
                String sourceKey = map.get("k");
                String fastMatchKey = map.get("fk");
                ThinkFilterBean keyCondition = filter.getKeyCondition(sourceKey);
                String sqlPart =""+ keyCondition.getQueryPart();
                sqlPart = sqlPart.replaceFirst(sourceKey,fastMatchKey);
                if(index > 0 || startWithAnd){
                    sb.append("AND (");
                }else{
                    sb.append(" (");
                }
                index ++ ;
                sb.append(sqlPart);
                Serializable[] vaules = keyCondition.getValues();
                for(int i=0 ;i < vaules.length; i++){
                    String v =(String) vaules[i];
                    String redoValue  = reDoStringAsFastMatchForQuery(v,false);
                    //快排支持情况下，忽略  脱敏 ！
//                    if(true ==keyCondition.isSensitive() ){
//                        //处理  sort 支持 值  ----
//                        redoValue = reDoStringAsFastMatchForQuery(DesensitizationUtil.encodeWithIgnore(v,'%'),false);
//                    }else {
//                        //处理  sort 支持 值  ----
//                        redoValue = reDoStringAsFastMatchForQuery(v,false);
//                    }
                    paramValues.add(redoValue);
                }   // end of inner for

                // secondary key append
                String sqlPartSecondary  = sqlPart.replaceFirst("fs_","fss_");
                sb.append("OR ").append(sqlPartSecondary);
                sb.append(" ) ");
                for(int i=0 ;i < vaules.length; i++){
                    String v =(String) vaules[i];
                    String redoValue = reDoStringAsFastMatchForQuery(v,true);
//                    if(true ==keyCondition.isSensitive()){
//                        //处理  sort 支持 值  ----
//                        redoValue = reDoStringAsFastMatchForQuery(DesensitizationUtil.encodeWithIgnore(v,'%'),true);
//                    }else {
//                        //处理  sort 支持 值  ----
//                        redoValue = reDoStringAsFastMatchForQuery(v,true);
//                    }
                    paramValues.add(redoValue);
                }



            } // end of FOR
        }//end of if
//        /**清空 快速匹配 支持list */
    }



    private void appendFilterParamList(List<ThinkFilterBean> list,StringBuilder sb ,boolean startWithAnd ){
        int appendIndex  =0 ;
        for(ThinkFilterBean bean : list){

            //是否需要拼接
            boolean doAppendAble = true;
            if(bean.isFastMatchAble() ){
                if(filter.isStrictFastMatch() == false){
                    doAppendAble = false;
                }

            }

            if(doAppendAble) {
                if( startWithAnd  || ( appendIndex > 0)){
                    sb.append("AND ");
                }
                appendIndex ++ ;
                sb.append(bean.getQueryPart()).append(" ");
                int valueLen = bean.getLen();
                Serializable[] values = bean.getValues();

                for (int i = 0; i < valueLen; i++) {

                    if (bean.isSensitive()) {
                        Serializable v = values[i];
                        if (v instanceof String) {

                            paramValues.add(DesensitizationUtil.encodeWithIgnore((String) v, '%'));
                        } else {
                            paramValues.add(values[i]);
                        }
                    } else {
                        paramValues.add(values[i]);
                    }
                }
            }





        }

    }


    private <T extends _Entity> String tableName(Class<T> cls){
        // 由 唯一出入口 作为唯一出口
        return "#tbName#" ; //DataModalBuilder.tableName(cls);
    }
    public <T extends _Entity> ThinkExecuteQuery countQuery(Class<T> cls){
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) as COUNT_RESULT FROM ")
                .append( tableName(cls)).append(" ")
                .append(queryStr);
        Serializable[] values = paramValues.toArray(new Serializable[paramValues.size()]);
        return new ThinkExecuteQuery(sb.toString(),values,this.filter.getResultFilterList(),isMaybyEmpty());
    }

    public <T extends _Entity> ThinkExecuteQuery selectFullKeys(Class cls){
        String keys = "*";
        return selectForKeys(cls,keys) ;
     }


     public <T extends _Entity,V extends BaseVo<T>> ThinkExecuteQuery selectForVo(Class<T> cls,  Class<V> voClass ){
        List<String> keyList =new ArrayList<>();
        ThinkTableModel tableModal = Manager.getModelBuilder().get(cls);
        for(Field field : ClassUtil.getFieldList(voClass)){
            if(tableModal.containsKey(field.getName())) {
                keyList.add(field.getName());
            }
        }
         return selectForKeys(cls,keyList.toArray(new String[keyList.size()])) ;
     }

     public <T extends _Entity> ThinkExecuteQuery selectForKeys(Class<T> cls, String... keys){

        String keyStr ;
        if(keys == null){
            keyStr = "*";
        }else if(keys.length == 1){
            keyStr = keys[0];
        }else{
            StringBuilder ksb = new StringBuilder("");
            for (int i = 0; i <keys.length ; i++) {
                if(i >0){
                    ksb.append(",");
                }
                ksb.append(keys[i]);
            }
            keyStr = ksb.toString();
        }
        String sortKey = filter.getSortKey();
        try {
            ThinkTableModel model = Manager.getModelBuilder().get(filter.gettClass());
            if (model.getKey(sortKey).isFastMatchAble()) {
                sortKey = model.getKey(sortKey).getFastMatchKeyWhileExits();
            }
        }catch (Exception e){}


         StringBuilder sb = new StringBuilder("SELECT  ")
                .append(keyStr).append(" ")
                .append("FROM ").append( tableName(cls)).append(" ")
                .append(queryStr).append(" ")
                .append("ORDER BY ")
                .append(sortKey ).append(" ")
                .append( filter.isDesc()?"DESC":"ASC")
                .append(" ");
        List<Serializable> valueTempList = new ArrayList<>();
        valueTempList.addAll(this.paramValues);
        if(filter.getStart() > 0 && filter.getLimit() > 0){
            sb.append("LIMIT ? ?");
            valueTempList.add(filter.getStart());
            valueTempList.add(filter.getLimit());
        }else if(filter.getStart() < 1){
            if(filter.getLimit() >0){
                sb.append("LIMIT ? ");
                valueTempList.add(filter.getLimit());
            }
        }
        return new ThinkExecuteQuery(sb.toString(),valueTempList.toArray(new Serializable[valueTempList.size()]),this.filter.getResultFilterList(),isMaybyEmpty());
     }


    public <T extends _Entity> ThinkExecuteQuery selectCount(){

        StringBuilder sb = new StringBuilder("SELECT  ")
                .append(" COUNT(*) as COUNT_RESULT ")
                .append("FROM ").append( tableName( null )).append(" ")
                .append(queryStr).append(" ");
        List<Serializable> valueTempList = new ArrayList<>();
        valueTempList.addAll(this.paramValues);
        return new ThinkExecuteQuery(sb.toString(),valueTempList.toArray(new Serializable[valueTempList.size()]),this.filter.getResultFilterList(),isMaybyEmpty());

    }


     public String filterQuery(){
        return this.queryStr;
     }

     public Serializable[] filterParamValueArray(){
        return this.paramValues.toArray(new Serializable[paramValues.size()]);
     }

     public List<Serializable> filterParamValues(){
        return this.paramValues;
     }


     private static String reDoStringAsFastMatchForQuery(String sourceValueStr,boolean isFull){
        StringBuilder resultSb = new StringBuilder();
        String[] arrays =  sourceValueStr.split("%");
        boolean endSign = sourceValueStr.endsWith("%");
        int index = 0 ;
        for(String ts : arrays){
            if(index>0){

                resultSb.append("%");
            }
            if(isFull){
                ts = ThinkUpdateQueryBuilder.computeSecondaryFastMatchKeyValue(ts);
            }else {
                ts = ThinkUpdateQueryBuilder.computeFastMatchKeyValue(ts);
            }
            resultSb.append(ts);
            index ++ ;
        }

        if(endSign){
            resultSb.append("%");
        }
        return resultSb.toString();
     }

    public ThinkSqlFilter getFilter() {
        return filter;
    }



}
