package com.think.tcp2.server.handler;

import com.think.tcp2.common.model.message.AuthRequestMessage;
import com.think.tcp2.server.ServerClientManager;
import com.think.tcp2.server.TcpServerClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/7 19:17
 * @description :
 */
@Slf4j
public class ThinkAuthRequestHandler extends SimpleChannelInboundHandler<AuthRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuthRequestMessage msg) throws Exception {
        TcpServerClient client = ServerClientManager.getInstance().get(ctx.channel());
        client.setAuthKey(msg.getAuthKey());
        client.setAppName(msg.getClientAppName());
        //检查是否deny
        log.info("收到 注册 申请 --- {} {}" ,msg.getClientAppName(),msg.getAuthKey());
        boolean deny = ServerClientManager.getInstance().checkIsDenyByAuthKey(msg.getAuthKey());
        log.info("处理 注册程序，该客户端 是否受限 ：{} " ,deny);
        client.setDeny(deny,"初始化恢复配置");

    }
}
