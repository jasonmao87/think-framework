package com.think.tcp.server.handler;

import com.think.tcp.THeartBeat;
import com.think.tcp.server.ThinkTcpServer;
import com.think.tcp.server.manager.ThinkTcpClientShdowManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThinkHeartBeatHandler extends SimpleChannelInboundHandler<THeartBeat> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, THeartBeat tHeartBeat) throws Exception {
        ThinkTcpClientShdowManager.active(channelHandlerContext.channel());
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            ThinkTcpClientShdowManager.idle(ctx.channel());
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active  {} " ,ctx.channel().id().toString());
        if(ThinkTcpServer.getTcpListener()!=null){
            ThinkTcpServer.getTcpListener().onConnect(ctx.channel().id().toString());
        }else{
            if (log.isDebugEnabled()) {
                log.debug("未找到Listener ....");
            }
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if(ThinkTcpServer.getTcpListener()!=null){
            ThinkTcpServer.getTcpListener().onDisConnect(ctx.channel().id().toString());
        }
        super.channelUnregistered(ctx);
    }
}
