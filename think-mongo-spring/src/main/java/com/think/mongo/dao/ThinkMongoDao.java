package com.think.mongo.dao;

import com.mongodb.WriteConcern;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.think.common.data.mongo.ThinkMongoQueryFilter;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.common.util.IdUtil;
import com.think.core.annotations.Remark;
import com.think.core.bean.SimpleMongoEntity;
import com.think.core.bean.util.ObjectUtil;
import com.think.exception.ThinkException;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ThinkMongoDao {
    @Autowired
    MongoTemplate mongoTemplate;

    private MongoTemplate getMongoTemplate() {
        mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoTemplate;
    }

    private void checkAndInitIndex(Class targetClass){
        try {
            String collectionName = mongoTemplate.getCollectionName(targetClass);
            ThinkMongoIndexBuilder.checkAndInitIndex(targetClass, collectionName, mongoTemplate);
        }catch (Exception e){

            log.error("",e);
        }
    }

//    public <T extends SimpleMongoEntity> T findFirstByAsc(Class<T> tClass){
//        Sort sort= Sort.by(Sort.Order.asc("_id"));
//        getMongoTemplate().findOne(new Query().with(sort),tClass);
//    }
//
//    public <T extends SimpleMongoEntity> T findFirstByDesc(){
//
//    }




    public <T extends SimpleMongoEntity> T findOneAndModify(ThinkMongoQueryFilter<T> filter ){
        Query query = ThinkMongoQueryBuilder.build(filter);
        Update update = new Update();
        filter.getModifyUpdateMapper().forEach((k, v)->{
            update.set(k,v);
        });

        filter.getModifyIncMapper().forEach((k,v)->{
            update.inc(k,v);
        });
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(false);
        return getMongoTemplate().findAndModify(query,update,findAndModifyOptions,filter.gettClass());
    }

    @Remark("只能处理匹配到的第一条记录")
    public <T extends SimpleMongoEntity> T findOneAndModify(ThinkMongoQueryFilter<T> filter ,boolean returnNew){
        Query query = ThinkMongoQueryBuilder.build(filter);
        Update update = new Update();
        filter.getModifyUpdateMapper().forEach((k, v)->{
            update.set(k,v);
        });
        filter.getModifyIncMapper().forEach((k,v)->{
            update.inc(k,v);
        });
        FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(returnNew);
        return getMongoTemplate().findAndModify(query,update,findAndModifyOptions,filter.gettClass());
    }

    public <T extends SimpleMongoEntity> T justInsert(T t){
        return insert(t);
    }

    public <T extends SimpleMongoEntity> T findById(String id ,Class<T> tClass){
        return getMongoTemplate().findOne(Query.query(Criteria.where("_id").is(id)),tClass);
    }


    public <T extends SimpleMongoEntity> T insert(T t){
        this.checkAndInitIndex(t.getClass());
        IdFixTool.dataInit(t);
        return getMongoTemplate().insert(t);
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
         t = (T) findOneAndModify(filter,true);
         if(t ==null){
             return ThinkResult.error("修改失败，可能涉及脏写被拦截！",new ThinkException("可能涉及脏写被拦截"));
         }
         return ThinkResult.successIfNoNull(t);
    }


//    @Deprecated
//    public <T extends SimpleMongoEntity> T save(T t){
//        this.checkAndInitIndex(t.getClass());
//        IdFixTool.dataInit(t);
//        return getMongoTemplate().save(t);
//    }


    public <T extends SimpleMongoEntity> List<T> saveAll(List<T> list){
        this.checkAndInitIndex(list.get(0).getClass());
        IdFixTool.dataInit(list);
        return (List<T>) getMongoTemplate().insertAll(list);
    }


    @Remark("必须包含ID参数才允许调用！")
    public ThinkResult<Long> pushItem(ThinkMongoQueryFilter mongoQueryFilter,String key , Serializable value){
        Update update = new Update();
        update.push(key,value);
        Query query = ThinkMongoQueryBuilder.build(mongoQueryFilter);
        UpdateResult updateResult = getMongoTemplate().updateMulti(query, update, mongoQueryFilter.gettClass());
        long c = updateResult.getMatchedCount();
        if (!mongoQueryFilter.containsKey("id")) {
            throw new RuntimeException("调用PUSH方法时候，filter必须指定id");
        }

        if(c>0){
            return ThinkResult.success(c);
        }else{
            return ThinkResult.fail("未匹配任何数据", ResultCode.SERVER_FORBIDDEN) ;
        }
    }




    public <T extends SimpleMongoEntity> long count(ThinkMongoQueryFilter<T> filter){
        Query query = ThinkMongoQueryBuilder.build(filter);
        return getMongoTemplate().count(query,filter.gettClass());
    }

    public <T extends SimpleMongoEntity> List<T> list(ThinkMongoQueryFilter<T> filter){
        Query query = ThinkMongoQueryBuilder.build(filter);
        return getMongoTemplate().find(query,filter.gettClass());
    }

    public <T extends SimpleMongoEntity> List<Map<String,Object>> listExcludeKeys(ThinkMongoQueryFilter<T> filter,String... ignoreKeys){
        Query query = ThinkMongoQueryBuilder.build(filter);
        for (String ignoreKey : ignoreKeys) {
            query.fields().exclude(ignoreKey);
        }
        List<T> list = getMongoTemplate().find(query, filter.gettClass());
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
        Query query = ThinkMongoQueryBuilder.build(filter);
        for (String selectKey : selectKeys) {
            query.fields().include(selectKey);
        }
        List<T> list = getMongoTemplate().find(query, filter.gettClass());
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
        DeleteResult deleteResult = getMongoTemplate().remove(Query.query(Criteria.where("_id").is(id)), targetClass);
        if(deleteResult.getDeletedCount()>0){
            return ThinkResult.success(deleteResult.getDeletedCount());
        }
        return ThinkResult.fastFail();
    }

//
//    public <T extends SimpleMongoEntity> List<T> page(int page,Class<T> tClass ){
//        Query query = new Query().skip((page-1) * 50).limit(50);
//        return getMongoTemplate().find( query,tClass);
//    }
//
//    public <T extends SimpleMongoEntity> List<T> page(String namelike,int page,Class<T> tClass ){
//        Pattern pattern = Pattern.compile("^.*" + namelike + ".*$");
//
//        Query query = new Query(Criteria.where("name").regex(pattern)).skip((page-1) * 50).limit(50);
//        return getMongoTemplate().find( query,tClass);
//    }




//    public <T extends SimpleMongoEntity> long count(Class<T> tClass){
//        return getMongoTemplate().count(new Query(),tClass);
//    }



}
