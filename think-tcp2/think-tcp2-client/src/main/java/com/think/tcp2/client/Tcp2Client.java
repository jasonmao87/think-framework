package com.think.tcp2.client;

import com.think.common.util.rt.ThinkMachineUtil;
import com.think.tcp2.IThinkTcpPayloadHandler;
import com.think.tcp2.common.ThinkTcpConfig;
import com.think.tcp2.common.model.TcpPayload;
import com.think.tcp2.core.listener.PayloadListenerManager;
import com.think.tcp2.core.listener.TcpPayloadEventListener;
import com.think.tcp2.listener.DefaultTcpEventListener;
import com.think.tcp2.listener.ThinkTcpClientEventListener;
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
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.*;
import java.util.Enumeration;
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

    private static Tcp2Client instance ;
    private int port;

    private String serverAddr ;

    private Bootstrap bootstrap = null;

    private EventLoopGroup worker = null;


    private IThinkTcpPayloadHandler consumer;

    private ThinkTcpClientEventListener listener;

    private ThinkTcpClientEventListener defaultListener  = new DefaultTcpEventListener();

    protected boolean connected = false;


    private String localIpaddr = "";


    public boolean isConnected() {
        return connected;
    }

    private Tcp2Client() {
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
    public final void connect(String serverAddr, int port , IThinkTcpPayloadHandler consumer) throws InterruptedException {


        if(consumer!=null ){
            this.consumer = consumer;
        }
        if(this.consumer == null){
            throw new InterruptedException("未设置消息处理的Consumer");
        }
        this.port = port;
        this.serverAddr = serverAddr;
        bootstrap= new Bootstrap();

//        bootstrap.bind();


        worker =new NioEventLoopGroup();
        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                ch.pipeline().addLast((new IdleStateHandler(0, ThinkTcpConfig.getIdleActiveSequenceTimeSeconds(), 0, TimeUnit.SECONDS)));
                ch.pipeline().addLast(new TcpWelMessageHandler());
                ch.pipeline().addLast(new TcpClientHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(serverAddr, port).sync();
        log.info("正在建立到{} : {} 的连接" ,serverAddr ,port);
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
//        Iterator<TcpPayloadEventListener> executeIterator = PayloadListenerManager.getExecuteIterator();
        final List<TcpPayloadEventListener> listeners = PayloadListenerManager.getListeners();
        for (TcpPayloadEventListener eventListener : listeners) {
            try {
                eventListener.beforeSend(payload);
            }catch (Exception e){}
        }
        if(channel == null){
            log.error("暂未与服务端[{}:{}]建立连接，无法发送相关信息，丢弃信息" ,this.serverAddr,this.port);
//            throw new InterruptedException("暂未何服务端建立连接");
            throw new InterruptedException("暂未与服务端建立连接");
        }
        this.channel.writeAndFlush(payload);
        for (TcpPayloadEventListener tcpPayloadEventListener : listeners) {
            try {
                tcpPayloadEventListener.afterSend(payload);
            }catch (Exception e){
                log.error("执行TcpPayloadListener[afterSend]出现的异常（该异常发送在消息发送后，不会影响正常程序）" ,e );
            }
        }
        return true;
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

    protected void setId(String id) {
        log.info("当前客户端Id = {}" ,id  );
        this.id = id;
    }



    //    public static void main(String[] args) throws InterruptedException {
//        Tcp2Client.getInstance().setListener(new DefaultTcpEventListener());
//        Tcp2Client.getInstance().connect("127.0.0.1", 5740, new IThinkTcpConsumer() {
//            @Override
//            public void acceptMessage(TcpPayload payload) {
////                System.out.println(payload.getData());
//            }
//        });
//        Scanner scanner= new Scanner(System.in);
//        while (scanner.hasNext()){
//            String text = scanner.nextLine();
//
//
//            getInstance().sendMessage(text);
//            System.out.println("SEND ===");
//
//
//
//        }
//
//    }


    public static void main(String[] args) throws UnknownHostException, SocketException {


        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            System.out.println(networkInterface);
            final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                System.out.println(" >> "+inetAddresses.nextElement());
            }
        }

    }

}
