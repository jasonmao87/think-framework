package com.think.tcp.server.manager;

import com.think.common.util.ThinkMilliSecond;
import com.think.common.util.TimeUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author JasonMao
 */
@Slf4j
public class ThinkTcpClientShdowManager {
    private static final Map<String, ThinkTcpClientShadowModel> clientHolder = new ConcurrentHashMap<>();
    private static long lastCheck = 0L ;
    private static volatile boolean lockCheck = false;


    public static final List<ThinkTcpClientShadowModel> allClient(){
        List<ThinkTcpClientShadowModel> list = new ArrayList<>();
        for (Map.Entry<String, ThinkTcpClientShadowModel> entry : clientHolder.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }
    /**
     * 通过 channelid 获取到链接客户端的对象
     * @param channelId
     * @return
     */
    public static final Optional<ThinkTcpClientShadowModel> getClient(String channelId){
        return  Optional.ofNullable(clientHolder.get(channelId));
    }


    /**
     * 心跳活跃事件
     * @param channel
     */
    public static final void active(Channel channel){
        String channelId = channel.id().toString();
        if(clientHolder.containsKey(channelId)){
            clientHolder.get(channelId).active();
        }else {

            if(log.isDebugEnabled()){
                log.debug("hold a new tcpClient , current count is  {}" ,clientHolder.size() +1 );
            }
            clientHolder.put(channelId,new ThinkTcpClientShadowModel(channel));
        }
    }


    /**
     * 心跳超时事件 ？
     *  每隔开50分中 会检查一次
     * @param channel
     */
    public static final void idle(Channel channel){
        long now = ThinkMilliSecond.currentTimeMillis();
        long _45Minutes = TimeUtil.MILLIS_OF_MINUTES(45);
        if(now -lastCheck >_45Minutes){
            try{
                callCheckAll();
            }catch (Exception e){}
        }
        String channelId = channel.id().toString();
        if(clientHolder.containsKey(channelId)){
            ThinkTcpClientShadowModel client = clientHolder.get(channelId);
            if(client.isHealth()){
                client.closeWithoutNotice();
                clientHolder.remove(channelId);
            }else {
                client.idle();
            }
        }
    }


    public static  final void callCheckAll(){
        lastCheck = ThinkMilliSecond.currentTimeMillis();
        if (tryLockCheck()) {
            Iterator<Map.Entry<String, ThinkTcpClientShadowModel>> iterator = clientHolder.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, ThinkTcpClientShadowModel> next = iterator.next();
                if(next!=null){
                    if (checkDead(next.getValue())) {
                        closeAndRemoveClient(next.getKey());
                    }
                }
            }
            unlockCheck();
        }
    }

    private synchronized static boolean checkDead(ThinkTcpClientShadowModel model){
        if(model == null){
            return true;
        }
        if( model.isExpire()){
            long now =ThinkMilliSecond.currentTimeMillis();
            if(now - model.getLastActiveTime() > (1000*60*60)){
                return true;
            }
        }
        return false;

    }

//    private synchronized static void lockCheck(){
//        lockCheck = true;
//    }
//

    private synchronized static boolean tryLockCheck(){
        if(!lockCheck){
            lockCheck = true;
            return true;
        }else{
            return false;
        }
    }

    private synchronized static void unlockCheck(){
        lockCheck = false;
    }


    /**
     * 关闭并移除客户端
     * @param channelId channelId
     */
    public static final void closeAndRemoveClient(String channelId){
        if(clientHolder.containsKey(channelId)) {
            ThinkTcpClientShadowModel client = clientHolder.get(channelId);
            if(client!=null) {
                client.closeWithoutNotice();
            }
        }
        clientHolder.remove(channelId);
    }


}
