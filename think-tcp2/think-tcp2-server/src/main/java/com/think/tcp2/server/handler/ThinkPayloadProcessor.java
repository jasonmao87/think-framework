package com.think.tcp2.server.handler;

import com.think.exception.ThinkRuntimeException;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.server.consumer.IServerMessageConsumer;
import com.think.tcp2.server.consumer.provider.*;
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


    private static final Map<String, IServerMessageConsumer> consumerMap =new HashMap<>();


    public static final void processPayload(TcpPayload payload){
        Serializable data = (Serializable) payload.getData();
        IServerMessageConsumer consumer =null;
        if (log.isDebugEnabled()) {
            log.debug("message type {}",data.getClass());
        }

        if(consumerMap.containsKey(data.getClass())){
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
            consumer.consume(data);
        }
    }



    public static final <T extends Serializable> void bindC2SMessageConsumer(String messageType , IServerMessageConsumer<T> messageConsumer ) throws ThinkRuntimeException {
        try {
            final Class<?> aClass = Class.forName(messageType);
            if (aClass != null) {
                consumerMap.put(aClass.getTypeName(), messageConsumer);
            }
        }catch (Exception e){
            throw new ThinkRuntimeException("无法绑定Payload消息消费处理器，非法的实例对象类型 :" + messageType + " ，请使用Class.getTypeName 作为判断依据来处理Payload消息消费处理器");
        }
    }


    public static final <T extends Serializable> void bindC2SMessageConsumer(Class messageType , IServerMessageConsumer<T> messageConsumer ){
        bindC2SMessageConsumer(messageType.getComponentType(),messageConsumer);
    }


}
