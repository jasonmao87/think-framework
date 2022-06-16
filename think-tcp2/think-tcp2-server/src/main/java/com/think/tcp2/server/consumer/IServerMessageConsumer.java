package com.think.tcp2.server.consumer;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/8 23:46
 * @description : TODO
 */
public interface IServerMessageConsumer<T extends Serializable> {

    /**
     * 处理payload内 对象
     * @param data
     * @param channel
     */
    void consume(T data, Channel channel);

}
