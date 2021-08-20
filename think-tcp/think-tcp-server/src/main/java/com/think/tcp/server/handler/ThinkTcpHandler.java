package com.think.tcp.server.handler;

import com.think.tcp.TcpTransModel;
import com.think.tcp.server.ThinkTcpServer;
import com.think.tcp.server.manager.ThinkTcpClientShdowManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ThinkTcpHandler extends SimpleChannelInboundHandler<TcpTransModel> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpTransModel msg) throws Exception {
        ThinkTcpClientShdowManager.active(ctx.channel());
        if(ThinkTcpServer.getConsumer() !=null){
            String channelId = ctx.channel().id().toString();
            ThinkTcpServer.getConsumer().accept(channelId,msg.getMessage());
        }
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        try {
            ThinkTcpClientShdowManager.closeAndRemoveClient(ctx.channel().id().toString());
        }catch (Exception e){}
        try {
            super.channelUnregistered(ctx);
        }catch (Exception e){}
    }

}
