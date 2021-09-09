package com.think.web;

import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import com.think.common.result.state.ThinkResultState;
import com.think.common.util.ThinkMilliSecond;
import com.think.web.util.WebUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
@Accessors(chain = true)
public class R<T>  implements Serializable {

    private static String defaultServiceId = null ;
    private static String defaultServiceName = null;

    @ApiModelProperty("服务器当前时间戳")
    private long serverTime;

    @ApiModelProperty("请求状态")
    private ThinkResultState state;

    @ApiModelProperty("请求资源")
    private String uri ;

    @ApiModelProperty("结果携带消息")
    private String message =null;

    @ApiModelProperty("异常堆栈")
    private Throwable throwable;

    @ApiModelProperty("请求结果")
    private T result;
    @ApiModelProperty("服务器线程")
    private long threadId ;

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("服务Id")
    private String serviceId  ;

    private R(){

    }


    private static R _init(ResultCode code){
        R webResult = new R();
        if(defaultServiceId !=null){
            webResult.setServiceId(defaultServiceId);
        }
        if(defaultServiceName !=null){
            webResult.setServiceName(defaultServiceName);
        }
        webResult.setServerTime( ThinkMilliSecond.currentTimeMillis() )
                .setThreadId(Thread.currentThread().getId())
                .setState(ThinkResultState.get(code))
                .setUri(WebUtil.uri());
        return webResult;
    }

    /**
     * 通过THINK RESULT 实例化
     * @param result
     * @return
     */
    public final static R RESULT(ThinkResult result){
        if(result.isSuccess()){
            return SUCCESS( result.getResultData());
        }else {
            return _init(result.getCode())
                    .setThrowable(result.getThrowable())
                    .setMessage(result.getMessage());
        }
    }



    public static R AUTH_NO_LOGIN(){
        R webResult = _init(ResultCode.AUTH_NO_LOGIN);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R AUTH_FORBIDDEN(){
        R webResult = _init(ResultCode.AUTH_FORBIDDEN);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R AUTH_EXPIRE(){
        R webResult = _init(ResultCode.AUTH_EXPIRE);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R AUTH_FAIL(){
        R webResult = _init(ResultCode.AUTH_FAIL);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R AUTH_ERROR(){
        R webResult = _init(ResultCode.AUTH_ERROR);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R REQUEST_PARAM_ERROR(){
        R webResult = _init(ResultCode.REQUEST_PARAM_ERROR);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    /**
     * 请求资源不存在
     * @return
     */
    public static R REQUEST_NO_RESOURCE(){
        R webResult = _init(ResultCode.REQUEST_NO_RESOURCE);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R REQUEST_LIMITED(){
        R webResult = _init(ResultCode.REQUEST_LIMITED);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R SUCCESS(Object result){
        R webResult = _init(ResultCode.SUCCESS);
        webResult.setMessage(webResult.getState().getDescription())
                .setResult(result);
        return webResult;
    }

    public static R DATA_NOT_NULL(Object data, String nullDataMessage){
        if(data ==null){
            return R.REQUEST_NO_RESOURCE().setMessage(nullDataMessage);
        }
        return R.SUCCESS(data);
    }

    /**
     * 异步通知结果，需要携带 异步处理 id ，用于前端查询
     * @param asyncProcessId
     * @return
     */
    public static R PROCESS_ASYNC(String asyncProcessId){
        R webResult = _init(ResultCode.PROCESS_ASYNC);
        webResult.setMessage(webResult.getState().getDescription());
        webResult.setResult(asyncProcessId);
        return webResult;
    }

    public static R SERVER_ERROR(Throwable throwable){
        R webResult = _init(ResultCode.SERVER_ERROR);
        webResult.setMessage(webResult.getState().getDescription());
        if(throwable!=null){
            webResult.setThrowable(throwable);
        }
        return webResult;
    }

    public static R SERVER_FORBIDDEN(){
        R webResult = _init(ResultCode.SERVER_FORBIDDEN);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R SERVER_BUSY(){
        R webResult = _init(ResultCode.SERVER_BUSY);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }

    public static R SERVER_INVALID(){
        R webResult = _init(ResultCode.SERVER_INVALID);
        webResult.setMessage(webResult.getState().getDescription());
        return webResult;
    }


    public static final void defaultServiceInfo(String serviceId , String serviceName){
        if(defaultServiceId == null){
            defaultServiceId = serviceId;
        }
        if(defaultServiceName == null) {
            defaultServiceName = serviceName;
        }

    }



    public Map<String,Object> toMap(){
        Map map = new HashMap();
        map.put("serverTime",serverTime);
        map.put("ThinkResultState",state.toString());
        map.put("uri",uri);
        map.put("message",message);
        map.put("throwable",throwable);
        map.put("result",result);
        map.put("threadId",threadId);
        map.put("serviceName",serviceName);
        map.put("serviceId",serviceId);
        return map;

    }





}
