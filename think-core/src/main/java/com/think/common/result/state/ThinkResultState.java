package com.think.common.result.state;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务请求状态
 */

public class ThinkResultState {
    private ResultCode code ;
    private String name ;
    private String description;
    private ThinkResultState(ResultCode type){
        this.code = type;
        switch (type){
            case SUCCESS:{
                this.name = "SUCCESS";
                this.description = "";
                break;
            }
            case REQUEST_LIMITED:{
                this.name = "请求受限";
                this.description = "请求过于频繁";
                break;
            }
            case REQUEST_NO_RESOURCE:{
                this.name = "请求资源不存在";
                this.description = "错误的URI地址";
                break;
            }
            case REQUEST_PARAM_ERROR:{
                this.name = "请求参数错误";
                this.description = "缺少必要请求参数，或者请求参数超长，类型或者格式错误";
                break;
            }
            case PROCESS_ASYNC:{
                this.name = "异步处理结果";
                this.description = "异步处理结果";
                break;
            }
            case AUTH_NO_LOGIN:{
                this.name = "未登录";
                this.description = "未登录";
                break;
            }
            case AUTH_ERROR:{
                this.name = "授权错误";
                this.description = "授权未通过验证，签名错误";
                break;
            }
            case AUTH_FAIL:{
                this.name = "授权失败";
                this.description = "验证授权失败";
                break;
            }
            case AUTH_MISSING:{
                this.name = "缺少授权必要信息";
                this.description = "无法通过授权，缺少必要的参数";
                break;
            }
            case AUTH_TIME_ERROR:{
                this.name = "客户端时间异常";
                this.description = "客户端时间与服务器差距过大，请检查本地时间设置是否准确";
                break;
            }

            case AUTH_EXPIRE:{
                this.name = "授权过期";
                this.description = "TOKEN已过期";
                break;
            }
            case AUTH_FORBIDDEN:{
                this.name = "授权不足";
                this.description = "授权不足，被拒绝操作";
                break;
            }
            case SERVER_BUSY:{
                this.name = "服务忙";
                this.description = "服务器忙，请稍后再试";
                break;
            }
            case SERVER_NOT_SUPPORT:{
                this.name = "服务不被支持";
                this.description = "服务暂不支持此方法";
                break;
            }
            case SERVER_ERROR:{
                this.name = "服务错误";
                this.description = "服务器错误";
                break;
            }
            case SERVER_FORBIDDEN:{
                this.name = "服务被拒绝";
                this.description = "拒绝执行，可能由于业务状态不被允许造成的错误，或其他拒绝原因";
                break;
            }
            case SERVER_INVALID:{
                this.name = "无效服务";
                this.description = "服务不存在或暂时无法提供服务，请稍后再试";
                break;
            }
        }
    }
    private static final Map<ResultCode, ThinkResultState> cache = new HashMap<>();

    public static final ThinkResultState get(ResultCode code){
        if(cache.containsKey(code)){
            return cache.get(code);
        }
        ThinkResultState resultType = new ThinkResultState(code);
        cache.put(code,resultType);
        return  resultType;
    }

    public String getDescription() {
        return description;
    }


    public String getName() {
        return name;
    }

    public ResultCode getCode() {
        return code;
    }
}
