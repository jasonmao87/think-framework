package com.think.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.*;


public class FastJsonUtil {
    private static SerializeConfig mapping = new SerializeConfig();
    private static SimpleDateFormatSerializer formatSerializer = new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss");
    private static final SerializerFeature[] features;

    static {
        features = new SerializerFeature[]{
//                SerializerFeature.WriteMapNullValue,
//                SerializerFeature.WriteNullListAsEmpty,
//                SerializerFeature.WriteNullNumberAsZero,
//                SerializerFeature.WriteNullBooleanAsFalse,
//                SerializerFeature.WriteNullStringAsEmpty,
//                SerializerFeature.PrettyFormat,
                SerializerFeature.IgnoreErrorGetter,
                SerializerFeature.IgnoreNonFieldGetter,
                SerializerFeature.DisableCircularReferenceDetect
        };
    }

    private FastJsonUtil() {
    }

    public static <T> T parseToClass(String jsonStr, Class<?> clazz) {
        T javaObject = (T) JSON.toJavaObject(JSON.parseObject(jsonStr), clazz);
        return javaObject;
    }

    public static JSONObject parseToJson(String json){
        return JSONObject.parseObject(json);
    }

    public static String parseToJSON(Object object) {
        return JSON.toJSONString(object, configMapping(),features);
    }

    public static String parseUnicodeJSON(Object object) {
        return JSON.toJSONString(object, new SerializerFeature[]{SerializerFeature.BrowserCompatible});
    }

    public static String parseJSONString(Object object, SimplePropertyPreFilter filter) {
        return JSON.toJSONString(object, filter, features);
    }

    public static String parseJSONString(Object object, String formatDate) {
        return JSON.toJSONString(object, configMapping(formatDate), features);
    }

    public static List<?> getListJSONToJava(String jsonString, Class<?> pojoClass) {
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        List<Object> list = new ArrayList();

        for(int i = 0; i < jsonArray.size(); ++i) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Object pojoValue = JSON.toJavaObject(jsonObject, pojoClass);
            list.add(pojoValue);
        }

        return list;
    }

    public static Map<String, Object> getMapFromJSON(String jsonString) {
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Map<String, Object> parseJSONMap = new LinkedHashMap(jsonObject);
        return parseJSONMap;
    }

    private static SerializeConfig configMapping() {
        mapping.put(Date.class, formatSerializer);
        return mapping;
    }

    private static SerializeConfig configMapping(String format) {
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer(format));
        return mapping;
    }

    public static SimplePropertyPreFilter filterProperty(Class<?> className, String... param) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(className, param);
        return filter;
    }

    public static String toPrettyString(Object o){
        return JSON.toJSONString(o,true);
    }


    public static String toJSONString(JSONObject jsonObject){
        return JSON.toJSONStringWithDateFormat(jsonObject,"yyyy-MM-dd HH:mm:ss" ,
                SerializerFeature.WriteDateUseDateFormat,SerializerFeature.IgnoreErrorGetter,SerializerFeature.IgnoreNonFieldGetter);
    }

    public static void main(String[] args) {

        xada xada = new xada();
        System.out.println(parseToJSON(xada));
    }

}

class xada{
    private int da;
    private String name =null;

    public xada() {
    }

    public void setDa(int da) {
        this.da = da;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDa() {
        return da;
    }

    public String getName() {
        return name;
    }

    public Boolean isGood(){
        return null;
    }

    public String getS(){
        throw new RuntimeException("s");
    }

}
