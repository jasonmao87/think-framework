package com.think.exception;

import com.think.common.util.StringUtil;

/**
 * @Date :2021/9/26
 * @Name :ThinkDataVerificationException
 * @Description : 数据 验证异常
 */
public class ThinkDataVerificationException  extends ThinkRuntimeException{

    public ThinkDataVerificationException(String message) {
            super(StringUtil.isEmpty(message)?"数据对象模型不合法":message);
    }
}
