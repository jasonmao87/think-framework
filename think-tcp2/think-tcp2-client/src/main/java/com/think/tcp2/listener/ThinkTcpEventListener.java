package com.think.tcp2.listener;

import com.think.core.annotations.Remark;
import com.think.core.executor.ThinkAsyncTask;
import com.think.tcp2.common.model.TcpPayload;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 23:09
 * @description :  tcp 事件 listener
 */
public interface ThinkTcpEventListener {

    /**
     * 连接事件
     */
    void onConnected() ;

    /**
     * 离线事件
     */
    void onDisConnected();

   /**
     * 异常事件
     */
    void onException(Throwable throwable);


    @Remark(value = "消息发送失败事件",description = "我们可以考虑从此处处理后续事件，如重发，或者丢弃的通知等 ")
    void onMessageFail(TcpPayload payload);



}
