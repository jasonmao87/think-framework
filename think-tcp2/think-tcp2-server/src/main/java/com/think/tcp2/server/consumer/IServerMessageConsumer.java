package com.think.tcp2.server.consumer;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/8 23:46
 * @description : TODO
 */
public interface IServerMessageConsumer<T extends Serializable> {

    /**
     * 处理payload内 对象
     * @param data
     */
    void consume(T data);

}
