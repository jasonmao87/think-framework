package com.think.tcp2.server.listener;

import com.think.tcp2.common.model.TcpPayload;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/14 11:19
 * @description : TODO
 */
public interface IThinkTcpClientTrigger {


    void onHold(String clientId);

    void beforeUnHold(String clientId);

    void onUnHold(String clientId );


    void onMessageSuccess(String clientId , TcpPayload payload);

    void onMessageFail(String clientId  ,TcpPayload payload) ;
}
