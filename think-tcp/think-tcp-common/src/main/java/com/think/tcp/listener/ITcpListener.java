package com.think.tcp.listener;

import com.think.tcp.TMessage;

public interface ITcpListener {

    /**
     * 发送失败通知
     * @param message
     */
    void onFail(TMessage message);

    /**
     * 发送成功事件
     * @param message
     */
    void onSuccess(TMessage message);


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
