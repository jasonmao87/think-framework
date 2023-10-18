package com.think.tcp2.server.handler;

import com.think.tcp2.common.model.Tcp2Heartbeat;
import com.think.tcp2.server.ServerClientManager;
import com.think.tcp2.server.TcpServerClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 20:38
 * @description : TODO
 */
@Slf4j
public class ThinkHeartbeatHandler extends SimpleChannelInboundHandler<Tcp2Heartbeat> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Tcp2Heartbeat tcp2Heartbeat) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("ThinkHeartbeatHandler 收到消息 {} " ,tcp2Heartbeat);
        }
        final String id = channelHandlerContext.channel().id().asShortText();
        if (ServerClientManager.getInstance().isHold(id)) {
            ServerClientManager.getInstance().get(id).idleState();
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            final TcpServerClient clientModel = ServerClientManager.getInstance().get( ctx.channel());
            if(clientModel!=null) {
                if (clientModel.isExpire()) {
                    ServerClientManager.getInstance().unHold(ctx.channel());
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
