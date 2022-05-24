package com.think.tcp2.server.consumer;

import com.think.tcp2.server.TcpClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/24 21:40
 * @description : TODO
 */
@Slf4j
public class ThinkTcp2DefaultConsumer implements ThinkTcp2ServerConsumer<Object> {

    @Override
    public void consume(TcpClient client, Object message) {
//        log.info("THINK TCP2 DEFAULT CONSUMER GET MESSAGE : {}" ,message);
    }
}
