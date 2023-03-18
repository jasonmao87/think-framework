package com.think.tcp2.server.handler.provider;

import com.think.tcp2.server.handler.IServerMessageHandler;
import io.netty.channel.Channel;


/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/8 23:53
 * @description : TODO
 */
public class StringMessagePrintConsumer implements IServerMessageHandler<String> {

    @Override
    public void handle(String data, Channel channel,String session) {
        System.out.println( " : "+data);

    }
}
