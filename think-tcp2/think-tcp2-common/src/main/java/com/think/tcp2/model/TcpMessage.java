package com.think.tcp2.model;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/3/9 16:27
 * @description : TCP 信息
 */
public class TcpMessage implements Serializable {
    private static final long serialVersionUID = 1997060819970608L;

    long id ;

    private String channelId ;

    private long initTime ;

    private byte[] payload ;

    public TcpMessage() {
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }


    public long getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }

    public long getInitTime() {
        return initTime;
    }

    public byte[] getPayload() {
        return payload;
    }
}
