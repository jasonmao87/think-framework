package com.think.tcp2.common;

import com.think.core.annotations.Remark;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/5 9:26
 * @description :  tcp 客户端 连接 信息
 */
public class ThinkTcpClientConnection {

    private String channelId ;

    private String name ;

    @Remark("初始化时间")
    private long initTime ;

    @Remark("活跃状态")
    private boolean active ;

    @Remark("最后活跃时间")
    private long lastActiveTime ;

    private long lastMessageTime ;


}
