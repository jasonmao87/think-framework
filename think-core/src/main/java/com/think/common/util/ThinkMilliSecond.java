package com.think.common.util;

import lombok.extern.slf4j.Slf4j;

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(rate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    now = System.currentTimeMillis();
                }
            }
        }).start();
    }

    public static final long currentTimeMillis() {
        return instance.now ;
    }


}
