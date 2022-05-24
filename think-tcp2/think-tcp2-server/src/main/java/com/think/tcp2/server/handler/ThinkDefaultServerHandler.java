package com.think.tcp2.server.handler;


import com.think.tcp2.common.model.ClientModel;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.server.ClientManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 14:09
 * @description : TODO
 */
public class ThinkDefaultServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object tcpPayload) throws Exception {
        final ClientModel clientModel = ClientManager.getInstance().get(channelHandlerContext.channel().id().asShortText());
        clientModel.active();
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ClientManager.getInstance().hold(ctx.channel());
        super.channelRegistered(ctx);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (ClientManager.getInstance().isHold(ctx.channel().id().asShortText())) {
            ClientManager.getInstance().unHold(ctx.channel().id().asShortText());
        }
        super.channelUnregistered(ctx);
    }




    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("caught Exception , exception is {} " ,cause);
        }
        if (!ctx.channel().isActive()) {
            ClientManager.getInstance().unHold(ctx.channel().id().asShortText());
        }

        super.exceptionCaught(ctx, cause);
    }

}
