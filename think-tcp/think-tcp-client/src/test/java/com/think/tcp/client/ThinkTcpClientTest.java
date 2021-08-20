package com.think.tcp.client;

import com.think.common.util.IdUtil;
import com.think.tcp.TMessage;
import com.think.tcp.consumer.IMessageConsumer;
import org.junit.jupiter.api.Test;

class ThinkTcpClientTest {
    static {
        try{
            IdUtil.instance(1);
        }catch (Exception e){}
    }
//
//    @Test
//    void send() {
//        TMessage message = new TMessage("123");
//
//        try{
//
//            ThinkTcpClient.getInstance().connect("127.0.0.1",8888);
//            ThinkTcpClient.getInstance().bindMessageConsumer(new IMessageConsumer() {
//                @Override
//                public void accept(TMessage message) {
//                    System.out.println("收到 消息 ");
//                    String s = message.getData(String.class);
//                    System.out.println(s);
//
//                }
//            });
//            int i= 0;
//            while (i < 10){
//                ThinkTcpClient.getInstance().send(message);
//                Thread.sleep(3000);
//
////
//////                System.out.println(" thread .... count " + Thread.activeCount());
//////                System.out.println(Thread.getAllStackTraces().size());
////                if(false) {
////                    if (ThinkTcpClient.getInstance().isActive()) {
////                        System.out.println("fasong ");
////                        ThinkTcpClient.getInstance().send(message);
////
////                        ThinkTcpClient.getInstance().close();
////                    } else {
////                        System.out.println("重连-------------");
////                        ThinkTcpClient.getInstance().connect("127.0.0.1", 8888);
////                        ThinkTcpClient.getInstance().send(message);
////                        System.out.println("重连------------成功");
////                    }
////                }
////                ThinkTcpClient.getInstance().send(message);
////
//
//
//            }
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//    }
}