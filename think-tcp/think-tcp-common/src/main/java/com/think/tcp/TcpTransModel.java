package com.think.tcp;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * tcp数据传输对象
 */
public class TcpTransModel implements Serializable {
    private static final long serialVersionUID = 1017857856186897879L;
    /**
     * 传输对象
     */
    private TMessage message = null;

    private int retryCount = 0;

    public TcpTransModel(TMessage message, Channel channel ) {
        this.message = message;
        //this.message.setChannelId(channel.id().toString());
    }

    public TMessage getMessage() {
        return message;
    }


    public int getRetryCount() {
        return retryCount;
    }

    /**
     * 触发一次重试
     */
    public void fireRetry(){
        this.retryCount ++ ;
    }
}
