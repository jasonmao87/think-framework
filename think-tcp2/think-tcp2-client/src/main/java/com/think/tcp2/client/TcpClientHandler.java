package com.think.tcp2.client;

import com.think.core.executor.ThinkThreadExecutor;
import com.think.tcp2.IThinkTcpPayloadHandler;
import com.think.tcp2.common.model.Tcp2Heartbeat;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.core.listener.PayloadListenerManager;
import com.think.tcp2.core.listener.TcpPayloadEventListener;
import com.think.tcp2.listener.ThinkTcpClientEventListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 20:01
 * @description : TODO
 */
@Slf4j
public class TcpClientHandler extends SimpleChannelInboundHandler<TcpPayload> {

    private IThinkTcpPayloadHandler getConsumer() {
        return Tcp2Client.getInstance().getConsumer();
    }

    private ThinkTcpClientEventListener getListener(){
        return Tcp2Client.getInstance().getListener();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TcpPayload payload) throws Exception {
        if (getConsumer()!=null) {
            final List<TcpPayloadEventListener> iterator = PayloadListenerManager.getListeners();
            for (TcpPayloadEventListener listener : iterator) {
                if (listener!=null) {
                    try {
                        listener.onAccept(payload);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                getConsumer().acceptMessage(payload);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }else{
            log.warn("未指定IThinkTcpConsumer ，无法处理消息 :: {}" ,payload.toString());
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
        //getListener().onConnected();
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        Tcp2Client.getInstance().connected =false;

        getListener().onDisConnected();

        ThinkThreadExecutor.runDelay(()->{
            Tcp2Client.getInstance().reConnect();
        },10);
        super.channelUnregistered(ctx);
    }
}
