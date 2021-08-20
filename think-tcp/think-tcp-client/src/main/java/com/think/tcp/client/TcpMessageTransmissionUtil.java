package com.think.tcp.client;

import com.think.common.util.FastJsonUtil;
import com.think.tcp.TcpTransModel;
import com.think.tcp.listener.ITcpListener;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpMessageTransmissionUtil {



    public static void transmission(Channel channel , Object message, final int c){
        if(channel.isActive() == false){
            if(log.isDebugEnabled()) {
                log.debug("channel 状态尚未处于ACTIVE 状态，放弃发送消息 :{}->{}...", message.getClass(), message.toString());
            }
            return;
        }
        ITcpListener listener = ThinkTcpClient.getInstance().getTcpListener();
        if(message instanceof TcpTransModel){
            if(c > 1) {
                ((TcpTransModel) message).fireRetry();
            }
        }
        if ( (c + 1) > 3) {
            //尝试次数超过3次，放弃 ！
            if(log.isWarnEnabled()){
                //log.warn(" channel state {}  未发送成功的消息 toString{} ，class ={}",channel.isActive(),message.toString(),message.getClass());
                log.warn("消息尝试发送超过3次未成功，放弃传输，消息详情[JSON格式]\n\t= {}\n{}", FastJsonUtil.parseToJSON(message),message);
            }
            if(listener!=null){
                if(message instanceof TcpTransModel){
                    listener.onFail(((TcpTransModel) message).getMessage());
                }
            }
            return;
        }

        channel.writeAndFlush(message)
                .addListener(w -> {
                    if (!w.isSuccess()) {
                        transmission(channel, message, c +1);
                    }else{
                        if(listener!=null){
                            if(message instanceof TcpTransModel){
                                listener.onSuccess(((TcpTransModel) message).getMessage());
                            }
                        }
                    }
                });
    }
}
