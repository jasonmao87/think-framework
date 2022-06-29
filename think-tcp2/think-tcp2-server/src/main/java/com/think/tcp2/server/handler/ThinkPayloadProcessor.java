package com.think.tcp2.server.handler;

import com.think.exception.ThinkRuntimeException;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.server.handler.provider.StringMessagePrintConsumer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/8 21:55
 * @description :
 */
@Slf4j
public class ThinkPayloadProcessor {


    private static final Map<String, IServerMessageHandler> consumerMap =new HashMap<>();




    public static final void processPayload(TcpPayload payload, Channel channel){

        Serializable data =null;
        try{
            data = (Serializable) payload.getData();
        }catch (Exception e){
            log.error("无法解析Payload内传递对象 : " ,e );
        }
        IServerMessageHandler consumer =null;
//        if (log.isDebugEnabled()) {
//            log.debug("message type {}",data.getClass());
//        }
        String dataType = data.getClass().getTypeName();
//        log.info("消息数据类型：{}" ,data.getClass().getTypeName() );

        if(consumerMap.containsKey(dataType)){
            consumer = consumerMap.get(data.getClass().getTypeName());
        }else{

            if(data instanceof String){
                if (consumerMap.containsKey(String.class.getTypeName())) {
                    consumer = consumerMap.get(String.class.getTypeName());

                }else{
                    consumer = new StringMessagePrintConsumer();
                    consumerMap.put(String.class.getTypeName(),consumer);
                }
            }
        }
        //最终的执行环节
        if(consumer!=null) {
            consumer.handle(data,channel);
        }else{
            log.info("未找到何时的 消息处理器 {} -->> {} " ,data.getClass().getTypeName(),data);
            log.info("当前注册的消息处理器" ,consumerMap);
        }
    }



    public static final <T extends Serializable> void bindC2SMessageHandler(String messageType , IServerMessageHandler<T> messageConsumer ) throws ThinkRuntimeException {
        try {
            log.info("服务绑定消息处理器 类型 {} ，实例 {}" ,messageType,messageConsumer.getClass().getTypeName());
            final Class<?> aClass = Class.forName(messageType);
            if (aClass != null) {
                consumerMap.put(messageType, messageConsumer);
            }
        }catch (Exception e){
            throw new ThinkRuntimeException("无法绑定Payload消息消费处理器，非法的实例对象类型 :" + messageType + " ，请使用Class.getTypeName 作为判断依据来处理Payload消息消费处理器");
        }
    }


    public static final <T extends Serializable> void bindC2SMessageHandler(Class messageType , IServerMessageHandler<T> messageConsumer ){
        bindC2SMessageHandler(messageType.getTypeName(),messageConsumer);
    }


}
