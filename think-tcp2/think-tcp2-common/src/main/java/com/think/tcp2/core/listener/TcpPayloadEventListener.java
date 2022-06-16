package com.think.tcp2.core.listener;

import com.think.tcp2.common.model.TcpPayload;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/16 14:37
 * @description : 基于 TcpPayload 的 事件监听器
 */
public interface TcpPayloadEventListener {

    /**
     * 获得 唯一名称
     * Listener Name
     * @return
     */
    default String getUniqueName() {
        return getName() + "::" +  getClass().getSimpleName();
    }

    /**
     * 读取 listener 名称
     * @return
     */
    String getName();

    /**
     * 构建事件
     * @param payload
     */
    void onInit(TcpPayload payload);

    /**
     * 重试事件
     * @param payload
     */
    default void onRetry(TcpPayload payload) { };


    void beforeSend(TcpPayload payload) ;

    void afterSend(TcpPayload payload);

}
