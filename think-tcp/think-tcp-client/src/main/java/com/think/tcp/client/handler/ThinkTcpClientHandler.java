package com.think.tcp.client.handler;

import com.think.tcp.THeartBeat;
import com.think.tcp.TcpTransModel;
import com.think.tcp.client.TcpMessageTransmissionUtil;
import com.think.tcp.client.ThinkTcpClient;
import com.think.tcp.client.IThinkTcpClientIdleListener;
import com.think.tcp.consumer.IMessageConsumer;
import com.think.tcp.listener.ITcpListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThinkTcpClientHandler extends SimpleChannelInboundHandler<TcpTransModel> {


    private static final IThinkTcpClientIdleListener idleListener(){
        return ThinkTcpClient.getIdleListener();
    }

    private static final THeartBeat heartBeat = new THeartBeat();
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpTransModel tcpTransModel) throws Exception {
        IMessageConsumer consumer = ThinkTcpClient.getInstance().getConsumer();
        if(consumer!=null){
            String channelId =channelHandlerContext.channel().id().toString();
            consumer.accept( channelId,tcpTransModel.getMessage());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ITcpListener tcpListener = ThinkTcpClient.getTcpListener();
        if(tcpListener!=null){
            tcpListener.onConnect(ctx.channel().id().toString());
        }
        if (idleListener() != null) {
            idleListener().onIdle(ctx.channel());
        }

        try {
            //当注完成时候，发  送一条心跳 ！
            TcpMessageTransmissionUtil.transmission(ctx.channel(), heartBeat, 1);
        }catch (Exception e){}

        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            TcpMessageTransmissionUtil.transmission(ctx.channel(),heartBeat,1);
            if(idleListener() !=null){
                idleListener().onIdle(ctx.channel());
            }
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try{
            log.error("Channel exception caught ： " ,cause);
//            ctx.close();
        }catch (Exception e){}
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        super.channelRegistered(ctx);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ITcpListener listener = ThinkTcpClient.getTcpListener();
        if(listener!=null){
            listener.onDisConnect(ctx.channel().id().toString());
        }
        super.channelUnregistered(ctx);
    }
}
