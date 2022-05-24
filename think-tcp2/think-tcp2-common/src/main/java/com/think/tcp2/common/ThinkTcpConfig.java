package com.think.tcp2.common;

import com.think.core.annotations.Remark;

import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 21:23
 * @description : TODO
 */
public class ThinkTcpConfig {

    private static final long defaultIdleTimeout =TimeUnit.SECONDS.toMillis(30L);

    private static long idleTimeout = TimeUnit.SECONDS.toMillis(30L);


    @Remark("默认会额外加1秒的时长，如果小于30S，那么会强制改成30")
    public static void setIdleTimeoutSecond(int timeout ,TimeUnit unit){
        idleTimeout = unit.toMillis(timeout);
        idleTimeout += 1000 ;
    }

    public static long getIdleActiveSequenceTimeMills(){
        long t = getIdleTimeoutMillis();
        return (t/2 + 5000L);
    }

    public static int getIdleActiveSequenceTimeSeconds(){
        long idleActiveSequenceTimeMills = getIdleActiveSequenceTimeMills();
        return Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(idleActiveSequenceTimeMills)).intValue();
    }

    public static long getIdleTimeoutMillis() {
        if(idleTimeout < defaultIdleTimeout){
            return defaultIdleTimeout;
        }
        return idleTimeout;
    }

    public static int getIdleTimeoutSeconds(){
        long idleTimeoutMillis = getIdleTimeoutMillis();
        return Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(idleTimeoutMillis)).intValue();

    }
}
