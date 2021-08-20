package com.think.mongo.dao;

import com.think.common.util.IdUtil;
import com.think.common.util.StringUtil;
import com.think.core.bean.SimpleMongoEntity;

import java.util.List;

public class IdFixTool {

    protected static final <T extends SimpleMongoEntity> T dataInit(T t){
        if(StringUtil.isEmpty(t.getId())){
            t.setId(IdUtil.nextId() +"");
        }
        return t ;
    }


    protected static final <T extends SimpleMongoEntity> List<T> dataInit(List<T> list){
        for (T t : list) {
            if(StringUtil.isEmpty(t.getId())){
                t.setId(IdUtil.nextId() +"");
            }
        }
        return list;
    }

}
