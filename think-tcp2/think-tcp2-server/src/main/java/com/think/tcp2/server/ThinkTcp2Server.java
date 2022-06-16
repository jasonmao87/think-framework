package com.think.tcp2.server;

import com.think.tcp2.common.ThinkTcpConfig;
import com.think.tcp2.server.handler.TcpPayloadHandler;
import com.think.tcp2.server.handler.ThinkDefaultServerHandler;
import com.think.tcp2.server.handler.ThinkHeartbeatHandler;
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
import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 13:28
 * @description : TODO
 */
@Slf4j
public class ThinkTcp2Server {
    private static ThinkTcp2Server serverInstance;

    /**
     * 默认超时 30 s
     */
    public long idleTimeout = 350000;

    /**
     * 默认端口号
     */
    private int port = ThinkTcpConfig.getPort();

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

    public ThinkTcp2Server(int port) {
        this.port = port;
    }

    public ThinkTcp2Server() {}


    public void start() throws InterruptedException{
        start(port);
    }
    public void start(int port ) throws InterruptedException{
        this.port = port;
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ThinkDefaultServerHandler defaultServerHandler = new ThinkDefaultServerHandler();
                ThinkHeartbeatHandler heartBeatHandler = new ThinkHeartbeatHandler();
                TcpPayloadHandler payloadHandler = new TcpPayloadHandler();
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(ThinkTcpConfig.getIdleTimeoutSeconds(),0,0, TimeUnit.SECONDS))
                        .addLast(new ObjectEncoder())
                        .addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                        .addLast(heartBeatHandler)
                        .addLast(payloadHandler)
                        .addLast(defaultServerHandler);
            }
        });
        serverBootstrap.option(ChannelOption.SO_BACKLOG,1024);
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        if(channelFuture.isSuccess()){
            log.info("TCP SERVER 启动 ");
            this.startState =true;
        }

        //非阻塞 。但是这样会永远吃掉一个线程
        CompletableFuture.runAsync(() -> {
            try {
                ChannelFuture closeFuture = channelFuture.channel().closeFuture().sync();
                this.startState =false;
                if (closeFuture.isCancelled()) {
                    if (log.isInfoEnabled()) {
                        log.info("Close Future is called");
                    }
                }
            } catch (Exception e) {}
        });


    }

    /**
     * 设置超时时间，即心跳包 最大间隔
     * @param idleTimeout
     */
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }




    public static void main(String[] args) throws InterruptedException {
        ThinkTcp2Server s =new ThinkTcp2Server();
        s.start(5740);

    }
}
