package com.think.tcp2.server;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 16:07
 * @description : TODO
 */
@Slf4j
public class ClientManager {

    private static ClientManager instance ;
    private final Map<String, TcpClient> clientHolder ;


    private ClientManager() {
        clientHolder = new ConcurrentHashMap<>();
    }

    public static final ClientManager getInstance() {
        if(instance == null){
            instance = new ClientManager();
        }
        return instance;
    }

    public void hold(Channel channel){
        this.hold(new TcpClient(channel));
    }

    private void hold(TcpClient client){
        if (log.isDebugEnabled()) {
            log.debug("注册新的客户端---- {}" ,client.getId());
        }
        clientHolder.put(client.getId(),client);
    }

    /**
     * 检查是否托管了客户端
     * @param id
     * @return
     */
    public boolean isHold(String id ){
        return this.clientHolder.containsKey(id) && this.clientHolder.get(id)!=null;
    }

    public boolean isHold(Channel channel){
        return this.isHold(channel.id().asShortText());
    }

    public void unHold(Channel channel){
        this.unHold(channel.id().asShortText());
    }
    public void unHold(String id){
        if (log.isDebugEnabled()) {
            log.debug("客户端离线或长时间未相应取消托管，从托管列表移除客户端----- {}" ,id);
        }
        if (this.clientHolder.containsKey(id)) {
            final Channel channel = this.clientHolder.get(id).getChannel();
            this.clientHolder.remove(id);
            CompletableFuture.runAsync(()->{
                try{
                    channel.close();
                }catch (Exception e){}
            });
        }else{
            if (log.isDebugEnabled()) {
                log.debug("未找到托管的客户端{}，放弃移除",id);
            }
        }
    }


    /**
     * 获取客户端
     * @param id
     * @return
     */
    public TcpClient get(String id){
        return this.clientHolder.get(id);
    }

    public TcpClient get(Channel channel){
        return this.get(channel.id().asShortText());
    }

    public int count(){
        return this.clientHolder.size();
    }

    public List<TcpClient> list(final int start, final int limit){
        final List<TcpClient> list = new ArrayList<>();
        final int finalLimit = limit >0?limit:Integer.MAX_VALUE;
        final int finalStart = start>0? start:0 ;
        AtomicInteger integer =new AtomicInteger(0);
        this.clientHolder.forEach((k,v)->{
            int index = integer.incrementAndGet();
            if(index>=finalStart && index - finalLimit < finalLimit){
                list.add(v);
            }
        });
        return list;
    }


}
