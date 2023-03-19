package com.think.structure;

import com.think.common.util.ThinkMilliSecond;

import java.util.ArrayList;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/15 22:34
 * @description : TODO
 */
public class ThinkTimeoutList <T> extends ArrayList<T> {


}

class TimeoutEntry{
    Object v ;
    long expireTime;

    protected static TimeoutEntry of(Object v ,long timeout){
        long expire = ThinkMilliSecond.currentTimeMillis();
        expire+= timeout;
        return new TimeoutEntry(v,expire);

    }
    private TimeoutEntry(Object v, long timeout) {
        this.v = v;
        this.expireTime = timeout;
    }



}
