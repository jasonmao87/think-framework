package com.think.tcp2.common.model.message;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/7 15:05
 * @description : 该消息 由 客户端 收到 WEL MESSAGE 后，来注册 通信权限，
 *  如果 未通过注册 ，服务器不会响应除了开发消息类型以为的 任何 消息 ！
 */
public class AuthRequestMessage implements Serializable {
    private static final long serialVersionUID = -1497909856481658441L;

    private String authKey;


    private String clientAppName;

    public AuthRequestMessage() {
    }

    public AuthRequestMessage(String authKey,String name) {
        this.authKey = authKey;
        this.clientAppName = name;
    }

    public void setClientAppName(String clientAppName) {
        this.clientAppName = clientAppName;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthKey() {
        return authKey;
    }

    public String getClientAppName() {
        return clientAppName;
    }
}
