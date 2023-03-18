package com.think.tcp.server.manager;

import com.think.common.util.ThinkMilliSecond;
import com.think.common.util.TimeUtil;
import com.think.exception.ThinkRuntimeException;
import com.think.tcp.TMessage;
import com.think.tcp.TcpTransModel;
import com.think.tcp.listener.ITcpListener;
import com.think.tcp.server.ThinkTcpServer;
import com.think.tcp.server.exception.ThinkTcpTransferException;
import io.netty.channel.Channel;

import java.lang.ref.WeakReference;

/**
 * 客户端影子模型 ----
 */
public class ThinkTcpClientShadowModel {

//    private String id = null;

    private String channelId;

    private long initTime =0L;

    private long lastActiveTime = 0L;

    /**
     * health == false 的 client 会被下次检查时候回收 移除
     */
    private boolean health = false;

    private WeakReference<Channel> holderChannel;


    protected ThinkTcpClientShadowModel(Channel channel){
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.lastActiveTime = initTime;
        this.channelId = channel.id().toString();
        this.health = true;
        this.holderChannel = new WeakReference<>(channel);
    }

//    public void bindId(String id) throws ThinkRuntimeException{
//        ThinkTcpClientManager.bindId(id,this.channelId);
//    }

    public boolean isExpire(){
        long now =ThinkMilliSecond.currentTimeMillis();
        /*
            超过三十秒视为 超时
         */
        if(now - lastActiveTime > TimeUtil.MILLIS_OF_30_SECONDS){
            return true;
        }
        return false;
    }

    public void active(){
        this.health = true;
        this.lastActiveTime =ThinkMilliSecond.currentTimeMillis();
    }

    /**
     * 超时 1次
     */
    public void idle(){
        this.health = false;
    }

    /**
     * 是否健康，或者 是否超时了1次 ！
     * @return
     */
    public boolean isHealth() {
        return health;
    }

    /**
     * 发送消息给 客户端
     * @param messageModal
     */
    public void transmission(TMessage messageModal) throws ThinkRuntimeException,ThinkTcpTransferException{
        if(this.health){
            transmission(new TcpTransModel(messageModal,getChannel()),1);
        }else{
            throw new ThinkTcpTransferException("客户端当前健康度存在问题，放弃通信");
        }
    }

    private Channel getChannel()  {
        Channel channel = this.holderChannel.get();
//        if(channel == null){
//            throw new ThinkRuntimeException("当前连接已经被回收，请重新建立连接后再试");
//        }
        return channel;
    }


    /**
     * 关闭 并且不携带任何消息
     */
    public void closeWithoutNotice(){
        if(getChannel() == null){

        }else {
            try{
                getChannel().close();
            }catch (Exception e){}finally {
                this.holderChannel.clear();
            }
        }

    }

    /**
     * (唯一消息传递接口 )传输消息
     * @param model
     * @param count
     */
    protected void transmission(TcpTransModel model , final int count){
        ITcpListener listener = ThinkTcpServer.getTcpListener();
        if(count + 1 > 3){
            //丢弃消息
            if(listener !=null){
                listener.onFail(model.getMessage());
            }
            return;
        }
        if(count > 1){
            model.fireRetry();
        }
        if(getChannel()!=null){
            getChannel().writeAndFlush(model).addListener(w->{
               if(!w.isSuccess()){
                   transmission(model,count +1 );
               }else {
                   if(listener !=null){
                       listener.onSuccess(model.getMessage());
                   }

               }
            });
        }

    }

    public String getChannelId() {
        return channelId;
    }

    public long getInitTime() {
        return initTime;
    }

    public long getLastActiveTime() {
        return lastActiveTime;
    }

    public WeakReference<Channel> getHolderChannel() {
        return holderChannel;
    }
}
