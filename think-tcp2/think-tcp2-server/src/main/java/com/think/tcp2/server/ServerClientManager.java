package com.think.tcp2.server;

import com.think.common.util.TimeUtil;
import com.think.core.annotations.Remark;
import com.think.core.executor.ThinkAsyncExecutor;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.server.listener.IThinkTcpClientTrigger;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 16:07
 * @description : TODO
 */
@Slf4j
public class ServerClientManager {

    private static ServerClientManager instance ;
    private final Map<String, TcpServerClient> clientHolder ;


    public final Set<String> authDenySet;


    private ServerClientManager() {
        authDenySet = new HashSet<>();
        clientHolder = new ConcurrentHashMap<>();
        ///启动自动巡检
        this._autoCheck();
    }

    private IThinkTcpClientTrigger tcpClientTrigger;


    public void setTcpClientTrigger(IThinkTcpClientTrigger tcpClientTrigger) {
        this.tcpClientTrigger = tcpClientTrigger;
    }


    public IThinkTcpClientTrigger getTcpClientTrigger() {
        if(tcpClientTrigger == null){
            log.info(" TCP SERVER 未指定客户端事件监听器[IThinkTcpClientTrigger]，默认构造DoNothing实现");
            log.info("锁定对象实例资源，以防止线程安全问题");
            synchronized (this) {
                tcpClientTrigger = new IThinkTcpClientTrigger() {
                    @Override
                    public void beforeUnHold(String clientId) {}

                    @Override
                    public void onHold(String clientId) {}
                    @Override
                    public void onUnHold(String clientId) {}

                    @Override
                    public void onMessageSuccess(String clientId, TcpPayload payload) {}

                    @Override
                    public void onMessageFail(String clientId, TcpPayload payload) {}
                };
            }
            log.info("释放对象实例资源");
            log.info("TCP SERVER 构建默认客户端事件监听器[IThinkTcpClientTrigger]完成。");
        }
        return tcpClientTrigger;
    }

    public static final ServerClientManager getInstance() {
        if(instance == null){
            return doInstance();
        }
        return instance;
    }

    private synchronized static ServerClientManager doInstance(){
        log.info("TCP SERVER 初始化客户端管理器");
        if(instance == null){
            instance = new ServerClientManager();
            log.info("TCP SERVER 构建客户端管理器完成");
        }
        return instance;
    }


    public void hold(Channel channel){
        this.hold(new TcpServerClient(channel));
    }

    private void hold(TcpServerClient client){
        if (log.isDebugEnabled()) {
            log.debug("注册新的客户端---- {}" ,client.getId());
        }
        clientHolder.put(client.getId(),client);
        getTcpClientTrigger().onHold(client.getId());
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
        try{
            getTcpClientTrigger().beforeUnHold(id);
        }catch (Exception e){
            e.printStackTrace();
        }
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
            getTcpClientTrigger().onUnHold(id);

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
    public TcpServerClient get(String id){
        return this.clientHolder.get(id);
    }

    public TcpServerClient get(Channel channel){
        return this.get(channel.id().asShortText());
    }

    public int count(){
        return this.clientHolder.size();
    }

    public List<String> holdClientIds(Predicate<String> predicate){
        List<String> idList =new ArrayList<>();
        final Iterator<String> iterator = this.clientHolder.keySet().iterator();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            if (predicate.test(next)) {
                idList.add(next);
            }
        }
        return idList;
    }

    public List<TcpServerClient> fullClientList(){
        List<TcpServerClient> list = new ArrayList<>();
        if (clientHolder!=null && !clientHolder.isEmpty()) {
            this.clientHolder.forEach((k,v)->{
                list.add(v);
            });
            if(list.size() > 1) {
                list.sort((a, b) -> {
                    return b.getAppName().compareTo(a.getAppName());
                });
            }
        }

        return list;

    }


    public List<TcpServerClient> list(final int start, final int limit){
        final List<TcpServerClient> list = new ArrayList<>();
        final int finalLimit = limit >0?limit:Integer.MAX_VALUE;
        final int finalStart = start>0? start:0 ;
        AtomicInteger integer =new AtomicInteger(0);
        this.fullClientList().forEach(t->{
            int index = integer.incrementAndGet();
            if(index>=finalStart && index - finalLimit < finalLimit){
                list.add(t);
            }
        });
        return list;
    }


    /**
     * 发送广播消息
     */
    @Remark("发送广播消息，异步的 ")
    public <T extends Serializable> void broadcastMessage(T message , Predicate<TcpServerClient> predicate){
        final CompletableFuture<Void> future = ThinkAsyncExecutor.execute(() -> {
            //异步多线程执行
            clientHolder.entrySet().parallelStream().forEach(t -> {
                if (t != null && predicate.test(t.getValue())) {
                    t.getValue().sendMessage(message);
                }
            });
        });

    }


    public void printClients(){
        int index =0;
        for (String key : this.clientHolder.keySet()) {
            TcpServerClient tcpServerClient = get(key);
            index++;
            if(log.isInfoEnabled()) {
                log.info("{} : [{}]{}  --最后活跃：{} 是否受限 {} ", index, tcpServerClient.getId(), tcpServerClient.getSocketAddress(), tcpServerClient.getLastActiveTime() , tcpServerClient.isDeny());
            }else{
                System.out.println(index + ":" + tcpServerClient.getId() +" --最后活跃 ： "+ tcpServerClient.getLastActiveTime());
            }
        }
    }


    /**
     * 检查 客户端是否受限
     * @param authKey
     * @return
     */
    public boolean checkIsDenyByAuthKey(String authKey){
        return this.authDenySet.contains(authKey);
    }

    /**
     * 设置客户端受限
     * @param authKey
     * @param message
     */
    public void denyClientByAuthKey(String authKey,String message){
        for (String key : this.clientHolder.keySet()) {
            TcpServerClient tcpServerClient = get(key);
            if(tcpServerClient.getAuthKey().equals(authKey)){
                tcpServerClient.setDeny(true,message);
            }
            this.authDenySet.add(authKey);
        }
    }

    public void acknowledgeClientByAuthKey(String authKey){
        for (String key : this.clientHolder.keySet()) {
            TcpServerClient tcpServerClient = get(key);
            if(tcpServerClient.getAuthKey().equals(authKey)){
                tcpServerClient.setDeny(false,"");
            }
            this.authDenySet.remove(authKey);
        }
    }




    private void _autoCheck(){
        ThinkAsyncExecutor.execute(()->{
            while (true) {
                try {
                    for (String key : this.clientHolder.keySet()) {
                        TcpServerClient tcpServerClient = get(key);
                        boolean deny = tcpServerClient.isDeny();
                        if (this.authDenySet.contains(tcpServerClient.getAuthKey())) {
                            if(deny == false){
                                deny = true;
                            }
                        }else{
                            if(deny == true){
                                deny =false;
                            }
                        }
                        tcpServerClient.setDeny(deny, "巡检重复通知");
                    }
                    log.info("巡检结束");
                    TimeUtil.sleep(15, TimeUnit.MINUTES);
                }catch (Exception e){}
            }


        });




        }

}