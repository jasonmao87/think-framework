package com.think.mongo.dao;

import com.think.core.bean._Entity;
import com.think.core.data.ThinkVersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Deprecated
@Repository
public  abstract class ThinkMongoVersionManager implements ThinkVersionManager {

//    @Autowired
//    ThinkMongoDao mongoDao;
//
//
//
//    @Override
//    public <T extends _Entity> T get(long targetId, int version, Class<T> targetClass) {
//        return null;
//    }
//
//    @Override
//    public <T extends _Entity> List<T> versionList(long targetId, int startVersion, int endVersion, Class<T> targetClass) {
//        return null;
//    }
}
