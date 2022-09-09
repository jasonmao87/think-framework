package com.think.tcp2.client;

import com.think.core.executor.ThinkAsyncExecutor;
import com.think.tcp2.common.model.message.AuthRequestMessage;
import com.think.tcp2.common.model.message.WelMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/15 19:51
 * @description : TODO
 */
@Slf4j
public class TcpWelMessageHandler extends SimpleChannelInboundHandler<WelMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, WelMessage welMessage) throws Exception {
        Tcp2Client client = Tcp2Client.getInstance();
        log.info("收到服务器的欢饮消息： {}" ,welMessage.getMessage());
        client.connected = true;
        client.setId(welMessage.getClientId());
        client.getListener().onConnected();
        ThinkAsyncExecutor.execute(()->{
            log.info("注册客户端生命周期永久authKey = {}" ,client.getAuthKey());
            AuthRequestMessage message = new AuthRequestMessage(client.getAuthKey(),client.getApplicationName());
            channelHandlerContext.channel().writeAndFlush(message);
        });



    }
}
