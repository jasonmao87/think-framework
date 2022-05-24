package com.think.tcp2.server;

import com.think.tcp2.server.consumer.ThinkTcp2DefaultConsumer;
import com.think.tcp2.server.consumer.ThinkTcp2ServerConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/24 21:39
 * @description : TODO
 */
public class ThinkTcp2ServerConsumerManager {


    private static final Map<Class, ThinkTcp2ServerConsumer> consumerMap = new HashMap<>();

    private static ThinkTcp2ServerConsumer defaultConsumer = new ThinkTcp2DefaultConsumer();

    public static final void registerConsumer(Class type , ThinkTcp2ServerConsumer consumer){
        consumerMap.put(type,consumer);
    }

    public static final void setDefaultConsumer(ThinkTcp2ServerConsumer<Object> defaultConsumer){
        ThinkTcp2ServerConsumerManager.defaultConsumer = defaultConsumer;
    }


    public static  <T> ThinkTcp2ServerConsumer<T> getConsumer(Class<T> tClass){
        return consumerMap.getOrDefault(tClass,defaultConsumer);
    }

//    public static <T> ThinkTcp2ServerConsumer<T> getConsumerByMessage(Object message){
//
//    }

    public static final void getClassInfo(Object o ){
        System.out.println( o.getClass());
    }



}
