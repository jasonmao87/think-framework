package com.think.tcp;

import com.think.common.util.IdUtil;
import com.think.common.util.ThinkMilliSecond;

import java.io.Serializable;

public class TMessage implements Serializable {


    /**
     * 消息id
     */
    private long messageId ;

    /**
     * 是否广播消息 （只有服务器能发送广播消息！）
     */
    private boolean broadCast = false;

    /**
     * 消息体
     */
    private Object payload;

//    private Class payloadClass ;

    private long initTime ;

//    private String channelId;

    public TMessage( boolean broadCast, Object data ) {
//        this.payloadClass = data.getClass();
        this.messageId = IdUtil.nextId();
        this.broadCast = broadCast;
        this.payload = data;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
    }

    public TMessage(Serializable data ) {
//        this.payloadClass = data.getClass();
        this.messageId = IdUtil.nextId();
        this.payload = data;
        this.initTime =ThinkMilliSecond.currentTimeMillis();
    }


    public Object getPayload() {
        return payload;
    }

    private long getMessageId() {
        return messageId;
    }

//    protected void setChannelId(String channelId) {
//        this.channelId = channelId;
//    }
//
//    public String getChannelId() {
//        return channelId;
//    }
//


    public long getInitTime() {
        return initTime;
    }

//    public <T extends Serializable> T getData(Class<T> targetClass){
//        return (T)ObjectUtil.deserialization(this.payload,targetClass);
//    }


    public long messageId() {
        return this.messageId;
    }


//    public String channelId() {
//        return this.getChannelId();
//    }

    public long initTime() {
        return this.getInitTime();
    }

    public boolean isBroadCast() {
        return this.broadCast;
    }


}
