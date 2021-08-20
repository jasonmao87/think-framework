package com.think.tcp.consumer;

import com.think.tcp.TMessage;

public interface IMessageConsumer {

    void accept(String channelId,TMessage message);
}
