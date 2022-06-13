package com.think.tcp2.client;

import com.think.tcp2.IThinkTcpConsumer;
import com.think.tcp2.common.ThinkTcpConfig;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.listener.DefaultTcpEventListener;
import com.think.tcp2.listener.ThinkTcpEventListener;
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

import java.io.Serializable;
import java.util.Scanner;
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

    private static Tcp2Client instance ;
    private int port;

    private String serverAddr ;

    private Bootstrap bootstrap = null;

    private EventLoopGroup worker = null;


    private IThinkTcpConsumer consumer;

    private ThinkTcpEventListener listener;

    private ThinkTcpEventListener defaultListener  = new DefaultTcpEventListener();


    private Tcp2Client() {
    }
    public static final Tcp2Client getInstance(){
        if(instance == null){
            instance = new Tcp2Client();
        }
        return instance;
    }
    private Channel channel;

    public void reConnect(){
        if(this.channel !=null) {
            try {
                connect(serverAddr, port,null);
            } catch (Exception e) {
            }
        }
    }
    public final void connect(String serverAddr,int port ,IThinkTcpConsumer consumer) throws InterruptedException {
        if(consumer!=null ){
            this.consumer = consumer;
        }
        if(this.consumer == null){
            throw new InterruptedException("未设置消息处理的Consumer");
        }
        this.port = port;
        this.serverAddr = serverAddr;
        bootstrap= new Bootstrap();
        worker =new NioEventLoopGroup();
        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                ch.pipeline().addLast((new IdleStateHandler(0, ThinkTcpConfig.getIdleActiveSequenceTimeSeconds(), 0, TimeUnit.SECONDS)));
                ch.pipeline().addLast(new TcpClientHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(serverAddr, port).sync();

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

    public boolean sendPayLoad(TcpPayload payload) throws InterruptedException{
        this.channel.writeAndFlush(payload);
        return true;
    }


    public void setConsumer(IThinkTcpConsumer consumer) {
        this.consumer = consumer;
    }

    public void setListener(ThinkTcpEventListener listener) {
        this.listener = listener;
    }

    public IThinkTcpConsumer getConsumer() {
        return consumer;
    }

    public ThinkTcpEventListener getListener() {
        return listener!=null?listener:defaultListener;
    }


    public static void main(String[] args) throws InterruptedException {
        Tcp2Client.getInstance().setListener(new DefaultTcpEventListener());
        Tcp2Client.getInstance().connect("127.0.0.1", 5740, new IThinkTcpConsumer() {
            @Override
            public void acceptMessage(TcpPayload payload) {
                System.out.println(payload.getData());
            }
        });
        Scanner scanner= new Scanner(System.in);
        while (scanner.hasNext()){
            String text = scanner.nextLine();


            getInstance().sendMessage(text);
            System.out.println("SEND ===");



        }

    }
}
