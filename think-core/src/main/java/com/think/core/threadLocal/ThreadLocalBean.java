package com.think.core.threadLocal;

import com.think.common.util.ThinkMilliSecond;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/25 20:15
 * @description : TODO
 */
public class ThreadLocalBean implements Serializable {
    private Object value ;

    private long expireTime ;


    public ThreadLocalBean(Object o) {
        this.value = o;
        this.expireTime = ThinkMilliSecond.currentTimeMillis() + 3000L;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public Object getValue() {
        return value;
    }

    boolean isExpire(){
        return ThinkMilliSecond.currentTimeMillis() > expireTime;
    }

    protected void active(){
        this.expireTime =ThinkMilliSecond.currentTimeMillis() + 3000L;
    }
}
