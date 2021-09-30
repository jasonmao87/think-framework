package com.think.common.data.mysql;

import com.think.common.util.DateUtil;
import com.think.core.annotations.bean.ThinkIgnore;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class ThinkUpdateMapper<T extends _Entity> {

    private Class<T> targetClass;
    private ThinkSqlFilter<T> filter = null ;
    private Map<String,Object> setMapper ;
    private Map<String,Object> incMapper ;
    private Map<String,String> setKeyMapper ;

    private  ThinkUpdateMapper(Class<T> targetClass) {
        this.targetClass = targetClass;
        //this.filter = ThinkSqlFilter.build(tClass);
        this.incMapper = new HashMap<>();
        this.setMapper = new HashMap<>();
        this.setKeyMapper = new HashMap<>();
    }

    public static <T extends _Entity> ThinkUpdateMapper<T> build(Class<T> tClass){

        List<String > emn =Collections.EMPTY_LIST;

        return (ThinkUpdateMapper<T>)new ThinkUpdateMapper<T>(tClass);
    }


    public ThinkSqlFilter<T> sqlFilter(){
        if(this.filter == null){
            filter = ThinkSqlFilter.build(targetClass,-1);
        }
        return this.filter;
    }

    /**
     * 将字段 k 的 值 设置为 sourceKey 一样的值
     * @param k
     * @param sourceKey
     * @return
     */
    public ThinkUpdateMapper<T> updateToKeyValue(String k , String sourceKey){
      if(this.checkKey(k,false) && this.checkKey(sourceKey,true)){
          this.setKeyMapper.put(k,sourceKey);
      }
      return this;
    }

    public ThinkUpdateMapper<T> updateValue(String k, Serializable v){
        if(this.checkKey(k,false)) {
            this.setMapper.put(k, v);
        }
        return this;
    }

    public ThinkUpdateMapper<T> updateInc(String k , int inc){
        if(this.checkKey(k,false)) {
            this.incMapper.put(k, inc);
        }
        return this;
    }

    public ThinkUpdateMapper<T> updateInc(String k,double inc){
        if(this.checkKey(k,false)) {
            this.incMapper.put(k, inc);
        }
        return this;
    }

    public ThinkUpdateMapper<T> updateDateAsNow(String key){
        if (this.checkKeyIsDateType(key) ) {
            this.updateValue(key, DateUtil.now());
        }
        return this;
    }

    /**
     * 设置需要被修改数据的Id
     * @param id
     * @return
     * @throws ThinkRuntimeException
     */
    public ThinkUpdateMapper<T> setTargetDataId(long id) throws ThinkRuntimeException {
        if(this.filter != null){
            throw new ThinkRuntimeException("已经为UpdateMapper设置了Filter，再设置Id值将会引起过滤筛选条件的混乱！");
        }
        ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass);
        sqlFilter.eq("id",id);
        return this.setFilter(sqlFilter);
    }


    public ThinkSqlFilter<T> getFilter() {
        if(filter == null){
            ThinkSqlFilter<T> sqlFilter = ThinkSqlFilter.build(targetClass);
            this.filter = sqlFilter;
        }
        return filter;
    }

    public ThinkUpdateMapper<T> setFilter(ThinkSqlFilter<T> sqlFilter) throws ThinkRuntimeException {
        if(this.filter != null){
            throw new ThinkRuntimeException("已经为UpdateMapper设置了Filter，请勿重复设置");
        }
        this.filter = sqlFilter;
        return this;


    }

//    /**
//     * 检查字段是否允许被设置
//     * @param keys
//     * @param allowNegative  允许 key前面出现负号
//     * @return
//     */
//    private boolean checkKey(String... keys ){
//        for(String k : keys){
//
//            Field field =ClassUtil.getField(targetClass,k);
//            if(field == null){
//                if(log.isDebugEnabled()) {
//                    log.debug("对象不存在对应的key；{}或{},被自动忽略", k);
//                }
//                return false;
//            }
//            if(field.getAnnotation(ThinkIgnore.class) !=null){
//                if(log.isDebugEnabled()) {
//                    log.debug("对象对应的key；{},未被映射到DB，被自动忽略", k);
//                }
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     *
     * @param k    key
     * @param allowNegative 允许key前面出现负号
     * @return
     */
    private boolean checkKey(String k,boolean allowNegative){
        if(k !=null && k.startsWith("-") && allowNegative){
            k = k.replaceFirst("-","");
        }

        Field field =ClassUtil.getField(targetClass,k);
        if(field == null){
            if(log.isDebugEnabled()) {
                log.debug("对象不存在对应的key；{} ,被自动忽略", k);
            }
            return false;
        }
        if(field.getAnnotation(ThinkIgnore.class) !=null){
            if(log.isDebugEnabled()) {
                log.debug("对象对应的key；{},未被映射到DB，被自动忽略", k);
            }
            return false;
        }
        return true;
    }

    private boolean checkKeyIsDateType(String key){
        if(this.checkKey(key,false)){
            Field field =ClassUtil.getField(targetClass,key);
            if (field.getType() == Date.class) {
                return true;
            }
        }

        return false;
    }


    public Map<String, Object> getIncMapper() {
        return incMapper;
    }


    public Map<String, Object> getSetMapper() {
        return setMapper;
    }

    public Map<String, String> getSetKeyMapper() {
        return setKeyMapper;
    }

    /**
     * 检查 UpdateMapper 是否 安全
     * @return
     */
    public boolean isSafe(){
        if(this.filter == null){
            return false;
        }
        if( (this.incMapper.size() == 0)
                && (this.setMapper.size() == 0)
                &&  (this.setKeyMapper.size() == 0)){
            return false;
        }
        return true;
    }

    public Class<T> getTargetClass() {
        return targetClass;
    }


    /**
     * 2021/1/28新增 --- 批量的通过map 设置 update 清单 ！
     * @param customUpdateMap
     * @return
     */
    public ThinkUpdateMapper<T> putUpdateMap(Map<String,Object> customUpdateMap){
        customUpdateMap.entrySet().forEach((entry)->{
            if(entry.getValue() instanceof Serializable) {
                this.updateValue(entry.getKey(), (Serializable) entry.getValue());
            }else{
                if (log.isDebugEnabled()) {

                    log.debug("由于 k :{}  的值不可序列化，在添加到修改参数清单过程中将被忽略" , entry.getKey());
                }
            }
        });
        return this;
    }





}
