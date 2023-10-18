package com.think.tcp2.server.handler;


import com.think.tcp2.server.ServerClientManager;
import com.think.tcp2.server.TcpServerClient;
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
        if (log.isTraceEnabled()) {
            log.trace("默认handler 收到消息 {} ",tcpPayload);
        }
        final TcpServerClient clientModel = ServerClientManager.getInstance().get(channelHandlerContext.channel());
        clientModel.active();

    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        ClientManager.getInstance().hold(ctx.channel());
//        super.channelRegistered(ctx);
////        log.info("发送欢饮消息");
//        final WelMessage welcome = WelMessage.welcome();
//        welcome.setClientId(ClientManager.getInstance().get(ctx.channel()).getId());
//        ctx.channel().writeAndFlush(welcome);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (ServerClientManager.getInstance().isHold(ctx.channel())) {
            ServerClientManager.getInstance().unHold(ctx.channel());
        }
        super.channelUnregistered(ctx);
    }




    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("caught Exception , exception is {} " ,cause);
        }
        if (!ctx.channel().isActive()) {
            ServerClientManager.getInstance().unHold(ctx.channel());
        }
        try{
            ctx.channel().close();
        }catch (Exception e){}

        super.exceptionCaught(ctx, cause);
    }

}
