package com.think.tcp2.server.handler;

import com.think.exception.ThinkRuntimeException;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.server.ClientManager;
import com.think.tcp2.server.TcpClient;
import com.think.tcp2.server.handler.provider.StringMessagePrintConsumer;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/8 21:55
 * @description :
 */
@Slf4j
public class ThinkPayloadProcessor {

    private static final Set<String> freeAuthTypeSet = new HashSet<>();

    private static final Map<String, IServerMessageHandler> consumerMap =new HashMap<>();


    public boolean auth(Channel channel,String dataType){
        if (freeAuthTypeSet.contains(dataType)) {
            return true;
        }
        final TcpClient tcpClient = ClientManager.getInstance().get(channel);
        return !tcpClient.isDeny();


    }



    public static  void processPayload(TcpPayload payload, Channel channel){
        String payloadSession = payload.getSession();
        Serializable data =null;
        try{
            data = (Serializable) payload.getData();
        }catch (Exception e){
            log.error("无法解析Payload内传递对象 : " ,e );
        }
        IServerMessageHandler consumer =null;
        String dataType = data.getClass().getTypeName();
        if(consumerMap.containsKey(dataType)){
            consumer = consumerMap.get(data.getClass().getTypeName());
        }else{
            if (log.isWarnEnabled()) {
                log.warn("找不到{}对应的消息处理器，尝试使用StringMessagePrintConsumer处理",dataType);
            }
            if(data instanceof String){
                if (consumerMap.containsKey(String.class.getTypeName())) {
                    consumer = consumerMap.get(String.class.getTypeName());

                }else{
                    consumer = new StringMessagePrintConsumer();
                    consumerMap.put(String.class.getTypeName(),consumer);
                }
            }
        }
        if(consumer!=null) {
            consumer.handle(data,channel,payloadSession);
        }else{
            if (log.isDebugEnabled()) {
                log.debug("未找到何时的 消息处理器 {} -->> {} " ,data.getClass().getTypeName(),data);
                log.debug("当前注册的消息处理器" ,consumerMap);
            }
        }
    }

    @Deprecated
    public static <T extends Serializable> void bindC2SMessageHandler(String messageType , IServerMessageHandler<T> messageConsumer ){
        bindC2SMessageHandler(messageType,messageConsumer,false);
    }
    @Deprecated
    public static <T extends Serializable> void bindC2SMessageHandler(Class messageType , IServerMessageHandler<T> messageConsumer ){
        bindC2SMessageHandler(messageType,messageConsumer,false);
    }



    public static <T extends Serializable> void bindC2SMessageHandler(String messageType , IServerMessageHandler<T> messageConsumer ,boolean authAble) throws ThinkRuntimeException {
        try {
            if(authAble == false){
                //无需 授权校验的加入到 set中
                freeAuthTypeSet.add(messageType);
            }
            if (log.isInfoEnabled()) {
                log.info("服务绑定消息处理器 类型 {} ，实例 {}" ,messageType,messageConsumer.getClass().getTypeName());
            }
            Class<?> aClass = Class.forName(messageType);
            consumerMap.put(messageType, messageConsumer);
        }catch (Exception e){
            throw new ThinkRuntimeException("无法绑定Payload消息消费处理器，非法的实例对象类型 :" + messageType + " ，请使用Class.getTypeName 作为判断依据来处理Payload消息消费处理器");
        }
    }



    public static <T extends Serializable> void bindC2SMessageHandler(Class messageType , IServerMessageHandler<T> messageConsumer,boolean authAble){
        bindC2SMessageHandler(messageType.getTypeName(),messageConsumer,authAble);
    }


}
