package com.think.tcp2.server.consumer.provider;

import com.think.tcp2.server.consumer.IServerMessageConsumer;
import io.netty.channel.Channel;


/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/8 23:53
 * @description : TODO
 */
public class StringMessagePrintConsumer implements IServerMessageConsumer<String> {

    @Override
    public void consume(String data, Channel channel) {
        System.out.println( " : "+data);

    }
}
