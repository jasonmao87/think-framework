package com.think.structure;

import com.think.common.util.ThinkMilliSecond;
import com.think.core.annotations.Remark;
import com.think.core.executor.ThinkBackgroundTask;
import com.think.core.executor.ThinkThreadExecutor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;

/**
 * @Date :2021/3/24
 * @Name :ThinkCacheMap
 * @Description : think 缓存
 */
public class ThinkCacheMap {

    @Remark("生命周期，有效时长（分钟），默认30分钟")
    private static int lifeCycleMinutes = 30 ;
    @Remark("缓存最大数量，默认1024")
    private static int maxSize = 1024 ;

    private static TCacheBean[] cacheBeans = new TCacheBean[1024];

    static {
        ThinkThreadExecutor.addBackgroundTask(new ThinkBackgroundTask() {
            @Override
            public void execute() {
                check();
            }
        },29,-1);
    }

    /**
     * 新增一个缓存
     * @param key
     * @param value
     */
    public static synchronized void put(String key ,Object value){
        boolean set = false;
        int index = getIndex(key);
        if(index > 0){
            cacheBeans[index] = new TCacheBean(key, value);
        }else {
            for (int i = 0; i < maxSize; i++) {
                if (cacheBeans[i] == null) {
                    cacheBeans[i] = new TCacheBean(key, value);
                    set = true;
                }
                if (set) {
                    break;
                }
            }
        }
        if(set == false){
            for (int i = 0; i < 10; i++) {
                tryRemoveOne();
            }
        }

    }

    private static int getIndex(String key){
        for (int i = 0; i < maxSize; i++) {
            TCacheBean bean = cacheBeans[i];
            if(bean!=null && !bean.isExpire()){
                if(cacheBeans[i].getKey().equals(key)){
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 读取缓存，已经过期的缓存 不会被读取
     * @param key
     * @return
     */
    public static Object get(String key) {
        int index = getIndex(key);
        if(index>0){
            try{
                //存在 当时被清理得风险，所以 try 掉
                cacheBeans[index].hit();
                return cacheBeans[index];
            }catch (Exception e){}
        }
        return null;
    }

    public static int getLifeCycleMinutes() {
        return lifeCycleMinutes;
    }

    public static int getMaxSize() {
        return maxSize;
    }

    public static void setLifeCycleMinutes(int lifeCycleMinutes) {
        ThinkCacheMap.lifeCycleMinutes = lifeCycleMinutes;
    }


    private static final int tryRemoveOne(){
        long minHit = Integer.MAX_VALUE ;
        int minHitIndex = -1;
        long minExpire =Long.MAX_VALUE;
        int minExpireIndex = -1;
        for (int i = 0; i < maxSize; i++) {
            TCacheBean bean = cacheBeans[i];
            if(bean!=null && !bean.isExpire()){
                long hit = bean.getHitCount();
                long expire = bean.getExpire();
                if(hit<minHit){
                    minHitIndex = i ;
                    minHit = hit;
                }
                if(minExpire < expire){
                    minExpireIndex = i;
                    minExpire = expire;
                }
            }
        }

        long now = ThinkMilliSecond.currentTimeMillis();
        if(now - minExpire <(60000)){
            //如果存在 1分钟内 要到期得数据，那么 优先移除
            cacheBeans[minExpireIndex] = null;
            return minExpireIndex;
        }
        if(minHit <10){
            cacheBeans[minHitIndex] = null;
            return minHitIndex;
        }

        if(minExpire < 120000){
            cacheBeans[minExpireIndex] = null;
            return minExpireIndex;
        }
        if(minHit <20){
            cacheBeans[minHitIndex] = null;
            return minHitIndex;
        }
        cacheBeans[minExpireIndex] = null;
        return minExpireIndex;
    }


    /**
     * 检查 缓存 ，移除 过期数据
     */
    private static final void check(){
        for (int i = 0; i < maxSize; i++) {
            if(cacheBeans[i] != null) {
                if(cacheBeans[i].isExpire()){
                    cacheBeans[i] = null;
                }
            }
        }
    }
}

class TCacheBean{
    BlockingDeque blockingDeque;

    private String key ;
    private Object value ;
    private long expire ;
    private long hitCount =0L;

    public TCacheBean(String key, Object value) {
        this.key = key;
        this.value = value;
        this.expire = ThinkMilliSecond.currentTimeMillis() + (ThinkCacheMap.getLifeCycleMinutes());
    }

    public long getExpire() {
        return expire;
    }

    public long getHitCount() {
        return hitCount;
    }

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
    public void hit(){
        this.hitCount ++;
    }

    public boolean isExpire(){
        return ThinkMilliSecond.currentTimeMillis() > this.expire;
    }
}
