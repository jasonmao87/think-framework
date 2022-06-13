package com.think.tcp2.common.model;

import com.think.common.util.ThinkMilliSecond;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 15:56
 * @description : TODO
 */
public class TcpPayload<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -2773780527532750730L;

    /**
     * 传输内容
     */
    private T data ;

    /**
     * 构建时间
     */
    private long initTime ;


    /**
     * 重试次数
     */
    private int tryCount = 0 ;

    /**
     * 发送 或者 接收消息的 clientId
     */
    private String clientId ;


    public TcpPayload(T data) {
        this.data = data;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
    }

    public TcpPayload retry(){
        this.tryCount ++ ;
        return this;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public T getData() {
        return data;
    }

    public long getInitTime() {
        return initTime;
    }

    public int getTryCount() {
        return tryCount;
    }

    public String getClientId() {
        return clientId;
    }
}
