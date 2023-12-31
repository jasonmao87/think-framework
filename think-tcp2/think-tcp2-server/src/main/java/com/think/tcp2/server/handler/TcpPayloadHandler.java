package com.think.tcp2.server.handler;

import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.server.ClientManager;
import com.think.tcp2.server.TcpClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/24 21:47
 * @description : TODO
 */
@Slf4j
public class TcpPayloadHandler  extends SimpleChannelInboundHandler<TcpPayload> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpPayload payload) throws Exception {
//        log.info("TcpPayloadHandler  收到消息 {} ",payload);
            final TcpClient client = ClientManager.getInstance().get(channelHandlerContext.channel());
            if(client!=null){
                //初始化 payLoad 的clientId
                payload.setClientId(client.getId());
                try {
                    ThinkPayloadProcessor.processPayload(payload,channelHandlerContext.channel());
//                    final ThinkTcp2ServerConsumer consumer = ThinkTcp2ServerConsumerManager.getConsumer(payload.getData().getClass());
//                    consumer.consume(ClientManager.getInstance().get(channelHandlerContext.channel()), payload.getData());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
    }
}
