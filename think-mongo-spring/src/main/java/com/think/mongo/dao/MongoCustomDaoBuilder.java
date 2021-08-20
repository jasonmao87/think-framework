package com.think.mongo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MongoCustomDaoBuilder {
    @Autowired
    MongoTemplate mongoTemplate;
    private static final Map<String,ThinkMongoCustomDao> holder= new HashMap();
    private static MongoCustomDaoBuilder instance  ;
    public MongoCustomDaoBuilder(){
        instance = this;
    }

    public static synchronized  ThinkMongoCustomDao buildCustomDao(String collectionName){
        if(holder.containsKey(collectionName)){
            return holder.get(collectionName);
        }else{
            ThinkMongoCustomDao customDao = new ThinkMongoCustomDao(instance.mongoTemplate,collectionName);
            holder.put(collectionName,customDao);
            return customDao;
        }

    }

}
