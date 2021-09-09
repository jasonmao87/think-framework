package com.think.mongo.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.WriteConcern;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import com.think.common.data.mongo.ThinkMongoQueryFilter;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.common.util.IdUtil;
import com.think.core.annotations.Remark;
import com.think.core.bean.SimpleMongoEntity;
import com.think.core.bean.util.ObjectUtil;
import com.think.exception.ThinkException;
import org.bson.BsonValue;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.util.*;

public class ThinkMongoCustomDao {

    private MongoTemplate mongoTemplate;
    /**
     * 自定义 collection name
     */
    private String customCollectionName;

    public ThinkMongoCustomDao(MongoTemplate mongoTemplate,String customCollectionName ){
        this.mongoTemplate = mongoTemplate;
        this.customCollectionName = customCollectionName;
    }


    private MongoTemplate getMongoTemplate() {
        mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoTemplate;
    }




    private void checkAndInitIndex(Class targetClass ){
        ThinkMongoIndexBuilder.checkAndInitIndex(targetClass,customCollectionName,mongoTemplate);
    }

    @Remark("只能处理匹配到的第一条记录")
    public <T extends SimpleMongoEntity> T findOneAndModify(ThinkMongoQueryFilter<T> filter){
        Query query = ThinkMongoQueryBuilder.build(filter,false);
        Update update = new Update();
        update.set("thinkUpdateKey",IdUtil.nextId());
        filter.getModifyUpdateMapper().forEach((k, v)->{
            update.set(k,v);
        });
        filter.getModifyIncMapper().forEach((k,v)->{
            update.inc(k,v);
        });
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(false);
        return getMongoTemplate().findAndModify(query,update,findAndModifyOptions,filter.gettClass(),customCollectionName);
    }

    @Remark("只能处理匹配到的第一条记录")
    public <T extends SimpleMongoEntity> T findOneAndModify(ThinkMongoQueryFilter<T> filter ,boolean returnNew){
        Query query = ThinkMongoQueryBuilder.build(filter,false);
        Update update = new Update();
        filter.getModifyUpdateMapper().forEach((k, v)->{
            update.set(k,v);
        });
        filter.getModifyIncMapper().forEach((k,v)->{
            update.inc(k,v);
        });
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(returnNew);
        return getMongoTemplate().findAndModify(query,update,findAndModifyOptions,filter.gettClass(),customCollectionName);
    }


    public <T extends SimpleMongoEntity> T insert(T t){
        t.setThinkUpdateKey(IdUtil.nextId());
        this.checkAndInitIndex(t.getClass());
        IdFixTool.dataInit(t);
        return getMongoTemplate().insert(t,customCollectionName);
    }

    public <T extends SimpleMongoEntity> ThinkResult<T> update(T t){
        this.checkAndInitIndex(t.getClass());
        IdFixTool.dataInit(t);
//        Update update = new Update();
        Map<String,Object> map = ObjectUtil.beanToMap(t);
        ThinkMongoQueryFilter<? extends SimpleMongoEntity> filter = ThinkMongoQueryFilter.build(t.getClass()).eq("id", t.getId()).eq("thinkUpdateKey",t.getThinkUpdateKey());
        map.keySet().forEach(k->{
            if(!k.equalsIgnoreCase("id") ){
                if(k.equalsIgnoreCase("thinkUpdateKey")){
                    filter.findAndModifyUpdate(k, IdUtil.nextId());
                }else{
                    filter.findAndModifyUpdate(k,map.get(k));
                }
            }
        });
        t = (T) findOneAndModify(filter,true );
        if(t ==null){
            return ThinkResult.error("修改失败，可能涉及脏写被拦截！",new ThinkException("可能涉及脏写被拦截"));
        }
        return ThinkResult.successIfNoNull(t);
    }



    public <T extends SimpleMongoEntity> T findById(String id , Class<T> tClass){
        return getMongoTemplate().findOne(Query.query(Criteria.where("_id").is(id)),tClass,customCollectionName);
    }

//    public <T extends SimpleMongoEntity> T save(T t){
//        this.checkAndInitIndex(t.getClass() );
//        t.setThinkUpdateKey(IdUtil.nextId());
//        IdFixTool.dataInit(t);
//        return getMongoTemplate().save(t,customCollectionName);
//    }
//


    public <T extends SimpleMongoEntity> List<T> saveAll(List<T> list){
        this.checkAndInitIndex(list.get(0).getClass());
        IdFixTool.dataInit(list);
        List<Document> list1 =new ArrayList<>();
        list.forEach(t->{
            t.setThinkUpdateKey(IdUtil.nextId());
            JSONObject jsonObject = (JSONObject) JSON.toJSON(t);
            jsonObject.put("_id",jsonObject.get("id"));
            list1.add( Document.parse(jsonObject.toJSONString()));
        });
        InsertManyResult result = getMongoTemplate().getCollection(customCollectionName).insertMany(list1);
        Map<Integer, BsonValue> insertedIds = result.getInsertedIds();
        if(list.size() == insertedIds.size()) {
            return list;
        }else{
            Set<String> insets = new HashSet<>();
            insertedIds.forEach((i,v)->{
                insets.add(v.asString().getValue());
            });
            List<T> results  = new ArrayList<>();
            for(T t : list){
                if(insets.contains(t.getId())){
                    results.add(t);
                }
            }
            return results;
        }
    }



    @Remark("必须包含ID参数才允许调用！")
    public ThinkResult<Long> pushItem(ThinkMongoQueryFilter mongoQueryFilter, String key, Serializable value) {
        Update update = new Update();
        update.push(key, value);
        update.set("thinkUpdateKey",IdUtil.nextId());
        Query query = ThinkMongoQueryBuilder.build(mongoQueryFilter,false);
        UpdateResult updateResult = this.getMongoTemplate().updateMulti(query, update, mongoQueryFilter.gettClass());

        long c = updateResult.getMatchedCount();
        if (!mongoQueryFilter.containsKey("id")) {
            throw new RuntimeException("调用PUSH方法时候，filter必须指定id");
        } else {
            return c > 0L ? ThinkResult.success(c) : ThinkResult.fail("未匹配任何数据", ResultCode.SERVER_FORBIDDEN);
        }
    }



    public <T extends SimpleMongoEntity> long count(ThinkMongoQueryFilter<T> filter){
        Query query = ThinkMongoQueryBuilder.build(filter,true);
        return getMongoTemplate().count(query,filter.gettClass(),customCollectionName);
    }

    public <T extends SimpleMongoEntity> List<T> list(ThinkMongoQueryFilter<T> filter){
        Query query = ThinkMongoQueryBuilder.build(filter,false);
        return getMongoTemplate().find(query,filter.gettClass(),customCollectionName);
    }


    public <T extends SimpleMongoEntity> List<Map<String,Object>> listExcludeKeys(ThinkMongoQueryFilter<T> filter,String... ignoreKeys){
        Query query = ThinkMongoQueryBuilder.build(filter,false);
        for (String ignoreKey : ignoreKeys) {
            query.fields().exclude(ignoreKey);
        }
        List<T> list = getMongoTemplate().find(query, filter.gettClass(),customCollectionName);
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (T t : list) {
            Map<String,Object> tmap = ObjectUtil.beanToMap(t);
            for(String k :ignoreKeys) {
                tmap.remove(k);
            }
            mapList.add(tmap);
        }
        return mapList;
    }



    public <T extends SimpleMongoEntity> List<Map<String,Object>> listForKeys(ThinkMongoQueryFilter<T> filter,String... selectKeys){
        Query query = ThinkMongoQueryBuilder.build(filter,false);
        for (String selectKey : selectKeys) {
            query.fields().include(selectKey);
        }
        List<T> list = getMongoTemplate().find(query, filter.gettClass(),customCollectionName);
        List<Map<String,Object>> mapList = new ArrayList<>();
        for (T t : list) {
            Map<String,Object> map = new HashMap();
            Map<String,Object> tmap = ObjectUtil.beanToMap(t);
            for(String k :selectKeys) {
                map.put(k,tmap.get(k));
            }
            mapList.add(map);
        }
        return mapList;
    }



    public ThinkResult<Long> delete(String id , Class targetClass){


        DeleteResult deleteResult = getMongoTemplate().remove(Query.query(Criteria.where("_id").is(id)), targetClass, customCollectionName);
        if(deleteResult.getDeletedCount()>0){
            return ThinkResult.success(deleteResult.getDeletedCount());
        }
        return ThinkResult.fastFail();
    }
}
