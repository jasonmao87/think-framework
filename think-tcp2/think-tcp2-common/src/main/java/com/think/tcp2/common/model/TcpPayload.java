package com.think.tcp2.common.model;

import com.think.common.util.ThinkMilliSecond;
import com.think.core.annotations.Remark;
import com.think.tcp2.core.listener.PayloadListenerManager;
import com.think.tcp2.core.listener.TcpPayloadEventListener;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 15:56
 * @description : TODO
 */
@Slf4j
public class TcpPayload<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -2773780527532750730L;

    /**
     * 传输内容
     */
    private T data ;

    /**
     * 构建时间
     */
    private long initTime ;


    /**
     * 重试次数
     */
    private int tryCount = 0 ;

    /**
     * 发送 或者 接收消息的 clientId
     */
    private String clientId ;

    private String session;

    public TcpPayload(T data) {
        this.data = data;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        Iterator<TcpPayloadEventListener> executeIterator = PayloadListenerManager.getExecuteIterator();
        while (executeIterator.hasNext()) {
            try{
                executeIterator.next().onInit(this);
            }catch (Exception e){
                log.error("执行TcpPayloadListener出现的异常 " ,e );
            }
        }


    }

    public TcpPayload retry(){
        Iterator<TcpPayloadEventListener> executeIterator = PayloadListenerManager.getExecuteIterator();
        while (executeIterator.hasNext()) {
            try{
                executeIterator.next().onRetry(this);
            }catch (Exception e){
                log.error("执行TcpPayloadListener出现的异常 " ,e );
            }
        }
        this.tryCount ++ ;
        return this;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public T getData() {
        return data;
    }

    public long getInitTime() {
        return initTime;
    }

    public int getTryCount() {
        return tryCount;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
