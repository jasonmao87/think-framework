package com.think.core.data.cache;

import com.think.core.annotations.Remark;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/2 15:09
 * @description : TODO
 */
public class ThinkCache<T> {

    @Remark("初始化时间")
    private long initTime;
    @Remark("最后活跃时间")
    private long lastActiveTime ;

    @Remark("强制过期时间")
    private long expireTime ;

    T data ;

    public ThinkCache(T data , long expireTime) {
        this.data = data;
        this.expireTime = expireTime;
    }
}
