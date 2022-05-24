package com.think.tcp2.server;

import com.think.common.util.ThinkMilliSecond;
import com.think.tcp2.common.ThinkTcpConfig;
import com.think.tcp2.common.model.TcpPayload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 13:09
 * @description : 连接客户端模型
 */
public class TcpClient implements Serializable {

    private static final long serialVersionUID = 8972824446970992179L;


    private String name ;

    private long initTime ;

    private long lastActiveTime ;

    private long lastIdleStateTime ;

    private Channel channel;


    protected TcpClient(Channel channel) {
        this.channel = channel;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
    }

    public void setName(String name) {
        this.name = name;
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

    public String getName() {
        return name;
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

    public void sendMessage(Object message){
        TcpPayload payload = new TcpPayload(message);
        channel.writeAndFlush(payload);
    }

    public boolean isExpire(){
        return ThinkMilliSecond.currentTimeMillis() - lastIdleStateTime > (ThinkTcpConfig.getIdleTimeoutMillis() + 5000L);
    }
}
