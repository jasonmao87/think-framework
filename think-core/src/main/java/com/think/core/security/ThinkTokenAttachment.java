package com.think.core.security;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2021/12/15 22:45
 * @description :  token 附属信息类
 */
public interface ThinkTokenAttachment extends Serializable{
    /**
     * 获得附属对象
     * @return
     */
    <T extends Serializable> T getAttachment();

    /**
     * 设置 附属对象
     * @param o
     */
    <T extends Serializable>  void setAttachment(T o);
}
