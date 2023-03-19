package com.think.tcp.client;

import io.netty.channel.Channel;

public interface IThinkTcpClientIdleListener {

    void onIdle(Channel channel);
}
