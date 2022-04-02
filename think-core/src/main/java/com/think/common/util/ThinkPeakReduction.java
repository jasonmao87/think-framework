package com.think.common.util;

import com.think.core.annotations.Remark;
import com.think.core.bean.ThinkSchedule;
import com.think.core.executor.ThinkAsyncExecutor;
import com.think.core.executor.ThinkThreadExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * 削峰工具类
 * 此工具类主要用于一些并发量比较高的方法，在DB来不相应而造成 一些数据脏读脏写的风险 。
 *
 */
@Remark("使用此放方法可以针对")
@Slf4j
public class ThinkPeakReduction {
    protected static final int reductionsMillis =50;
    protected static boolean backTaskInit = false;
    private static final Map<String,PeakBean> holder = new ConcurrentHashMap<>();
    private static long lastCheckTime = 0;



    /**
     * 通过两个 关键id ，来创建一个削峰 请求，如 遇到重复且在同20毫秒内的请求，将会延迟50毫秒返回，从而给前置请求一些缓冲空间处理和锁定资源的时间，降低峰值
     * @param key1
     * @param key2
     */
    public static final void peakReduction(long key1,long key2){
        String mk = buildKey(key1,key2);
        PeakBean peakBean = holder.getOrDefault(mk, new PeakBean(key1, key2));

        int reductionResult = peakBean.reduction();
        while (reductionResult == -1 ){
            reductionResult = peakBean.reduction();
        }
        if (log.isDebugEnabled()) {
            if( reductionResult == 1){
                log.debug("瞬时请求过大，逻辑削峰介入...");
            }
        }
//
//        while (!peakBean.canReduction()) {
//            try{
//                Thread.sleep(5);
//            }catch (Exception e){}
//
//        }
//        peakBean.reduction();
        holder.put(mk,peakBean);
        callCheck();
    }
    protected static final String buildKey(long key1,long key2){
        StringBuilder stringBuilder= new StringBuilder();
        stringBuilder.append(Long.toHexString(key1)).append(Long.toHexString(key2));
        return stringBuilder.toString();
    }

    private static final void callCheck(){
        if(ThinkMilliSecond.currentTimeMillis() - lastCheckTime > (reductionsMillis*2)){
            checkData();
        }
    }


    private static synchronized void checkData(){
        lastCheckTime = ThinkMilliSecond.currentTimeMillis();
        Iterator<Map.Entry<String, PeakBean>> it = holder.entrySet().iterator();
        while (it.hasNext()){
            PeakBean peakBean = it.next().getValue();
            if (peakBean.destroyAble()) {
                it.remove();
            }
        }
        if(!holder.isEmpty()){
            if(backTaskInit == false){
                backTaskInit = true;
                //挂载后台任务 
                backTask();
            }
        }
    }


    private static void backTask(){
        ThinkThreadExecutor.addScheduledBackTaskWithToken("削峰检查",()->{
            if(DateUtil.currentMinuteOfTime() % 6 == 0) {
                // 每 6分钟执行1次
                callCheck();
            }
        }, ThinkSchedule.buildEverMinuteSchedule(13),-1,null);



//        ThinkThreadExecutor.addBackgroundTask(()->{
//            callCheck();
//            ThinkPeakReduction.backTaskInit = false;
//        },5,1,15, TimeUnit.MINUTES);
    }


}
@Slf4j
class PeakBean{

    private String mainKey ;
    private long initTime ;
    private long lastActiveTime ;

    private volatile boolean lock = false;

    public PeakBean(long key1,long key2) {
        this.mainKey = ThinkPeakReduction.buildKey(key1,key2);
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.lastActiveTime = this.initTime;
    }


    /**
     * @return  1： 削峰介入 ， 0 表示未介入
     */
    private synchronized int active(){
        lock = true;
        int r = 0 ;
        if(ThinkMilliSecond.currentTimeMillis() - lastActiveTime <ThinkPeakReduction.reductionsMillis){
            try{
                Thread.sleep(ThinkPeakReduction.reductionsMillis);
                r =1 ;
            }catch (Exception e){}
        }
        this.lastActiveTime = ThinkMilliSecond.currentTimeMillis();
        lock = false;
        return r ;
    }


    /**
     *
     * @return
     *      返回 0 表示未介入 ，1表示 介入 ，-1 表示 未执行 ，需要重新调用
     */
    public  int reduction(){
        if(lock != true){
            int  r = this.active();
            return r ;
        }
        return -1;

    }
    public boolean destroyAble(){
        return ThinkMilliSecond.currentTimeMillis() - lastActiveTime > (ThinkPeakReduction.reductionsMillis*2);
    }

}
