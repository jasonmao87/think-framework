package com.think.tcp2.common;

import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 21:23
 * @description : TODO
 */
public class ThinkTcpConfig {

    private static long idleTimeout = TimeUnit.SECONDS.toMillis(30L);

    public static void setIdleTimeoutSecond(int timeout ,TimeUnit unit){
        idleTimeout = unit.toMillis(timeout);
    }

    public static long getIdleTimeoutMillis() {
        return idleTimeout;
    }
}
