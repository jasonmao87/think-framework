package com.think.common.data.mongo;

import com.alibaba.fastjson.JSONObject;
import com.think.common.data.IFilterChecker;
import com.think.common.data.ThinkMongoFilterOp;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkIgnore;
import com.think.core.bean.SimpleMongoEntity;
import com.think.core.bean.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ThinkMongoQueryFilter<T extends SimpleMongoEntity> implements Serializable {

    private static final long serialVersionUID = -483715413545869712L;

    private static IFilterChecker iFilterChecker = null;

    @Remark("目标对象class")
    private Class<T> tClass;

    @Remark("排序key")
    private String sortKey ="id";

    @Remark("是否倒叙")
    private boolean desc = true;

    @Remark("跳过多少记录，不建议使用，建议直接用 0 ")
    private int start = 0;

    @Remark("筛选记录数，limit ，-1代表不分页  ")
    private int limit  = 10 ;

    @Remark("Group key")
    private String[] groupByKeys ;

    private List<ThinkMongoFilterBean> beans = new ArrayList<ThinkMongoFilterBean>();

    private Map<String,Object> modifyUpdateMapper = new HashMap<>();
    private Map<String,Number> modifyIncMapper =new HashMap<>();

    public List<ThinkMongoFilterBean> getBeans() {
        return this.beans;
    }

    private ThinkMongoQueryFilter(Class<T> tClass) {
        this.tClass = tClass;
    }

    public static <T extends SimpleMongoEntity> ThinkMongoQueryFilter<T> build(Class<T> tClass){
        return new ThinkMongoQueryFilter<>(tClass);
    }
    public static <T extends SimpleMongoEntity> ThinkMongoQueryFilter<T> build(Class<T> tClass, int limit){
        ThinkMongoQueryFilter<T> sqlFilter =  new ThinkMongoQueryFilter<>(tClass);
        sqlFilter.limit = limit;
        return sqlFilter;
    }

    public static <T extends SimpleMongoEntity> ThinkMongoQueryFilter<T> parseFromJSON(String filterJson , Class<T> tClass){
        JSONObject jsonObject = JSONObject.parseObject(filterJson);
        ThinkMongoQueryFilter filter = new ThinkMongoQueryFilter(tClass);
        if (jsonObject.containsKey("limit")) {
            filter.limit = jsonObject.getInteger("limit");
        }
        Map<String, String> sortQuery = (Map<String, String>) jsonObject.getOrDefault("sortQuery", new HashMap<>());
        String sortKey = sortQuery.getOrDefault("key","id");
        if(sortQuery.getOrDefault("sort","desc").equalsIgnoreCase("desc")){
            filter.sortDesc(sortKey);
        }else {
            filter.sortAsc(sortKey);
        }
        if (jsonObject.containsKey("filterBody")) {

            JSONObject filterMap = jsonObject.getJSONObject("filterBody");
            if(log.isDebugEnabled()) {
                log.debug("filterBody:{}",filterMap);
            }
            if(filterMap!=null){
                for(String k : filterMap.keySet()){
                    String realKey =  null;
                    if(k.contains("##")){
                        realKey = k.split("##")[1];
                    }else{
                        realKey = k;
                    }
                    ThinkMongoFilterBean bean = ThinkMongoFilterBean.parseFromJSON(realKey,filterMap.getJSONObject(k)) ;
                    if(bean !=null){
                        filter.beans.add(bean);
                    }
                }
            }
        }
        return filter;
    }

    public ThinkMongoQueryFilter<T> eq(String k , Serializable v){
        this._append(k, ThinkMongoFilterOp.EQ,v);
        return this;
    }


    public ThinkMongoQueryFilter<T> lessThan(String k , Serializable v){
        this._append(k,ThinkMongoFilterOp.LE,v);
        return this;
    }


    public ThinkMongoQueryFilter<T> lessThanAndEq(String k , Serializable v){
        this._append(k,ThinkMongoFilterOp.LEE,v);
        return this;
    }



    public ThinkMongoQueryFilter<T> largeThan(String k , Serializable v){
        this._append(k,ThinkMongoFilterOp.LG,v);
        return this;
    }




    public ThinkMongoQueryFilter<T> largeThanAndEq(String k , Serializable v){
        this._append(k,ThinkMongoFilterOp.LGE,v);
        return this;
    }

    public ThinkMongoQueryFilter<T> in(String k , Serializable... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            this._append(k,ThinkMongoFilterOp.IN,v);
        }
        return this;
    }

    public ThinkMongoQueryFilter<T> or(String k , Serializable... v){
        if(v==null || v.length<2){
            if(log.isDebugEnabled()){
                log.debug("or 语法必须包含至少2个或2个以上的参数值");
            }
            return this;
        }
        this._append(k,ThinkMongoFilterOp.OR,v);
        return this;
    }


    public ThinkMongoQueryFilter<T> betweenAnd(String k , Serializable v1, Serializable v2){
        if(v1!=null && v2!=null) {
            this._append(k, ThinkMongoFilterOp.BETWEEN_AND, v1,v2);
        }else {
            if (log.isDebugEnabled()) {
                log.debug("BETWEEN AND 参数均不能为NULL");
            }
        }
        return this;
    }


    public ThinkMongoQueryFilter<T> like(String k, String likeStr){
        this._append(k,ThinkMongoFilterOp.LIKE,likeStr);
        return this;
    }

    private ThinkMongoQueryFilter<T> _append(String key, ThinkMongoFilterOp op, Serializable... values){
        if(key.equalsIgnoreCase("id") || key.equalsIgnoreCase("_id")){
            if(values!=null && values.length>0){
                String[] strValues = new String[values.length];
                for(int i=0;i<values.length;i++){
                    strValues[i] = values[i] + "";
                }
                return _append("_id",op,strValues);
            }


            //
        }

        if(values == null){
            if(log.isDebugEnabled()){
                log.debug("THINK SQL FILTER 除了 NULL ，IS_NOT_NULL 外 ，参数均不能为NULL");
            }
            return this;
        }
        if(check(key)){
            ThinkMongoFilterBean filterBean = ThinkMongoFilterBean.build(key).initOp(op,values);
            this.beans.add(filterBean);
        }else{
            if(log.isDebugEnabled()){
                log.debug("key:{} 不存在于对象{}，被忽略 " ,key,tClass.getName() );
            }
        }
        return this;
    }

//
//    public ThinkMongoQueryFilter groupBy(String... keys){
//        this.groupByKeys= keys;
//        return this;
//    }

    public ThinkMongoQueryFilter sortDesc(String sortKey){
        if(check(sortKey)){
            this.sortKey = sortKey;
            this.desc =true;
        }
        return this;
    }

    public ThinkMongoQueryFilter sortAsc(String sortKey){
        if(check(sortKey)){
            this.sortKey = sortKey;
            this.desc = false;
        }
        return this;
    }

    public ThinkMongoQueryFilter updateLimit(int limit){
        this.limit = limit;
        return this;
    }

    public ThinkMongoQueryFilter findAndModifyUpdate(String k ,Object v){
        this.modifyUpdateMapper.put(k,v);
        return this;
    }

    public ThinkMongoQueryFilter findANdModifyInc(String k,Number inc){
        this.modifyIncMapper.put(k,inc);
        return this;
    }



    public Map<String, Object> getModifyUpdateMapper() {
        return modifyUpdateMapper;
    }

    public Map<String, Number> getModifyIncMapper() {
        return modifyIncMapper;
    }

    /**
     * 检查 是否 是合法字段
     * @param key
     * @return
     */
    private boolean check(String key){
        if(key.equals("_id")){
            return true;
        }
        if(iFilterChecker == null) {
            Field field = ClassUtil.getField(tClass, key);
            if (field == null) {
                return false;
            }
            if (field.getAnnotation(ThinkIgnore.class) != null) {
                return false;
            }
            return true;
        }else {
            return iFilterChecker.checkKey(key,tClass);
        }
    }
    public static final void setiFilterChecker(IFilterChecker checker){
        iFilterChecker = checker;
    }

    public Class<T> gettClass() {
        return tClass;
    }

    public int getLimit() {
        return limit;
    }

    public int getStart() {
        return start;
    }

    public String getSortKey() {
        return sortKey;
    }

    public boolean isDesc() {
        return desc;
    }


    /**
     * 2020-11-17 新增 基本数据类型支持
     * */
    /**
     * 2020 -11 -17 新增 基础 数据类型新增
     *
     */

    public ThinkMongoQueryFilter<T> in(String k , long... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Long[] vArray = new Long[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Long.valueOf(v[i]);
            }
            this._append(k, ThinkMongoFilterOp.IN,vArray);
        }
        return this;
    }

    public ThinkMongoQueryFilter<T> in(String k , int... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Integer[] vArray = new Integer[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Integer.valueOf(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.IN,vArray);
        }
        return this;
    }
    public ThinkMongoQueryFilter<T> in(String k , float... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Float[] vArray = new Float[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Float(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.IN,vArray);
        }
        return this;
    }
    public ThinkMongoQueryFilter<T> in(String k , double... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Double[] vArray = new Double[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Double(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.IN,vArray);
        }
        return this;
    }

    public ThinkMongoQueryFilter<T> in(String k , short... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Short[] vArray = new Short[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Short.valueOf(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.IN,vArray);
        }
        return this;
    }
    public ThinkMongoQueryFilter<T> in(String k , boolean... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Boolean[] vArray = new Boolean[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Boolean.valueOf(v[i]);// new Boolean(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.IN,vArray);
        }
        return this;
    }

    /** -----------------OR -------------------------------*/

    public ThinkMongoQueryFilter<T> or(String k , long... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Long[] vArray = new Long[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Long.valueOf(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.OR,vArray);
        }
        return this;
    }

    public ThinkMongoQueryFilter<T> or(String k , int... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Integer[] vArray = new Integer[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]=  Integer.valueOf(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.OR,vArray);
        }
        return this;
    }
    public ThinkMongoQueryFilter<T> or(String k , float... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Float[] vArray = new Float[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Float(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.OR,vArray);
        }
        return this;
    }
    public ThinkMongoQueryFilter<T> or(String k , double... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Double[] vArray = new Double[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Double(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.OR,vArray);
        }
        return this;
    }

    public ThinkMongoQueryFilter<T> or(String k , short... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Short[] vArray = new Short[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]=  Short.valueOf(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.OR,vArray);
        }
        return this;
    }
    public ThinkMongoQueryFilter<T> or(String k , boolean... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Boolean[] vArray = new Boolean[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]=Boolean.valueOf(v[i]);
                // new Boolean(v[i]);
            }
            this._append(k,ThinkMongoFilterOp.OR,vArray);
        }
        return this;
    }

    public boolean containsUpdate(){
        return  this.getModifyUpdateMapper().size() + this.getModifyIncMapper().size() >0;
    }



    public boolean containsKey(String key){
        for (ThinkMongoFilterBean bean : this.getBeans()) {
            if(bean.getKey().equalsIgnoreCase(key)){
                return true;
            }
        }
        return false;


    }
}
