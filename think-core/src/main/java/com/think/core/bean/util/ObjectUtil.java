package com.think.core.bean.util;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.core.bean._Entity;
import lombok.Data;
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


    public static final <T> T mapToBean( Map<String, Object> map,Class<T> targetClass ,String...  ignoreKeys  ){
        T t = null;
        try {
            Set<String> ignores = new HashSet<>();
            if(ignoreKeys!=null && ignoreKeys.length>0){
                for(String ig : ignoreKeys){
                    ignores.add(ig);
                }
            }
            t = (T) Class.forName(targetClass.getCanonicalName()).newInstance();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if(ignores.contains(key) || StringUtil.isEmpty(key)){
                    continue;
                }
                Field field = ClassUtil.getField(t.getClass(), key);
                if(field!= null){
                    try{
                        field.setAccessible(true);
                        ClassUtil.setValue(field,t,map.get(key));
                    }catch (Exception e){
                    }
                }
            }

//            for (String key : map.keySet()) {
//                if(ignores.contains(key)){
//                    continue;
//                }
//                Field field = ClassUtil.getField(t.getClass(), key);
//                if(field!= null){
//                    try{
//                        field.setAccessible(true);
//                        ClassUtil.setValue(field,t,map.get(key));
//                    }catch (Exception e){
//                    }
//                }
//            }
        } catch (Exception e) {
            if(log.isErrorEnabled()){
                log.error("Exception @ObjectUtil::mapToBean -> {}",e);
            }
        }
        return t;
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

    public static void main(String[] args) throws Exception{
        List<TbX> list=new ArrayList<>();
        for(int i= 0 ; i < 100 ; i ++){
            list.add(new TbX().setDate(DateUtil.computeAddDays(DateUtil.now(),i)).setName("UI-你知道巨大大剂量的煎熬了多久了 的急啊离开军队垃圾的啦 大当家垃圾的老咔叽大剂量的急啊离开东京拉开大家立刻决定离开就大家来看建档立卡就对啦-" +i));
        }

        List<byte[]> listPro =new ArrayList<>();
        List<byte[]> listJdk = new ArrayList<>();
        long begin =  System.currentTimeMillis();

        for (TbX tbX : list) {
            byte[] x = protostufSerializeObject(tbX);
            listPro.add(x);

//            System.out.println(x.length + "完成");
        }

        long end = System.currentTimeMillis();
        System.out.println("protostuff 序列化完成 " + (end -begin));


        begin = System.currentTimeMillis();
        for (TbX tbX : list) {
//            System.out.println(x.length + "完成");
            byte[] x = serializeObject(tbX);
            listJdk.add(x);
        }
        end = System.currentTimeMillis();
        System.out.println("JDK 序列化完成 " + (end -begin));
        TbX xx =null;
        System.out.println("反序列化");
        begin = System.currentTimeMillis();
        for (byte[] bytes : listPro) {
            TbX x =  protostufDeserialization(bytes,TbX.class);
            xx = x ;
        }
        end = System.currentTimeMillis();
        System.out.println("protostuff 反序列化完成 " + (end -begin));

        System.out.println(xx );

        for (byte[] bytes : listJdk) {
            TbX x =  deserialization(bytes,TbX.class);
            xx =x ;
        }
        end = System.currentTimeMillis();
        System.out.println("jdk 反序列化完成 " + (end -begin));

        System.out.println(xx);






    }


}
