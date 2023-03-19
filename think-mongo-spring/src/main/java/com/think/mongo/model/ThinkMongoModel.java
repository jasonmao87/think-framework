package com.think.mongo.model;

import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkMongoIndex;
import com.think.core.annotations.bean.ThinkMongoIndexes;
import com.think.core.bean.SimpleMongoEntity;
import com.think.core.bean.util.ClassUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThinkMongoModel implements Serializable {
    private static final long serialVersionUID = 8594180087630745151L;

    private Class modalClass ;

    private final mongoColumn[] keys ;

    private final String[] useIndexKeys;

    public ThinkMongoModel(Class modalClass, mongoColumn[] keys, String[] useIndexKeys) {
        this.modalClass = modalClass;
        this.keys = keys;
        this.useIndexKeys = useIndexKeys;
    }

    public Class getModalClass() {
        return modalClass;
    }

    public mongoColumn[] getKeys() {
        return keys;
    }

    public String[] getUseIndexKeys() {
        return useIndexKeys;
    }

    public boolean containsKey(String key){
        for(mongoColumn c : keys){
            if(c.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }


    public boolean useIndex(String key){
        for(String k :useIndexKeys){
            if(k.equals(key)){
                return true;
            }
        }
        return false;
    }

    public static <T extends SimpleMongoEntity> ThinkMongoModel build(Class<T> targetClass){
        List<Field> fields  = ClassUtil.getFieldList(targetClass);
        ThinkMongoIndexes indexes = targetClass.getAnnotation(ThinkMongoIndexes.class);
        Set<String> indexSet = new HashSet<>();
        indexSet.add("id");
        if(indexes!=null){
            for(ThinkMongoIndex index : indexes.indexes()){
                String k = index.keys()[0] ;
                if(indexSet.contains(k) == false) {
                    indexSet.add(k);
                }
            }
        }
        String[] useIndexKeys = new String[indexSet.size()] ;
        indexSet.toArray(useIndexKeys);
        List<mongoColumn> clms = new ArrayList<>();
        for(Field f : fields){
            mongoColumn c = new mongoColumn();
            if(f.getName().equals("serialVersionUID")){
                continue;
            }
            c.setType(f.getType());
            c.setKey(f.getName());
            clms.add(c);
        }
        mongoColumn[] keys = new mongoColumn[clms.size()];
        clms.toArray(keys);
        ThinkMongoModel modal = new ThinkMongoModel(targetClass,keys,useIndexKeys) ;
        return modal;
    }
}

class mongoColumn implements Serializable{
    private static final long serialVersionUID = 1299272985810179921L;
    @Remark("类型")
    private String key ;
    @Remark("类型 ")
    private Type type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

