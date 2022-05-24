package com.think.tcp2.core.filter;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 10:46
 * @description : 连接 Fileter
 */
public interface ThinkConnectionFilter {

    boolean onConnect(String id );


    boolean onDisConnect(String id );


    boolean onConnectActive(String id );


    boolean onException(String id ,Exception e);

    boolean onIdle(String id );



}
