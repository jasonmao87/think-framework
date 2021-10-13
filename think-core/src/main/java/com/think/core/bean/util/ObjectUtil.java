package com.think.core.bean.util;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.core.annotations.bean.ThinkStateColumn;
import com.think.core.bean.BaseVo;
import com.think.core.bean.TFlowBuilder;
import com.think.core.bean.TFlowState;
import com.think.core.bean._Entity;
import com.think.core.enums.TEnum;
import com.think.structure.ThinkExplainList;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class ObjectUtil  {

    static {

    }

    public static final Field[] fields(Class beanClass){
        List<Field> list = ClassUtil.getFieldList(beanClass) ;
        return list.toArray( new Field[list.size()]);
    }


    /**
     * 对象转MAP
     * @param o
     * @return
     */
    public static final Map<String,Object> beanToMap(Object o){
        List<Field> list = ClassUtil.getFieldList(o.getClass());
        int initialCapacity =  list.size() *4 /3 + 1  ;
        Map<String,Object> map = new HashMap(initialCapacity);
        for(Field field : list){
            map.put(field.getName(),ClassUtil.getProperty(o,field.getName()));
        }
        return map ;
    }


    @Deprecated
    public static final void setFieldValue(Object o , String k ,Object v){
        try{
            Field f = ClassUtil.getField(o.getClass(),k);
            ClassUtil.setValue(f,o,v);
        }catch (Exception e){}
    }




    public static final <T> void mapSetValueToBean(Map<String, Object> map,T t ,Field field,String key){
//        Field field = ClassUtil.getField(t.getClass(),key);
        if( (t instanceof _Entity || t instanceof BaseVo ) && field.getType() == TFlowState.class){
            log.info("状态类的逻辑  TFlowState 。。。");
            //处理 状态类的逻辑 。。。。
            String stateKeyName  =null;
            String comment = "";
            ThinkStateColumn stateColumn =field.getAnnotation(ThinkStateColumn.class);
            if (stateColumn !=null) {
                comment = stateColumn.comment();
            }
            stateKeyName = field.getName();
            Integer value = (Integer) map.getOrDefault(stateKeyName +ThinkStateColumn.flowStateSuffix_StateValue,0);
            Date startTime = (Date) map.getOrDefault(stateKeyName+ThinkStateColumn.flowStateSuffix_StartTime,DateUtil.zeroDate());
            Date cancelTime  = (Date) map.getOrDefault(stateKeyName+ThinkStateColumn.flowStateSuffix_CancelTime,DateUtil.zeroDate());
            Date completeTime = (Date) map.getOrDefault(stateKeyName+ThinkStateColumn.flowStateSuffix_CompleteTime,DateUtil.zeroDate());
            String resultMessage = (String) map.getOrDefault(stateKeyName +ThinkStateColumn.flowStateSuffix_ResultMessage,"");
            Integer tryCount = (Integer) map.getOrDefault(stateKeyName + ThinkStateColumn.flowStateSuffix_TryCount,"0");
            TFlowState state = TFlowBuilder.build(stateKeyName,comment,value,startTime,completeTime,cancelTime,tryCount,resultMessage);
            field.setAccessible(true);
            ClassUtil.setValue(field,t,state);
            log.info("设置值 {}" ,state);
        }else{
//            Field field = ClassUtil.getField(t.getClass(), key);
            if(field.getType().getSuperclass()!=null && field.getType().getSuperclass().equals(Enum.class)){
                Object x = enumValue(field.getType(), (String) map.get(key));
                field.setAccessible(true);
                ClassUtil.setValue(field, t, x);
            }else{
                field.setAccessible(true);
                ClassUtil.setValue(field,t,map.get(key));
            }


        }




    }

    public static final <T> T mapToBean( Map<String, Object> map,Class<T> targetClass ,String...  ignoreKeys  ){
        T t = null;

//        Map<String, TEnum> cacheTEnum = null;
        try {
            t = (T) Class.forName(targetClass.getCanonicalName()).newInstance();
//            Set<String> stateDoneSet = new HashSet<>();
            Set<String> ignores = new HashSet<>();
            if(ignoreKeys!=null && ignoreKeys.length>0){
                for(String ig : ignoreKeys){
                    ignores.add(ig);
                }
            }

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if(entry.getKey().contains(ThinkStateColumn.splitFlag)){
                    key = key.split(ThinkStateColumn.splitFlag)[0];
                }
                if(ignores.contains(key) || StringUtil.isEmpty(key)  ){
                    continue;
                }else{
                    Field field = ClassUtil.getField(t.getClass(), key);
                    try{
                        mapSetValueToBean(map,t,field,key);
                    }catch (Exception e){
                        log.error("赋值未成功，相关KEY = {}" ,key);
                    }
                    ignores.add(key);
                }
            }

        } catch (Exception e) {
            if(log.isErrorEnabled()){
                log.error("Exception @ObjectUtil::mapToBean -> {}",e);
            }
        }

//        try {
//            if (t instanceof _Entity && cacheTEnum != null) {
//                for (Map.Entry<String, TEnum> entry : cacheTEnum.entrySet()) {
//                    ((_Entity) t).getEnumsValueExplain().add(entry.getValue().toRemark());
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        return t;
    }


    public static final<T extends _Entity>  void doThinkEntityTEnumExplain(T t ){
        ThinkExplainList thinkExplainList = t.getThinkTEnumsValueExplain();
        if (!thinkExplainList.isInit()) {
            List<Field> fieldList = ClassUtil.getFieldList(t.getClass());
            for (Field field : fieldList) {
                if (isTEnum(field)) {
//                field.getType()
                    TEnum tEnum = (TEnum) ClassUtil.getProperty(t,field.getName());
//                    tEnum.explain(field.getName());
                    thinkExplainList.add(tEnum.explain(field.getName()));
                }
            }
        }

    }


    public static  <T extends Enum> T enumValue(Class type , String value){
        return (T) Enum.valueOf(type,value);
    }

    public static final boolean isTEnum(Field field){
        Class<?>[] interfaces = field.getType().getInterfaces();
        if(interfaces.length >0){
            for (Class<?> i : interfaces) {
                if (i == TEnum.class) {
                    return true;
                }
            }
        }
        return false;
    }

    public static final  <T extends _Entity> void setDbPersistent(T t) {
        try{
            Field field = ClassUtil.getField(t.getClass(), "dbPersistent");
            field.setAccessible(true);
            ClassUtil.setValue(field ,t,true);
        }catch (Exception e){}
    }


    /**
     * copy对象的值到另一个对象！
     * @param source
     * @param targetClass
     * @return
     */
    public static Object copyObject(Object source  ,Class targetClass){
        Map sourceMap = beanToMap(source);
        return mapToBean(sourceMap,targetClass);
    }


    public static byte[] serializeObject(Object t){
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }
    /**
     *  反序列化
     * @param bytes
     * @return
     */
    public static <T> T deserialization(byte[] bytes,Class<T>  tClass) {


        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return (T)obj;
    }




    public static byte[] protostufSerializeObject(Object t) throws Exception{
        return ProtostuffUtil.serializer(t);
    }
    public static  <T> T protostufDeserialization(byte[] bytes,Class<T>  tClass) throws Exception{
        return ProtostuffUtil.deserializer(bytes,tClass);
    }


    /**
     * 我i发通过 protostuf 序列化反序列化的class 类型
     */
    public static final HashSet<Class> protostufErrClassSet= new HashSet<>();




}
