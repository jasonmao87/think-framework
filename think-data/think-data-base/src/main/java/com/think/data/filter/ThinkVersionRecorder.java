package com.think.data.filter;

import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.core.annotations.Remark;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;

@Remark("控制版本记录器")
public interface ThinkVersionRecorder {


    <T extends SimplePrimaryEntity> void recodeInsert(T t);

    <T extends SimplePrimaryEntity> void recodeUpdate(T t);

    <T extends SimplePrimaryEntity> void recodeUpdate(ThinkUpdateMapper mapper);

    <T extends SimplePrimaryEntity> void recodeDelete(ThinkUpdateMapper mapper);


    <T extends SimplePrimaryEntity> T getVersionData(Class<T> tClass ,long id ,int versionNo );

}
