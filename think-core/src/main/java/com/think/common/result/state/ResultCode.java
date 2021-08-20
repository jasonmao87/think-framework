package com.think.common.result.state;

/**
 * 状态码枚举
 */
public enum ResultCode {
    /**
     * 授权 :未登录
     */
    AUTH_NO_LOGIN ,

    /**
     * 缺少授权必要信息
     */
    AUTH_MISSING,

    /**
     * 客户端时间异常
     */
    AUTH_TIME_ERROR ,

    /**
     * 授权不足，被拒绝操作
     */
    AUTH_FORBIDDEN,

    /**
     * 授权失败-验证授权失败
     */
    AUTH_FAIL,

    /**
     * 授权失败-授权未通过验证，签名错误
     */
    AUTH_ERROR ,

    /**
     * 授权过期
     */
    AUTH_EXPIRE ,


    /**
     * 请求参数错误
     */
    REQUEST_PARAM_ERROR ,


    /**
     * 请求资源不存在
     */
    REQUEST_NO_RESOURCE ,

    /**
     * 请求受限
     */
    REQUEST_LIMITED ,

    /**
     * 成功
     */
    SUCCESS ,


    /**
     * 异步处理结果
     */
    PROCESS_ASYNC,

    /**
     * 服务不被支持
     */
    SERVER_NOT_SUPPORT,

    /**
     * 服务错误
     */
    SERVER_ERROR ,

    /**
     * 服务被拒绝
     */
    SERVER_FORBIDDEN ,

    /**
     * 服务被拒绝
     */
    SERVER_BUSY ,

    /**
     * 无效服务
     */
    SERVER_INVALID;








}
