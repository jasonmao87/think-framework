package com.think.common.data.mysql;

import com.alibaba.fastjson.JSONObject;
import com.think.common.data.IFilterChecker;
import com.think.common.data.ThinkFilterOp;
import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkIgnore;
import com.think.core.annotations.bean.ThinkTable;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.core.enums.DbType;
import com.think.core.enums.TEnableRequired;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class ThinkSqlFilter<T extends _Entity> implements Serializable {

    private static final long serialVersionUID = 3347480035180588760L;

    private final DbType dbType;

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

//    @Remark("限制查询年份，仅对按年切分表有效")
//    private int filterSplitYear ;
//

    @Remark("限制查询开始年份，大于0有效")
    private int filterSplitYearFrom ;

    @Remark("限制查询结束年份，大于0有效")
    private int filterSplitYearEnd ;

    @Remark("可能为空结果")
    private boolean mayBeEmptyResult = false;


    @Remark(value = "支持快速匹配的严格模式",description = "true 时候为严格匹配，如果 false，不知道 可以 匹配到 不指导")
    private boolean strictFastMatch = false;


    @Remark("Group key")
    private String[] groupByKeys ;

    private List<ThinkFilterBean> beans = new ArrayList<>();


    private List<ThinkFilterBean> keyOrBeans = new ArrayList<>();


    @Remark("key or 使用 LIKE 模式，默认未 EQ")
    private boolean keyOrTypeUsingLike  = false;



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
        ThinkTable thinkTable = tClass.getAnnotation(ThinkTable.class);
        if(thinkTable == null) {
            final DbType type = thinkTable.dbType().realType();
            if (type == DbType.DEFAULT) {
                dbType = DbType.defaultDbTypeValue();
            }else{
                dbType = type;
            }
        }else{
            log.warn("未确定到ThinkFilter匹配的数据库模型对象。使用默认数据库类型{}" ,DbType.defaultDbTypeValue());
            dbType = DbType.defaultDbTypeValue();
//            throw new IllegalArgumentException("ThinkFilter目标筛选对象非数据库映射对象");
        }
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
            int filterSplitYear = jsonObject.getInteger("filterSplitYear");
            filter.filterSplitYearFrom = filterSplitYear;
            filter.filterSplitYearEnd = filterSplitYear;
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
                    if (filter.check(realKey)) {
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
        if(jsonObject.containsKey("keyOrBody")) {
            JSONObject keyOrBody = jsonObject.getJSONObject("keyOrBody");
            String[] keys = keyOrBody.keySet().toArray(new String[keyOrBody.size()]);
            if(keys.length ==2 ){
                filter.keyOr( keys[0],(Serializable) keyOrBody.get(keys[0]) ,keys[1],(Serializable)keyOrBody.get(keys[1]));
            }else if(keys.length ==3){
                filter.keyOr( keys[0],(Serializable) keyOrBody.get(keys[0]) ,keys[1],(Serializable)keyOrBody.get(keys[1]) ,  keys[2],(Serializable) keyOrBody.get(keys[2]) );
            }else if(keys.length ==4 ){
                filter.keyOr( keys[0],(Serializable) keyOrBody.get(keys[0]) ,keys[1],(Serializable)keyOrBody.get(keys[1]) ,  keys[2],(Serializable) keyOrBody.get(keys[2]) ,  keys[3],(Serializable) keyOrBody.get(keys[3]) );
            }
            // 检查 使用key or 的方式 ！
            String keyOrType = (String) jsonObject.getOrDefault("keyOrType", "EQ");
            if(keyOrType.equalsIgnoreCase("like")){
                filter.keyOrUsingLike();
            }
        }

        return filter;
    }


    public ThinkSqlFilter<T> keyOrUsingLike(){
        this.keyOrTypeUsingLike = true;
        List<ThinkFilterBean> newArrayList = new ArrayList<>();
        this.keyOrBeans.forEach(t->{
            ThinkFilterBean bean = ThinkFilterBean.LIKE(t.getKey(), (String) t.getValues()[0]);
            newArrayList.add(bean);
        });
        this.keyOrBeans  = newArrayList;
        return this;
    }

    public boolean isKeyOrTypeUsingLike() {
        return keyOrTypeUsingLike;
    }

    public ThinkSqlFilter<T> enableRequired(TEnableRequired required){
        if(required == null){
            required = TEnableRequired.MATCH_ALL;
        }

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


    public ThinkSqlFilter<T> eqIfNotNull(String k ,Serializable v){
        if(v!=null){
            return this.eq(k,v);
        }
        return this;
    }

    public ThinkSqlFilter<T> eqIfNotEmpty(String k ,String v){
        if(StringUtil.isNotEmpty(v)){
            return this.eq(k,v);
        }
        return this;
    }

    public ThinkSqlFilter<T> eqIfNumberGreaterThanZero(String k ,Number v){
        if(v !=null && v.intValue()>0){
            return this.eq(k,v);
        }
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

    /**
     * 在 指定的 这一天里面
     * @param k
     * @param d
     * @return
     */
    public ThinkSqlFilter<T> inThatDay(String k, Date d){
        this.betweenAnd(k, DateUtil.beginOfDate(d),DateUtil.endOfDate(d));
        return this;
    }

    public ThinkSqlFilter<T> in(String k , Serializable... v){
        if(v == null || v.length == 0){
            if (log.isWarnEnabled()) {
                log.warn(" {} in 语法未包含任何 值，这样查询结果必然无法匹配到任何数据 ！" ,k);
            }
            this.mayBeEmptyResult = true;
        }else if( v.length == 1){
            return eq(k,v[0]);
        }else{
            this._append(k,ThinkFilterOp.IN,v);
        }
        return this;
    }


    public ThinkSqlFilter<T> notIn(String k , Serializable... v){
        if(v == null){
            return this;
        }else if(v.length == 0){
            return this;
        }else if( v.length == 1){
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


    private void keyOrAppend(String k ,Serializable v){
        if (this.check(k)) {
            if(this.keyOrTypeUsingLike) {
                this.keyOrBeans.add(ThinkFilterBean.LIKE(k, (String) v));
            }else{
                this.keyOrBeans.add(ThinkFilterBean.EQ(k,v));
            }
        }else{
            log.warn("key " +k +" 不包含再模型对象中，将被忽略OR查询" );
        }



    }


    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2){
        this.keyOrAppend(k1,v1);
        this.keyOrAppend(k2,v2);
        return this;
    }

    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2 or k3 =v3) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2,String k3, Serializable v3){
        if (check(k3)) {
            this.keyOr(k1,v1,k2,v2);
            //this.keyOrMap.put(k3,v3);
            this.keyOrAppend(k3,v3);
        }

        return this;
     }

    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2 or k3 =v3 or k4 =v4) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2,String k3, Serializable v3,String k4, Serializable v4){
        if(check(k4)){
            this.keyOr(k1,v1,k2,v2,k3,v3);
//            this.keyOrMap.put(k4,v4);
            this.keyOrAppend(k4,v4);
        }
        return this;
    }

    @Remark("转换成SQL >> ... where ... and  ( k1 =v1 or k2 =v2 or k3 =v3 or k4 =v4 or k5 = v5 ) ")
    public ThinkSqlFilter<T> keyOr(String k1, Serializable v1,String k2, Serializable v2,String k3, Serializable v3,String k4, Serializable v4,String k5, Serializable v5){
        this.keyOr(k1,v1,k2,v2,k3,v3,k4,v4);
//        keyOrMap.put(k5,v5);
        this.keyOrAppend(k5,v5);
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
     * 检查是否已经包含key
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


    /**
     * 从 filter中获取 keyCondition
     * @param key
     * @return
     */
    public ThinkFilterBean getKeyCondition(String key){
        for(ThinkFilterBean bean : beans){
            if(bean.getKey().equals(key)){
                return bean;
            }
        }
        return null;
    }


    public ThinkSqlFilter removeKeyConditions(String key){
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



    public ThinkSqlFilter<T> setFilterSelectYearLimit(@Remark("filterSplitYearFrom 查询限制开始年份") int filterSplitYearFrom,@Remark("filterSplitYearEnd 查询限制结束年份")int filterSplitYearEnd){
        this.filterSplitYearFrom =  filterSplitYearFrom;
        this.filterSplitYearEnd = filterSplitYearEnd ;
        return this;
    }


    public Class<T> gettClass() {
        return tClass;
    }

    public int getLimit() {
        return limit;
    }

//    public int getFilterSplitYear() {
//        return filterSplitYear;
//    }

    public int getFilterSplitYearEnd() {
        return filterSplitYearEnd;
    }

    public int getFilterSplitYearFrom() {
        return filterSplitYearFrom;
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

    public ThinkSqlFilter<T> in(String k , long[] v){
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

    public ThinkSqlFilter<T> in(String k , int[] v){
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
    public ThinkSqlFilter<T> in(String k , float[] v){
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
    public ThinkSqlFilter<T> in(String k , double[] v){
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

    public ThinkSqlFilter<T> in(String k , short[] v){
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
    public ThinkSqlFilter<T> in(String k , boolean[] v){
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

    public ThinkSqlFilter<T> or(String k , long[] v){
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

    public ThinkSqlFilter<T> or(String k , int[] v){
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
    public ThinkSqlFilter<T> or(String k , float[] v){
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
    public ThinkSqlFilter<T> or(String k , double[] v){
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

    public ThinkSqlFilter<T> or(String k , short[] v){
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
    public ThinkSqlFilter<T> or(String k , boolean[] v){
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


    /**
     * 该方法过于 简单，即将废弃
     * @return
     */
    @Remark("该方法过于简单，即将废弃")
    @Deprecated
    public Map<String, Serializable> getKeyOrMap() {
        if(this.keyOrBeans.isEmpty()){
            return new HashMap<>();
        }
        Map<String, Serializable> returnMap = new HashMap<>();
        keyOrBeans.forEach(t->{
            returnMap.put( t.getKey(),t.getValues()[0]);
        });

//        for (Map.Entry<String, Serializable> entry : keyOrMap.entrySet()) {
//            returnMap.put(entry.getKey(),entry.getValue());
//        }
        return returnMap;
    }


    public List<ThinkFilterBean> getKeyOrBeans() {
        return keyOrBeans;
    }

    public boolean mayBeEmptyResult() {
        return mayBeEmptyResult;
    }


//    public static void main(String[] args) {
//
//        doss(null);
//    }
//
//    public static final void doss(String... x){
//        System.out.println(x );
//    }


    public final ThinkSqlFilter<T> copyNew(){
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(gettClass(), getLimit());
        if(this.isDesc()){
            sqlFilter.sortDesc(this.getSortKey());
        }else{
            sqlFilter.sortAsc(this.getSortKey());
        }

        for (ThinkFilterBean bean : this.beans) {
            sqlFilter._append(bean.getKey(),bean.getOp(),bean.getValues());
        }

        sqlFilter.keyOrTypeUsingLike = this.keyOrTypeUsingLike;
        for (ThinkFilterBean keyOrBean : this.keyOrBeans) {
            sqlFilter.keyOrBeans.add(keyOrBean);
        }
        sqlFilter.mayBeEmptyResult = this.mayBeEmptyResult;

        return sqlFilter;
    }


    public Map<String,String> fastMatchMapInfo(){
        Map<String,String> map =new HashMap();
        getKeyOrBeans().forEach(filterBean->{
            if(filterBean.isFastMatchAble()){
                Object v = filterBean.getValues()[0];
                if (v instanceof String  && this.isKeyOrTypeUsingLike()) {
                    map.put(
                            filterBean.getKey(),
                            v.toString().replaceAll("%","")
                    );
                }
                //fastMatchMap.put(t.getKey(),t.getValues()[0].toString());
            }
        });


        return map;
    }

    public DbType getDbType() {
        return dbType;
    }
}
