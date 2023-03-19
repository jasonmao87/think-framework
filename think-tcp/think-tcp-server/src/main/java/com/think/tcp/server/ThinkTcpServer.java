package com.think.tcp.server;


import com.think.tcp.consumer.IMessageConsumer;
import com.think.tcp.listener.ITcpListener;
import com.think.tcp.server.handler.ThinkHeartBeatHandler;
import com.think.tcp.server.handler.ThinkTcpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 注意，这边是阻塞的 ，不要在主线程上启动他 ！
 */
@Slf4j
public class ThinkTcpServer  {
    private static ThinkTcpServer instance ;

    private static IMessageConsumer consumer;
    private static ITcpListener tcpListener;

    /*端口号，默认 7285*/
    private int port = 7285;

    /*server 启动状态*/
    private boolean startState = false;

    /*启动时间戳*/
    private long startTime ;

    /*处理业务线程的线程组个数*/
    protected final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;//默认

    /*业务出现线程大小*/
    protected final int BIZTHREADSIZE=4;

    /*
     * NioEventLoopGroup实际上就是个线程池,
     * NioEventLoopGroup在后台启动了n个NioEventLoop来处理Channel事件,
     * 每一个NioEventLoop负责处理m个Channel,
     * NioEventLoopGroup从NioEventLoop数组里挨个取出NioEventLoop来处理Channel
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);
    private ServerBootstrap serverBootstrap;

    public synchronized static ThinkTcpServer getInstance(){
        if(instance == null){
            instance = new ThinkTcpServer();
        }
        return instance;
    }
    private ThinkTcpServer(){
    }

    /**
     * 绑定消息处理器
     * @param consumer
     */
    public void bindConsumer(IMessageConsumer consumer){
        ThinkTcpServer.consumer = consumer;
    }

    public void addListener(ITcpListener listener){
        ThinkTcpServer.tcpListener = listener;
    }

    public synchronized boolean isStart(){
        return startState;
    }

    private synchronized void noticeStop(){
        startState = false;
    }

    private synchronized void noticeStart(){
        startState = true;
    }




    /**
     * 非阻塞的
     * @param port
     * @throws InterruptedException
     */
    public void startServer(int port ) throws InterruptedException {
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ThinkTcpHandler handler = new ThinkTcpHandler();
                ThinkHeartBeatHandler heartBeatHandler = new ThinkHeartBeatHandler();
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(5,0,0, TimeUnit.MINUTES))
                        .addLast(new ObjectEncoder())
                        .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                        .addLast(handler)
                        .addLast(heartBeatHandler);
            }
        });
        serverBootstrap.option(ChannelOption.SO_BACKLOG,1024);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        if(channelFuture.isSuccess()){
            log.info("TCP SERVER 启动 ");
        }

        //非阻塞 。但是这样会永远吃掉一个线程
        CompletableFuture.runAsync(() -> {
            try {
                ChannelFuture closeFuture = channelFuture.channel().closeFuture().sync();
                if (closeFuture.isCancelled()) {
                    if (log.isInfoEnabled()) {
                        log.info("Close Future is called");
                    }
                }
            } catch (Exception e) {

            }
        }, Executors.newSingleThreadExecutor());


    }




    public static IMessageConsumer getConsumer() {
        return consumer;
    }

    public static ITcpListener getTcpListener() {
        return tcpListener;
    }



    public static void main(String[] args) {
        try{
            ThinkTcpServer.getInstance().startServer(8888);
        }catch (Exception e){

        }
    }
}
