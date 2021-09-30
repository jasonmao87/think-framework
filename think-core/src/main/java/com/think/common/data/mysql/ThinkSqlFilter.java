package com.think.common.data.mysql;

import com.alibaba.fastjson.JSONObject;
import com.think.common.data.IFilterChecker;
import com.think.common.data.ThinkFilterOp;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkIgnore;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.core.enums.TEnableRequired;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class ThinkSqlFilter<T extends _Entity> implements Serializable {

    private static final long serialVersionUID = 3347480035180588760L;

    private static IFilterChecker filterChecker = null;

    private List<IThinkResultFilter> resultFilterList;

    @Remark("针对enable的匹配要求")
    private TEnableRequired enableRequired = null;

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

    @Remark("限制查询年份，仅对按年切分表有效")
    private int filterSplitYear ;


    @Remark(value = "支持快速匹配的严格模式",description = "true 时候为严格匹配，如果 false，不知道 可以 匹配到 不指导")
    private boolean strictFastMatch = false;

    @Remark("Group key")
    private String[] groupByKeys ;

    private List<ThinkFilterBean> beans = new ArrayList<>();

    private Map<String,Serializable> keyOrMap = new HashMap<>();

    public ThinkSqlFilter<T> resultFilter(IThinkResultFilter filter){
        if (this.resultFilterList==null) {
            this.resultFilterList = new ArrayList<>();
        }
        this.resultFilterList.add(filter);
        return this;
    }

    public TEnableRequired getEnableRequired() {
        return enableRequired;
    }

    public List<IThinkResultFilter> getResultFilterList() {
        return resultFilterList;
    }

    public List<ThinkFilterBean> getBeans() {
        return this.beans;
    }

    private ThinkSqlFilter(Class<T> tClass) {
        this.tClass = tClass;
    }

    public static <T extends _Entity> ThinkSqlFilter<T> build(Class<T> tClass){
        return new ThinkSqlFilter<>(tClass);
    }
    public static <T extends _Entity> ThinkSqlFilter<T> build(Class<T> tClass, int limit){
        ThinkSqlFilter<T> sqlFilter =  new ThinkSqlFilter<>(tClass);
        sqlFilter.limit = limit;
        return sqlFilter;
    }

    public boolean isStrictFastMatch() {
        return strictFastMatch;
    }

    public void setStrictFastMatch(boolean strictFastMatch) {
        this.strictFastMatch = strictFastMatch;
    }

    public static <T extends _Entity> ThinkSqlFilter<T> parseFromJSON(String filterJson , Class<T> tClass){
        JSONObject jsonObject = null;
        try{
            jsonObject = JSONObject.parseObject(filterJson);
        }catch (Exception e){
            throw new IllegalArgumentException("Filter格式错误，无法解析成正确的JSON");
        }
        ThinkSqlFilter filter = new ThinkSqlFilter(tClass);
        if(jsonObject.containsKey("filterSplitYear")){
            filter.filterSplitYear = jsonObject.getInteger("filterSplitYear");
        }
        if (jsonObject.containsKey("limit")) {
            filter.limit = jsonObject.getInteger("limit");
        }


        /**
         * 如果 filter
         */
        if(jsonObject.containsKey("strictFastMatch")){
            boolean strict = jsonObject.getBoolean("strictFastMatch");
            filter.strictFastMatch = strict;
        }
        /**
         * 针对enable 参数的 独立 参数 处理
         */
        if(jsonObject.containsKey("enableRequired")){
            try {
                String enableRequired = jsonObject.getString("enableRequired");
                filter.enableRequired = TEnableRequired.valueOf(enableRequired);
            }catch (Exception e){}
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
            if(log.isTraceEnabled()) {
                log.trace("filterBody:{}",filterMap);
            }
            if(filterMap!=null){
                for(String k : filterMap.keySet()){
                    String realKey =  null;
                    if(k.contains("##")){
                        realKey = k.split("##")[1];
                    }else{
                        realKey = k;
                    }
                    if (filterChecker.checkKey(realKey,tClass)) {
                        ThinkFilterBean bean = ThinkFilterBean.parseFromJSON(realKey,filterMap.getJSONObject(k)) ;
                        if(bean !=null){
                            filter.beans.add(bean);
                        }
                    }else{
                        if (log.isDebugEnabled()) {
                            log.debug("{} 为非法字段，结构中并不包含此字段，所以thinkSqlFilter将屏蔽该关键字.",realKey);
                        }
                    }

                }
            }
        }
        return filter;
    }

    public ThinkSqlFilter<T> enableRequired(TEnableRequired required){
        if (this.getKeyCondition("enable")!=null) {
            log.warn("已经再ThinkSQLFilter中指定了enable的需求属性，该操作可能引起不必要的误解,但您的调用仍然被接受");
        }
        if(this.enableRequired!=null){
            log.warn("已经指定了EnableRequired为{}，由于本次操作被替换为：{}" ,this.enableRequired,required );
        }

        this.enableRequired = required;
        return this;
    }

    public ThinkSqlFilter<T> idIs( Serializable v){
        this.eq("id",v);
        return this;
    }

    public ThinkSqlFilter<T> eq(String k ,Serializable v){
        this._append(k, ThinkFilterOp.EQ,v);
        return this;
    }

    public ThinkSqlFilter<T> notEq(String k ,Serializable v){
        this._append(k,ThinkFilterOp.NOT_EQ,v);
        return this;
    }
    public ThinkSqlFilter<T> eqKey(String k ,String k2){
        this._append(k,ThinkFilterOp.EQ_KEY,k2);
        return this;
    }
    public ThinkSqlFilter<T> notEqKey(String k ,String k2){
        this._append(k,ThinkFilterOp.NOT_EQ_KEY,k2);
        return this;
    }

    public ThinkSqlFilter<T> lessThan(String k ,Serializable v){
        this._append(k,ThinkFilterOp.LE,v);
        return this;
    }

    public ThinkSqlFilter<T> lessThanKey(String k ,String k2){
        this._append(k,ThinkFilterOp.LE_KEY,k2);
        return this;
    }

    public ThinkSqlFilter <T> lessThanAndEq(String k ,Serializable v){
        this._append(k,ThinkFilterOp.LEE,v);
        return this;
    }

    public ThinkSqlFilter <T> lessThanAndEqKey(String k ,String k2){
        this._append(k,ThinkFilterOp.LEE_KEY,k2);
        return this;
    }

    public ThinkSqlFilter <T> largeThan(String k ,Serializable v){
        this._append(k,ThinkFilterOp.LG,v);
        return this;
    }


    public ThinkSqlFilter <T> largeThanKey(String k ,Serializable k2){
        this._append(k,ThinkFilterOp.LG_KEY,k2);
        return this;
    }

    public ThinkSqlFilter <T> largeThanAndEq(String k ,Serializable v){
        this._append(k,ThinkFilterOp.LGE,v);
        return this;
    }

    public ThinkSqlFilter <T> largeThanAndEqKey(String k ,Serializable k2){
        this._append(k,ThinkFilterOp.LGE_KEY,k2);
        return this;
    }

    public ThinkSqlFilter<T> in(String k , Serializable... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            this._append(k,ThinkFilterOp.IN,v);
        }
        return this;
    }


    public ThinkSqlFilter<T> notIn(String k , Serializable... v){
        if(v==null || v.length == 1){
            return notEq(k,v[0]);
        }else{
            this._append(k,ThinkFilterOp.NOT_IN,v);
        }
        return this;
    }

    public ThinkSqlFilter<T> or(String k ,Serializable... v){
        if(v==null || v.length<2){
            if(log.isDebugEnabled()){
                log.debug("or 语法必须包含至少2个或2个以上的参数值");
            }
            return this;
        }
        this._append(k,ThinkFilterOp.OR,v);
        return this;
    }


    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2){
        if(!this.keyOrMap.isEmpty() ){
            throw new ThinkRuntimeException("ThinkSqlFilter 中只允许调用一次keyOr方法");
        }
        keyOrMap.put(k1,v1);
        keyOrMap.put(k2,v2);
        return this;
    }

    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2 or k3 =v3) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2,String k3, Serializable v3){
       this.keyOr(k1,v1,k2,v2);
       this.keyOrMap.put(k3,v3);
       return this;
     }

    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2 or k3 =v3 or k4 =v4) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2,String k3, Serializable v3,String k4, Serializable v4){
        this.keyOr(k1,v1,k2,v2,k3,v3);
        keyOrMap.put(k4,v4);
        return this;
    }

    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2 or k3 =v3 or k4 =v4 or k5 = v5 ) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2,String k3, Serializable v3,String k4, Serializable v4,String k5, Serializable v5){
        this.keyOr(k1,v1,k2,v2,k3,v3,k4,v4);
        keyOrMap.put(k5,v5);
        return this;
    }

    @Deprecated
    public ThinkSqlFilter<T> isNull(String k){
        this._append(k,ThinkFilterOp.IS_NULL,new Serializable[]{});
        return this;
    }

    @Deprecated
    public ThinkSqlFilter<T> isNotNull(String k){
        this._append(k,ThinkFilterOp.IS_NOT_NULL,new Serializable[]{});
        return this;
    }



    public ThinkSqlFilter<T> betweenAnd(String k ,Serializable v1 ,Serializable v2){
        if(v1!=null && v2 != null) {
            this._append(k, ThinkFilterOp.BETWEEN_AND, v1,v2);
        }else {
            if (log.isDebugEnabled()) {
                log.debug("BETWEEN AND 参数均不能为null");
            }
        }
        return this;
    }


    public ThinkSqlFilter<T> like(String k,String likeStr){
        this._append(k,ThinkFilterOp.LIKE,likeStr);
        return this;
    }

    private ThinkSqlFilter<T> _append(String key,ThinkFilterOp op, Serializable... values){
        if(values == null){
            if(log.isDebugEnabled()){
                log.debug("THINK SQL FILTER 除了 NULL ，IS_NOT_NULL 外 ，参数均不能为NULL");
            }
            return this;
        }
        if(check(key)){
            ThinkFilterBean filterBean = ThinkFilterBean.build(key).initOp(op,values);
            this.beans.add(filterBean);
        }else{
            if(log.isDebugEnabled()){
                log.debug("key:{} 不存在于对象{}，被忽略 " ,key,tClass.getName() );
            }
        }
        return this;
    }

    /**
     * 是否存在 key的筛选
     * @param key
     * @return
     */
    public boolean existsKeyCondition(String key){
        for(ThinkFilterBean bean : beans){
            if(bean.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }


    /**
     * @param key
     * @param op
     * @return
     */
    public boolean checkKeyCondition(String key ,ThinkFilterOp op){
        for(ThinkFilterBean bean : beans){
            if(bean.getKey().equals(key)){
                return  op == bean.getOp();
            }
        }
        return false;
    }


    public ThinkFilterBean getKeyCondition(String key){
        for(ThinkFilterBean bean : beans){
            if(bean.getKey().equals(key)){
                return bean;
            }
        }
        return null;
    }


    public ThinkSqlFilter removeKeyConditions(String key){
//        Iterator<ThinkFilterBean> iterator = beans.iterator();
//        if(iterator!=null){
//            List<ThinkFilterBean> removeBeans = new ArrayList<>();
//            while (iterator.hasNext()) {
//                ThinkFilterBean next = iterator.next();
//                if (next.getKey().equals(key)) {
//                    removeBeans.add(next);
//                }
//            }
//            for(ThinkFilterBean remove : removeBeans){
//                beans.remove(remove);
//            }
//        }
        if(beans.size() > 0){
            beans.removeIf(bean -> bean.getKey().equals(key));
        }
        return this;
    }



    public ThinkSqlFilter groupBy(String... keys){
        this.groupByKeys= keys;
        return this;
    }

    public ThinkSqlFilter sortDesc(String sortKey){
        if(check(sortKey)){
            this.sortKey = sortKey;
            this.desc =true;
        }
        return this;
    }

    public ThinkSqlFilter sortAsc(String sortKey){
        if(check(sortKey)){
            this.sortKey = sortKey;
            this.desc = false;
        }
        return this;
    }

    public ThinkSqlFilter updateLimit(int limit){
        this.limit = limit;
        return this;
    }



    /**
     * 检查 是否 是合法字段
     * @param key
     * @return
     */
    private boolean check(String key){
        if(filterChecker ==null) {
            Field field = ClassUtil.getField(tClass, key);
            if (field == null) {
                return false;
            }
            if (field.getAnnotation(ThinkIgnore.class) != null) {
                return false;
            }
            return true;
        }else{
            return filterChecker.checkKey(key,tClass);
        }
    }





    public Class<T> gettClass() {
        return tClass;
    }

    public int getLimit() {
        return limit;
    }

    public int getFilterSplitYear() {
        return filterSplitYear;
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

    public static final void bindChecker(IFilterChecker checker){
        filterChecker = checker;
    }




    /**
     * 2020 -11 -17 新增 基础 数据类型新增
     *
     */

    public ThinkSqlFilter<T> in(String k , long... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Long[] vArray = new Long[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]=  Long.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.IN,vArray);
        }
        return this;
    }

    public ThinkSqlFilter<T> in(String k , int... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Integer[] vArray = new Integer[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Integer.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.IN,vArray);
        }
        return this;
    }
    public ThinkSqlFilter<T> in(String k , float... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Float[] vArray = new Float[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Float(v[i]);
            }
            this._append(k,ThinkFilterOp.IN,vArray);
        }
        return this;
    }
    public ThinkSqlFilter<T> in(String k , double... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Double[] vArray = new Double[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Double(v[i]);
            }
            this._append(k,ThinkFilterOp.IN,vArray);
        }
        return this;
    }

    public ThinkSqlFilter<T> in(String k , short... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Short[] vArray = new Short[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]=   Short.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.IN,vArray);
        }
        return this;
    }
    public ThinkSqlFilter<T> in(String k , boolean... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Boolean[] vArray = new Boolean[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]=Boolean.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.IN,vArray);
        }
        return this;
    }

    /** --------------------------OR --------------------------------------*/

    public ThinkSqlFilter<T> or(String k , long... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Long[] vArray = new Long[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]=   Long.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.OR,vArray);
        }
        return this;
    }

    public ThinkSqlFilter<T> or(String k , int... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Integer[] vArray = new Integer[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Integer.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.OR,vArray);
        }
        return this;
    }
    public ThinkSqlFilter<T> or(String k , float... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Float[] vArray = new Float[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Float(v[i]);
            }
            this._append(k,ThinkFilterOp.OR,vArray);
        }
        return this;
    }
    public ThinkSqlFilter<T> or(String k , double... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Double[] vArray = new Double[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= new Double(v[i]);
            }
            this._append(k,ThinkFilterOp.OR,vArray);
        }
        return this;
    }

    public ThinkSqlFilter<T> or(String k , short... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Short[] vArray = new Short[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Short.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.OR,vArray);
        }
        return this;
    }
    public ThinkSqlFilter<T> or(String k , boolean... v){
        if(v==null || v.length == 1){
            return eq(k,v[0]);
        }else{
            Boolean[] vArray = new Boolean[v.length];
            for (int i = 0; i < v.length; i++) {
                vArray[i]= Boolean.valueOf(v[i]);
            }
            this._append(k,ThinkFilterOp.OR,vArray);
        }
        return this;
    }


    public Map<String, Serializable> getKeyOrMap() {
        if(this.keyOrMap.isEmpty()){
            return new HashMap<>();
        }
        Map<String, Serializable> returnMap = new HashMap<>();
        for (Map.Entry<String, Serializable> entry : returnMap.entrySet()) {
            returnMap.put(entry.getKey(),entry.getValue());
        }
        return returnMap;
    }
}
