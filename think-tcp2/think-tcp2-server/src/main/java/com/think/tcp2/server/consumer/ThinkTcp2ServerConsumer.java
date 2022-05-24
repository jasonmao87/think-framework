package com.think.tcp2.server.consumer;

import com.think.core.annotations.Remark;
import com.think.tcp2.server.TcpClient;
import io.netty.channel.Channel;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/24 21:37
 * @description : TODO
 */
public interface ThinkTcp2ServerConsumer<T> {

    /**
     * 消费收到的消息
     * @param channel
     * @param message
     */
    @Remark("消费收到的消息")
    void consume(TcpClient client , T message);
}
