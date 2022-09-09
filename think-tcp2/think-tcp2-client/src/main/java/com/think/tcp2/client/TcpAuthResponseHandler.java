package com.think.tcp2.client;

import com.think.common.util.StringUtil;
import com.think.tcp2.common.model.message.AuthResponseMessage;
import com.think.tcp2.common.model.message.WelMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/7 17:33
 * @description :
 */
@Slf4j
public class TcpAuthResponseHandler  extends SimpleChannelInboundHandler<AuthResponseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthResponseMessage msg) throws Exception {
        log.info("收到 授权控制指令，当前客户端 ： {} " ,msg.isDenyState()?"通信受限":"权限正常");
        if(msg.isDenyState() && StringUtil.isNotEmpty(msg.getMessage())){
            log.info("受限原因：{}" ,msg.getMessage());
        }
        Tcp2Client tcp2Client = Tcp2Client.getInstance();
        tcp2Client.setDeny(msg.isDenyState());
        tcp2Client.getListener().onDenyStateChange();


    }
}
