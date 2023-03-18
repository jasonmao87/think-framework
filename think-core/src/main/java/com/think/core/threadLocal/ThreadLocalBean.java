package com.think.core.threadLocal;

import com.think.common.util.ThinkMilliSecond;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/25 20:15
 * @description : TODO
 */
public class ThreadLocalBean<T> implements Serializable {
    private T value ;

    private long expireTime ;


    public ThreadLocalBean(T o) {
        this.value = o;
        this.expireTime = ThinkMilliSecond.currentTimeMillis() + 3000L;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public T getValue() {
        this.expireTime += 1000;
        return value;
    }

    public boolean isExpire(){
        return ThinkMilliSecond.currentTimeMillis() > expireTime;
    }

    protected void active(){
        this.expireTime =ThinkMilliSecond.currentTimeMillis() + 3000L;
    }
}
