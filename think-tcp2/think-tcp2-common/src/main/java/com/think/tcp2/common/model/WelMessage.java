package com.think.tcp2.common.model;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/15 19:47
 * @description : TODO
 */
public class WelMessage implements Serializable {

    private String message ;
    public WelMessage() {
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
