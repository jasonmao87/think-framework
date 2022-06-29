package com.think.tcp2.client;

import com.think.tcp2.common.model.WelMessage;
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
        log.info("收到服务器的欢饮消息： {}" ,welMessage.getMessage());
        Tcp2Client.getInstance().connected = true;
        Tcp2Client.getInstance().setId(welMessage.getClientId());
        Tcp2Client.getInstance().getListener().onConnected();
    }
}
