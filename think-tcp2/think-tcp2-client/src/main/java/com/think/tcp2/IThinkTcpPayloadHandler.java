package com.think.tcp2;

import com.think.core.annotations.Remark;
import com.think.tcp2.common.model.TcpPayload;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/23 23:19
 * @description : TODO
 */
public interface IThinkTcpPayloadHandler {

    /**
     * 处理收到的消息
     * @param payload
     */
    @Remark("处理收到来自TCP服务的消息")
    void acceptMessage(TcpPayload payload);
}
