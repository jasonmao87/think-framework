package com.think.tcp2.server.handler;

import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.core.listener.PayloadListenerManager;
import com.think.tcp2.core.listener.TcpPayloadEventListener;
import com.think.tcp2.server.ServerClientManager;
import com.think.tcp2.server.TcpServerClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/24 21:47
 * @description : TODO
 */
@Slf4j
public class ServerTcpPayloadHandler extends SimpleChannelInboundHandler<TcpPayload> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpPayload payload) throws Exception {
        //事件响应
        final List<TcpPayloadEventListener> listeners = PayloadListenerManager.getListeners();
        for (TcpPayloadEventListener listener : listeners) {
            listener.onAccept(payload);
        }

        final TcpServerClient client = ServerClientManager.getInstance().get(channelHandlerContext.channel());
        if(client!=null){
            payload.setClientId(client.getId());
            try {
                ThinkPayloadProcessor.processPayload(payload,channelHandlerContext.channel());
            }catch (Exception e){
                e.printStackTrace();
            }

            }
    }
}
