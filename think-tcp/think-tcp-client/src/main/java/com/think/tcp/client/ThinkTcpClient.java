package com.think.tcp.client;

import com.think.tcp.TMessage;
import com.think.tcp.TcpTransModel;
import com.think.tcp.client.handler.ThinkTcpClientHandler;
import com.think.tcp.consumer.IMessageConsumer;
import com.think.tcp.listener.ITcpListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 单例
 */
@Slf4j
public class ThinkTcpClient {
    private static ThinkTcpClient instance = null;
    private int port = 7285;
    private String ip = "127.0.0.1";

    private Bootstrap bootstrap = null;
    private EventLoopGroup worker = null;

    private static IMessageConsumer consumer = null;
    private static ITcpListener tcpListener =null;
    private static IThinkTcpClientIdleListener idleListener = null;

    private ThinkTcpClient(){
    }
    public synchronized static ThinkTcpClient getInstance(){
        if(instance == null){
            instance = new ThinkTcpClient();
        }
        return instance;
    }
    private Channel channel;


    public boolean isInit(){
        return channel!=null;
    }
    public boolean isActive(){
        if(channel==null){
            return false;
        }
        //channel.isRegistered();
        // channel.isOpen();
        return channel.isActive();

    }

    private Executor executor = Executors.newSingleThreadExecutor();

    public final void close(){
        try{
           channel.disconnect();
        }catch (Exception e){}
        try{
            channel.close();
        }catch (Exception e){}
    }

    public final void reConnect() throws InterruptedException {
       this.connect(ip,port);
    }




    public final void connect(String ip,int port) throws InterruptedException {
        this.ip = ip;
        this.port = port;
        bootstrap = new Bootstrap();
        worker = new NioEventLoopGroup();
        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                //服务器端 8分钟算超时，这边 4分钟发送一次心跳包
                ch.pipeline().addLast((new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS)));
                ch.pipeline().addLast(new ThinkTcpClientHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();

        channel = channelFuture.channel();
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
        }, executor);
    }

    public void send(TMessage message) {
        TcpTransModel model = new TcpTransModel(message,channel);
        channel.writeAndFlush(model);
    }

    public boolean sendSync(TMessage message){
        TcpTransModel model = new TcpTransModel(message,channel);
        try{
            return channel.writeAndFlush(model).sync().isSuccess();
        }catch (Exception e){
            return false;
        }
    }

    public void bindMessageConsumer(IMessageConsumer iMessageConsumer){
        consumer = iMessageConsumer;
    }

    public void addListener(ITcpListener listener){
        tcpListener = listener;
    }

    public IMessageConsumer getConsumer(){
        return consumer;
    }

    public static ITcpListener getTcpListener() {
        return tcpListener;
    }


    public static void addClientIdleListener(IThinkTcpClientIdleListener idleListener){
        ThinkTcpClient.idleListener = idleListener;

    }
    public static IThinkTcpClientIdleListener getIdleListener(){
        return idleListener;
    }


}
