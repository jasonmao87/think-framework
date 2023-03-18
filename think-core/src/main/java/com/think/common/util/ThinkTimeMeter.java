package com.think.common.util;

import java.time.Instant;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/21 17:23
 * @description : 计时器
 */
public class ThinkTimeMeter {
    private long begin ;

    public ThinkTimeMeter() {
        this.begin = System.currentTimeMillis();
    }


    public long cost(){
        long now = System.currentTimeMillis();
        long t = now-begin;
        begin =now;
        return t;
    }

    public static void main(String[] args) {
        ThinkTimeMeter t =new ThinkTimeMeter();
        System.out.println(Instant.now().toEpochMilli());
        System.out.println(t.cost());
        System.out.println(System.currentTimeMillis());
        System.out.println(t.cost());
    }
}
