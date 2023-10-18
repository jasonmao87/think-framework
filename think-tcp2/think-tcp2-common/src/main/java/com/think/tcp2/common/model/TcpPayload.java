package com.think.tcp2.common.model;

import com.think.common.util.ThinkMilliSecond;
import com.think.core.bean.util.ObjectUtil;
import com.think.exception.ThinkException;
import com.think.tcp2.core.listener.PayloadListenerManager;
import com.think.tcp2.core.listener.TcpPayloadEventListener;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 15:56
 * @description : TODO
 */
@Slf4j
public class TcpPayload implements Serializable {
    private static final long serialVersionUID = -2773780527532750730L;

    /**
     * 传输内容
     */
    private byte[] data ;


    private String dataType ;

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

    /**
     * 传递时候得扩展传递信息
     */
    private String extra;


    public TcpPayload(Serializable data) {
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.dataType = data.getClass().getName();
        this.data = ObjectUtil.serializeObject(data);
        List<TcpPayloadEventListener> executeIterator = PayloadListenerManager.getListeners();
        for (TcpPayloadEventListener listener : executeIterator) {
            try {
                listener.onInit(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public TcpPayload retry(){
        final List<TcpPayloadEventListener> listeners = PayloadListenerManager.getListeners();
        for (TcpPayloadEventListener listener : listeners) {
            try {
                listener.onRetry(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.tryCount ++ ;
        return this;
    }


    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Serializable getData() throws ClassNotFoundException, ThinkException {
        try {
            if(data==null){
                throw new ThinkException("传递的可序列化内容是NULL！");
            }
            return (Serializable) ObjectUtil.deserialization(data, dataType());
        }catch (ClassNotFoundException e){
            log.error("我们无法找到这个CLASS" + dataType());
            throw e;
        }catch (Exception e){
            log.error("无法将传递的内容[data length = {} ]序列化为：{} ，请确保传递的内容是可序列化的，且是正常的: ",data!=null?data.length+"":"NULL" ,dataType() );
            log.error("无法解析Payload内传递对象 : " ,e );
            e.printStackTrace();
            throw e;
        }
    }

    public Class dataType() throws ClassNotFoundException,ThinkException{
        if(this.dataType!=null) {
            return Class.forName(this.dataType);
        }
        throw new ThinkException("尚未被正确的初始化构造");
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

    public String getDataTypeName(){
        return this.dataType;
    }

    public void setSession(String session) {
        this.session = session;
    }




    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public String toString() {
        return "TcpPayload{" +
                "data=" + Arrays.toString(data) +
                ", dataType='" + dataType + '\'' +
                ", initTime=" + initTime +
                ", tryCount=" + tryCount +
                ", clientId='" + clientId + '\'' +
                ", session='" + session + '\'' +
                ", extendedData='" + extra + '\'' +
                '}';
    }
}
