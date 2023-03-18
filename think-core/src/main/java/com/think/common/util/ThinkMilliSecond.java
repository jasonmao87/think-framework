package com.think.common.util;

import com.think.core.executor.ThinkAsyncExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author
 * @Date :2021/1/8
 * @Name :ThinkMilliSecond
 * @Description : think frame work 提供得更快 获取时间戳解决方案 ！
 */
@Slf4j
public class ThinkMilliSecond {

    /**频率*/
    private long rate = 0;

    /**当前时间*/
    private volatile long now = 0;
    private static final  ThinkMilliSecond instance = new ThinkMilliSecond(2);
    private ThinkMilliSecond(long rate) {
        this.rate = rate;
        this.now = System.currentTimeMillis();
        start();
    }

    private void start() {
        ThinkAsyncExecutor.execute(()->{
            while (true) {
                now = System.currentTimeMillis();
                TimeUtil.sleep(1, TimeUnit.MILLISECONDS);
            }
        });
    }

    public static final long currentTimeMillis() {
        return instance.now ;
    }


    /**
     * 获取指定 N  时间单位后的 时间戳。
     * 如 ： 获取 10分钟后的时间戳 ： timeMillisAfter(10 , TimeUnit.MINUTES) ;
     * @param num
     * @param timeUnit
     * @return
     */
    public static final long timeMillisAfter(int num , TimeUnit timeUnit){
        long time = currentTimeMillis();
        time += timeUnit.toMillis(num);

        return time;
    }


}
