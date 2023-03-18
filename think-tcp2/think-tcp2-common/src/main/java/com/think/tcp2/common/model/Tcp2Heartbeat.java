package com.think.tcp2.common.model;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 20:35
 * @description : 心跳包
 */
public class Tcp2Heartbeat implements Serializable {
    private static final long serialVersionUID = 3093639250703976883L;
    private static  Tcp2Heartbeat heartbeat ;
    public static final Tcp2Heartbeat get(){
        if(heartbeat == null){
            heartbeat = new Tcp2Heartbeat();
        }
        return heartbeat;
    }

}
