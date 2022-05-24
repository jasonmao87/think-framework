package com.think.tcp2.server.handler;

import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.server.ClientManager;
import com.think.tcp2.server.consumer.ThinkTcp2ServerConsumer;
import com.think.tcp2.server.ThinkTcp2ServerConsumerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/24 21:47
 * @description : TODO
 */
public class TcpPayloadHandler  extends SimpleChannelInboundHandler<TcpPayload> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpPayload o) throws Exception {
        try {
            final ThinkTcp2ServerConsumer consumer = ThinkTcp2ServerConsumerManager.getConsumer(o.getData().getClass());
            consumer.consume(ClientManager.getInstance().get(channelHandlerContext.channel()), o.getData());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
