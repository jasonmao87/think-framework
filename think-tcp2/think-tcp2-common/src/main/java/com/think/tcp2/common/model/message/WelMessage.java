package com.think.tcp2.common.model.message;

import java.io.Serializable;


/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/15 19:47
 * @description :  欢饮消息 WEL MESSAGE
 *  这个消息由 服务器端 发送给 客户端  / 用于表示欢饮
 *      同时， 通知 client - 可以执行接下来一系列的操作了！
 *
 *
 */
public class WelMessage implements Serializable {

    private String message ;
    private String clientId ;
    public WelMessage() {
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public WelMessage(String message) {
        this.message = message;
    }

    public static final WelMessage hello(){
        return new WelMessage("HELLO");
    }

    public static final WelMessage welcome(){
        return new WelMessage("WELCOME");
    }
    public String getMessage() {
        return message;
    }
}
