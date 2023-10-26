package com.think.tcp2.client;

import com.think.common.util.StringUtil;
import com.think.common.util.security.MD5Util;
import com.think.core.annotations.Remark;
import com.think.core.security.sm.SM3Utils;
import com.think.tcp2.IThinkTcpPayloadHandler;
import com.think.tcp2.common.ThinkTcpConfig;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.core.listener.PayloadListenerManager;
import com.think.tcp2.core.listener.TcpPayloadEventListener;
import com.think.tcp2.listener.DefaultTcpEventListener;
import com.think.tcp2.listener.ThinkTcpClientEventListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.provider.digest.SM3;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 19:52
 * @description : TODO
 */
@Slf4j
public class Tcp2Client {

    private String id ;

    @Remark("客户端控制id")
    private final String authKey;

    @Remark("客户端的应用名称")
    private String applicationName;


    private static Tcp2Client instance ;
    private int port;

    private String serverAddr ;

    private Bootstrap bootstrap = null;

    private EventLoopGroup worker = null;

    private IThinkTcpPayloadHandler consumer;

    private ThinkTcpClientEventListener listener;

    private ThinkTcpClientEventListener defaultListener  = new DefaultTcpEventListener();

    protected boolean connected = false;

    private boolean deny =true;


    private String localIpaddr = "";


    public String getAuthKey() {
        return authKey;
    }

    public boolean isDeny() {
        return deny && isConnected();
    }

    public void setDeny(boolean deny) {
        this.deny = deny;
    }

    public boolean isConnected() {
        if (this.channel!=null) {
            if (this.channel.isRegistered()) {
                return true;
            }
        }
        return false;
//        return connected;
    }


    private Tcp2Client() {
//        this.authKey =   MD5Util.encryptMd5(StringUtil.uuid() +"-millis-"+  System.currentTimeMillis() + "nano-"+ System.nanoTime());
        this.authKey = SM3Utils.sm3(StringUtil.uuid() +"-millis-"+  System.currentTimeMillis() + "nano-"+ System.nanoTime());
    }
    public static final Tcp2Client getInstance(){
        if(instance == null){
            instance = new Tcp2Client();
        }
        return instance;
    }
    private Channel channel;


    public void setLocalIpaddr(String localIpaddr) {
        if (NetUtil.isValidIpV4Address(localIpaddr)) {
            this.localIpaddr = localIpaddr;
        }
        if(channel!=null && channel.isActive()){
            throw new RuntimeException("无效绑定本机IP，请在Connect之前绑定！");
        }
    }

    public void reConnect(){
        if(this.channel !=null) {
            try {
                connect(serverAddr, port,null);
            } catch (Exception e) {
            }
        }
    }
    public final void connect(String tcpServerHostAddr, int port , IThinkTcpPayloadHandler consumer) throws InterruptedException {


        if(consumer!=null ){
            this.consumer = consumer;
        }
        if(this.consumer == null){
            throw new InterruptedException("未设置消息处理的Consumer");
        }
        this.port = port;
        this.serverAddr = tcpServerHostAddr;
        bootstrap= new Bootstrap();


        worker =new NioEventLoopGroup();
        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ObjectEncoder());
                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                pipeline.addLast((new IdleStateHandler(0, ThinkTcpConfig.getIdleActiveSequenceTimeSeconds(), 0, TimeUnit.SECONDS)));
                pipeline.addLast(new TcpWelMessageHandler());
                pipeline.addLast(new TcpAuthResponseHandler());
                pipeline.addLast(new TcpClientHandler());
            }
        });

        if(StringUtil.isNotEmpty(localIpaddr)){
//            bootstrap.remoteAddress()
        }

//        bootstrap.remoteAddress()
        ChannelFuture channelFuture = bootstrap.connect(tcpServerHostAddr, port).sync();
        log.info("正在建立到{} : {} 的连接" ,tcpServerHostAddr ,port);
        this.channel = channelFuture.channel();



        CompletableFuture.runAsync(()->{
            try{
                channel.closeFuture().sync();
                log.info("channel closed ");
            }catch (Exception e){

            }finally {
                log.info("work shut down ....");
                worker.shutdownGracefully();
                bootstrap = null;
            }
        });

    }


    /**
     * 发送 消息
     * @param message
     * @return
     * @throws InterruptedException
     */
    public <T extends Serializable>  boolean sendMessage(T message) throws InterruptedException {
        TcpPayload payload = new TcpPayload(message);
        return sendPayLoad(payload);
    }

    public boolean sendPayLoad(TcpPayload payload) throws InterruptedException {
        try {
            if (isDeny()) {
                log.warn("当前TCP CLIENT 通信运维受限，通信的内容可能不被服务器处理和消费");
            }

            final List<TcpPayloadEventListener> listeners = PayloadListenerManager.getListeners();
            for (TcpPayloadEventListener eventListener : listeners) {
                try {
                    eventListener.beforeSend(payload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (channel == null) {
                if (log.isErrorEnabled()) {
                    log.error("暂未与服务端[{}:{}]建立连接，无法发送相关信息，丢弃信息", this.serverAddr, this.port);
                }
                throw new InterruptedException("暂未与服务端建立连接");
            }
            ChannelFuture channelFuture = this.channel.writeAndFlush(payload);
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    for (TcpPayloadEventListener tcpPayloadEventListener : listeners) {
                        try {
                            tcpPayloadEventListener.afterSend(payload);
                        } catch (Exception e) {
                            log.error("执行TcpPayloadListener[afterSend]出现的异常（该异常发送在消息发送后，不会影响正常程序）", e);
                        }
                    }
                } else {
                }
            });


            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public void setConsumer(IThinkTcpPayloadHandler consumer) {
        this.consumer = consumer;
    }

    public void setListener(ThinkTcpClientEventListener listener) {
        this.listener = listener;
    }

    public IThinkTcpPayloadHandler getConsumer() {
        return consumer;
    }

    public ThinkTcpClientEventListener getListener() {
        return listener!=null?listener:defaultListener;
    }

    public String getId() {
        return id;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    protected void setId(String id) {
        if (log.isDebugEnabled()) {
            log.debug("当前客户端Id = {}" ,id  );
        }
        this.id = id;
    }


}
