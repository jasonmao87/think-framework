package com.think.tcp2.server.handler;

import com.think.tcp2.common.model.message.WelMessage;
import com.think.tcp2.server.ServerClientManager;
import com.think.tcp2.server.TcpServerClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/8 10:50
 * @description :
 */
@Slf4j
public class ThinkTcpAddressFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {

    public static Map<String,InetSocketAddress> socketAddressMap;

    public ThinkTcpAddressFilter() {
        synchronized (this) {
            if (socketAddressMap == null) {
                socketAddressMap = new HashMap<>();
            }
        }
    }

    public String clientId(ChannelHandlerContext ctx){
        String clientId = ctx.channel().id().asShortText();
        return clientId;
    }

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) throws Exception {
        log.debug("新的客户端连接 :{} {}" ,clientId(ctx),remoteAddress);
        if (ServerClientManager.getInstance().isHold(ctx.channel())) {
            log.debug("客户端[{}]，设置IP信息：{}",clientId(ctx),remoteAddress);
            ServerClientManager.getInstance().get(ctx.channel()).setSocketAddress(remoteAddress);
        }else{
            socketAddressMap.put(clientId(ctx),remoteAddress);
        }

        return true;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String clientId =clientId(ctx);
        try {
            ServerClientManager.getInstance().hold(ctx.channel());
            WelMessage welcome = WelMessage.welcome();
            /*>>>>>>>>>>>>>>>>>>>>>>-发送WEL MESSAGE-<<<<<<<<<<<<<<<<<<<<<<<*/
            TcpServerClient client = ServerClientManager.getInstance().get(ctx.channel());
            welcome.setClientId(client.getId());
            ctx.channel().writeAndFlush(welcome);
            /*>>>>>>>>>>>>>>>>>>>>>>-绑定ip-<<<<<<<<<<<<<<<<<<<<<<<*/
            InetSocketAddress inetSocketAddress = socketAddressMap.get(clientId);
            if (inetSocketAddress != null) {
                log.debug("绑定地址信息 {} -> {}" ,inetSocketAddress,client.getId());
                client.setSocketAddress(inetSocketAddress);
            }
        }catch (Exception e){
            log.error("E",e);
        }finally {
            /*>>>>>>>>>>>>>>>>>>>>>>-移除缓存-<<<<<<<<<<<<<<<<<<<<<<<*/
            socketAddressMap.remove(clientId);
        }
        super.channelRegistered(ctx);
    }

    @Override
    protected ChannelFuture channelRejected(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {

        return super.channelRejected(ctx, remoteAddress);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (ServerClientManager.getInstance().isHold(ctx.channel())) {
            ServerClientManager.getInstance().unHold(ctx.channel());
        }
        super.channelUnregistered(ctx);
    }
}


