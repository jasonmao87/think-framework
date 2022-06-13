package com.think.tcp2.server.consumer.provider;

import com.think.tcp2.server.consumer.IServerMessageConsumer;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/8 23:53
 * @description : TODO
 */
public class StringMessagePrintConsumer implements IServerMessageConsumer<String> {

    @Override
    public void consume(String data) {
        System.out.println(data);

    }
}
