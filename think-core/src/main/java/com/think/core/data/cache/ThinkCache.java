package com.think.core.data.cache;

import com.think.common.util.ThinkMilliSecond;
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
    @Remark("最后命中时间")
    private long lastHitTime;

    @Remark("强制过期时间")
    private long expireTime ;

    private int hitCount ;

    @Remark("最大命中间隔")
    private long maxInterval =0L;

    @Remark("最小命中间隔")
    private long minInterval =Long.MIN_VALUE;


    T data ;

    public ThinkCache(T data , long expireTime) {
        this.data = data;
        this.expireTime = expireTime;
        this.lastHitTime = ThinkMilliSecond.currentTimeMillis();
        this.initTime = lastHitTime;
    }



    public boolean isExpire(){
        return ThinkMilliSecond.currentTimeMillis() - expireTime < 0L;
    }


    public long getExpireTime() {
        return expireTime;
    }

    public long getInitTime() {
        return initTime;
    }

    public long getLastHitTime() {
        return lastHitTime;
    }

    public T getData() {
        hit();
        return data;
    }


    private void hit(){
        this.hitCount ++ ;
        long  now = ThinkMilliSecond.currentTimeMillis();
        long durationInterval = now - this.lastHitTime;
        if(durationInterval > maxInterval){
            maxInterval = durationInterval;
        }
        if(durationInterval < minInterval ){
            minInterval = durationInterval;
        }
        this.lastHitTime = ThinkMilliSecond.currentTimeMillis();
    }

    @Remark("命中数")
    public int getHitCount() {
        return hitCount;
    }


    @Remark("平均命中间隔，判断 是否 热资源 的参考指标")
    public long getHitAverageInterval(){
        if(hitCount > 0) {
            final long duration = lastHitTime - initTime;
            return duration / hitCount;
        }else {
            return Long.MAX_VALUE;
        }
    }


    @Remark("最大命中间隔 ")
    public long getMaxInterval() {
        return maxInterval;
    }

    @Remark("最小命中间隔")
    public long getMinInterval() {
        return minInterval;
    }
}
