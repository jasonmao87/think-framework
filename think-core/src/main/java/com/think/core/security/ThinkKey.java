package com.think.core.security;

import com.think.common.util.ThinkMilliSecond;

public class ThinkKey {
    private String key ;
    private long expireTime ;
    private long initTime ;

    protected ThinkKey(String key ,long expireTime ) {
        this.key = key;
        this.expireTime =expireTime;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
    }
}
