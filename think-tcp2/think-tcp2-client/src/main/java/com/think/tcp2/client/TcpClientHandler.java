package com.think.tcp2.client;

import com.think.core.executor.ThinkThreadExecutor;
import com.think.tcp2.IThinkTcpConsumer;
import com.think.tcp2.common.model.Tcp2Heartbeat;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.listener.IThinkTcpConnectionListener;
import com.think.tcp2.listener.ThinkTcpEventListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 20:01
 * @description : TODO
 */
public class TcpClientHandler extends SimpleChannelInboundHandler<TcpPayload> {

    private IThinkTcpConsumer getConsumer() {
        return Tcp2Client.getInstance().getConsumer();
    }

    private ThinkTcpEventListener getListener(){
        return Tcp2Client.getInstance().getListener();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpPayload o) throws Exception {
        if (getConsumer()!=null) {
            getConsumer().acceptMessage(o);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //发送心跳包
        ctx.channel().writeAndFlush(Tcp2Heartbeat.get());
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        getListener().onConnected();
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        getListener().onDisConnected();
        ThinkThreadExecutor.runDelay(()->{
            Tcp2Client.getInstance().reConnect();
        },10);
        super.channelUnregistered(ctx);
    }
}
