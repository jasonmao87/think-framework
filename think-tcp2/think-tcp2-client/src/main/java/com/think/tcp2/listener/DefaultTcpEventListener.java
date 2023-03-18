package com.think.tcp2.listener;

import com.think.tcp2.client.Tcp2Client;
import com.think.tcp2.common.model.TcpPayload;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 23:46
 * @description : 默认的事件 监听器
 */
@Slf4j
public class DefaultTcpEventListener implements ThinkTcpClientEventListener {

    @Override
    public void onConnected() {
        if (log.isDebugEnabled()) {
            log.debug("连接成功");
        }
    }

    @Override
    public void onDisConnected() {
        if (log.isDebugEnabled()) {
            log.debug("连接断开");
        }
    }


    @Override
    public void onDenyStateChange() {
        Tcp2Client.getInstance();
    }

    @Override
    public void onException(Throwable throwable) {
        if (log.isDebugEnabled()) {
        }
        if (log.isErrorEnabled()) {
            log.error("遇到异常 " ,throwable);
        }

    }

    @Override
    public void onMessageFail(TcpPayload payload) {
        payload.retry();
        if (log.isDebugEnabled() && payload.getTryCount()>3) {
//            log.debug("消息发送失败 {} " ,payload.getData());
//            log.debug("会尝试重发消息.... {} / 3 ",payload.getTryCount());
        }

        if(payload.getTryCount()<3){
            try{
                Tcp2Client.getInstance().sendPayLoad(payload);
            }catch (Exception e){}
        }


    }
}
