package com.think.tcp2.server.handler;


import com.think.tcp2.common.model.TcpPayload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 14:09
 * @description : TODO
 */
@Slf4j
public class ThinkTcpServerHandler extends SimpleChannelInboundHandler<TcpPayload> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpPayload tcpPayload) throws Exception {
        log.info("读取===1 ={}" ,tcpPayload.getData());

    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx---" ,ctx.channel());
        super.channelRegistered(ctx);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }





    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
