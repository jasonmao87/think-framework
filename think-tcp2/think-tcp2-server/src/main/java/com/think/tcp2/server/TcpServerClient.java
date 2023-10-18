package com.think.tcp2.server;

import com.think.common.util.StringUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.tcp2.common.ThinkTcpConfig;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.common.model.message.AuthResponseMessage;
import com.think.tcp2.core.listener.PayloadListenerManager;
import com.think.tcp2.core.listener.TcpPayloadEventListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 13:09
 * @description : 连接客户端模型
 */
@Slf4j
public class TcpServerClient implements Serializable {

    private static final long serialVersionUID = 8972824446970992179L;

    private boolean deny = false ;

    private String appName;

    private long initTime ;

    private long lastActiveTime ;

    private long lastIdleStateTime ;

    private Channel channel;

    /**
     * 扩展的 信息 Set
     */
    private Set<String> extendsSet ;

    private String authKey ;

    /*>>>>>>>>>>>>>>>>>>>>>>IP-ADDRESS<<<<<<<<<<<<<<<<<<<<<<<*/
    private InetSocketAddress socketAddress ;


    public String getClientHostName(){
        return socketAddress!=null?socketAddress.getHostName():"";
    }

    protected TcpServerClient(Channel channel) {
        this.channel = channel;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void idleState(){
        this.lastIdleStateTime = ThinkMilliSecond.currentTimeMillis();
    }
    public void active(){
        this.lastActiveTime = ThinkMilliSecond.currentTimeMillis();
    }

    public String getId() {
        return channel.id().asShortText();
    }

    public String getAppName() {
        return appName;
    }

    public long getInitTime() {
        return initTime;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public long getLastIdleStateTime() {
        return lastIdleStateTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public <T extends Serializable> void sendMessage(T message){
        try {
            TcpPayload payload = new TcpPayload(message);
            this.sendPayload(payload);
        }catch (Exception   e){
            e.printStackTrace();
        }
    }

    public <T extends Serializable> void sendMessageWithSession(T message,String session){
        try {
            this.active();
            TcpPayload payload = new TcpPayload(message);
            payload.setSession(session);
            this.sendPayload(payload);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void sendPayload(TcpPayload payload){
        payload.setClientId(this.getId());
        this.active();
        final List<TcpPayloadEventListener> listeners = PayloadListenerManager.getListeners();
        for (TcpPayloadEventListener listener : listeners) {
            listener.beforeSend(payload);
        }
        final ChannelFuture channelFuture = channel.writeAndFlush(payload);
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                for (TcpPayloadEventListener listener : listeners) {
                    try{
                        listener.afterSend(payload);
                    }catch (Exception e){}
                }
            }else{}
        });
    }



    public boolean isExpire(){
        return ThinkMilliSecond.currentTimeMillis() - lastIdleStateTime > (ThinkTcpConfig.getIdleTimeoutMillis() + 5000L);
    }

    public boolean isDeny() {
        if(StringUtil.isNotEmpty(authKey)) {
            return deny;
        }
        if (log.isDebugEnabled()) {
            log.debug("由于客户端 {} 未发送注册授权请求，当前状态受限！",getId());
        }
        return true;
    }

    public void setDeny(boolean deny,String message) {
        this.deny = deny;
        AuthResponseMessage authResponseMessage = new AuthResponseMessage(deny);
        authResponseMessage.setMessage(message);
        this.getChannel().writeAndFlush(authResponseMessage);
    }

    public void addExtendData(String extend){
        if(this.extendsSet == null){
            this.extendsSet = new HashSet<>();
        }
        if (!this.extendsSet.contains(extend)) {
            this.extendsSet.add(extend);
        }
    }

    public Set<String> getExtendsSet() {
        return extendsSet;
    }


    @Override
    protected void finalize() throws Throwable {
        if (this.extendsSet!=null) {
            this.extendsSet.clear();
            this.extendsSet = null;
        }
        super.finalize();
    }



    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthKey() {
        return authKey;
    }
}
