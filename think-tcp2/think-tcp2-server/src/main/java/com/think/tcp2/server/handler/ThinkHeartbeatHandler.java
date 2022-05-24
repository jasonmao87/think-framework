package com.think.tcp2.server.handler;

import com.think.tcp2.common.model.ClientModel;
import com.think.tcp2.common.model.Tcp2Heartbeat;
import com.think.tcp2.server.ClientManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
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
        final String id = channelHandlerContext.channel().id().asShortText();
        if (ClientManager.getInstance().isHold(id)) {
            ClientManager.getInstance().get(id).idleState();
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            String id = ctx.channel().id().asShortText();
            final ClientModel clientModel = ClientManager.getInstance().get(id);
            if(clientModel!=null) {
                if (clientModel.isExpire()) {
                    ClientManager.getInstance().unHold(id);
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
