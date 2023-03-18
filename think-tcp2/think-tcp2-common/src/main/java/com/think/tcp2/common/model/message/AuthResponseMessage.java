package com.think.tcp2.common.model.message;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/7 17:15
 * @description :   此消息用 由 服务器端 发送给客户端，
 *  用来通知： 客户端被限制
 *           客户端被解除限制
 */
public class AuthResponseMessage implements Serializable {
    private static final long serialVersionUID = -142158729947416930L;

    private boolean denyState = false;

    private String message ;

    public AuthResponseMessage(boolean denyState) {
        this.denyState = denyState;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isDenyState() {
        return denyState;
    }
}
