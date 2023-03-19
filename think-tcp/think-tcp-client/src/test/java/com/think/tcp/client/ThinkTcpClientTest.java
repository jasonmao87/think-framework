package com.think.tcp.client;

import com.think.common.util.IdUtil;

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
//                    L.print("收到 消息 ");
//                    String s = message.getData(String.class);
//                    L.print(s);
//
//                }
//            });
//            int i= 0;
//            while (i < 10){
//                ThinkTcpClient.getInstance().send(message);
//                Thread.sleep(3000);
//
////
//////                L.print(" thread .... count " + Thread.activeCount());
//////                L.print(Thread.getAllStackTraces().size());
////                if(false) {
////                    if (ThinkTcpClient.getInstance().isActive()) {
////                        L.print("fasong ");
////                        ThinkTcpClient.getInstance().send(message);
////
////                        ThinkTcpClient.getInstance().close();
////                    } else {
////                        L.print("重连-------------");
////                        ThinkTcpClient.getInstance().connect("127.0.0.1", 8888);
////                        ThinkTcpClient.getInstance().send(message);
////                        L.print("重连------------成功");
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