package com.think.mongo.dao;

import com.mongodb.client.model.IndexOptions;
import com.think.core.annotations.bean.ThinkMongoIndex;
import com.think.core.annotations.bean.ThinkMongoIndexes;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Mongo 索引 HOLDER
 */
@Slf4j
public class ThinkMongoIndexBuilder {


    private static final Set<String> indexedHolder =new HashSet<>();

    protected static void checkAndInitIndex(Class targetClass ,String collectionName , MongoTemplate mongoTemplate){

        String k = targetClass.getName() + "::" + collectionName;
        if(indexedHolder.contains(k) == false){
            try{
                mongoTemplate.createCollection(collectionName);
            }catch (Exception e){}
            indexedHolder.add(k);
            ThinkMongoIndexes indexes= (ThinkMongoIndexes) targetClass.getAnnotation(ThinkMongoIndexes.class);
            if(indexes !=null) {
                //String collectionName = mongoTemplate.getCollectionName(targetClass);
                // 创建 过期缩影
                _expireIndex(collectionName,indexes,mongoTemplate);
                //创建 普通索引
                for(ThinkMongoIndex index : indexes.indexes()){
                    _commonIndex(collectionName,index,mongoTemplate);
                }
            }
        }

    }

    protected static final boolean checkIndexed(Class targetClass){
        return indexedHolder.contains(targetClass);
    }

    private static void _commonIndex(String collectionName , ThinkMongoIndex index , MongoTemplate mongoTemplate){
        if(index !=null){
            StringBuilder indexName = new StringBuilder("");
            Document document = new Document();
            for(String k : index.keys()){
                indexName.append(k).append("_");
                document.append(k,1);
            }
            IndexOptions indexOptions = new IndexOptions();
            indexOptions.background(true)
                    .unique(index.unique())
                    .name(indexName.toString());

            try{
                mongoTemplate.getCollection(collectionName).createIndex(document,indexOptions);
            } catch (Exception e){
                //e.printStackTrace();
                if(log.isErrorEnabled()){
                    log.error("如果是首次启动抛出，可以关注异常详情参考。如果是collection存在异常，忽略即可",e);
                }
            }

        }
    }


    private static void _expireIndex(String collectionName , ThinkMongoIndexes indexes, MongoTemplate mongoTemplate ){
        if(indexes.expireAble()) {
            long day = indexes.expireAtDays();
            IndexOptions indexOptions = new IndexOptions();
            indexOptions.background(true)
                    .expireAfter(day, TimeUnit.DAYS)
                    .name("expireAt");
            Map map = new HashMap();
            map.put("expireAt", day);
            try {
                mongoTemplate.getCollection(collectionName).createIndex(new Document(map), indexOptions);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }





}
