package com.think.tcp2.listener;

import com.think.tcp2.model.TcpMessage;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/3/9 16:35
 * @description : TODO
 */
public interface IThinkTcpConnectionListener {

    /**
     * 发送失败通知
     * @param message
     */
    void onMessageFail(TcpMessage message);

    /**
     * 发送成功事件
     * @param message
     */
    void onMessageSuccess(TcpMessage message);


    /**
     * 连接成功事件，返回 一个 连接id
     * @param channelId
     */
    void onConnect(String channelId);


    /**
     * 断开连接事件
     * @param channelId
     */
    void onDisConnect(String channelId);


}
