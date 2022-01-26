package com.think.common.result;

import com.think.common.result.state.ResultCode;
import com.think.common.util.StringUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.annotations.Remark;
import com.think.core.bean.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * 通用复杂结果返回类
 */
@Slf4j
public class ThinkResult<T> implements Serializable {
    private static final long serialVersionUID = -5067861801641137196L;


    private ResultCode code;
    /**
     * 线程id
     */
    private long threadId ;

    /**
     * 自定义任务进程的id
     */
    private long processId ;

    /**
     * 初始化时间
     */
    private long time ;

    /**
     * 执行是否成功
     */
    private boolean success ;

    /**
     * 是否错误：捕获到异常
     */
    private boolean error;


    /**
     * 传递消息
     */
    private String message ;


    /**
     * 返回的数据，可以是任意类型
     */
    private T resultData;

    /**
     * 返回数据的长度，列表等 返回 列表的size
     * 对象其他  均为 1 ，
     * null 为 0
     */
    private int resultLength  = 0 ;

    /**
     * 携带的异常 （不一定携带）
     */
    private Throwable throwable ;

    private int splitSuffix = 0;


    private ThinkResult(boolean success, boolean exception, String message, T resultData, Throwable throwable,ResultCode code) {
        this.success = success;
        this.error = exception;
        this.message = message;
        this.resultData = resultData;
        this.throwable = throwable;
        this.time = ThinkMilliSecond.currentTimeMillis(); //System.currentTimeMillis();
        this.threadId = Thread.currentThread().getId();
        this.code =code;
        if(success == false){
            if (log.isDebugEnabled()) {
                log.debug("ThinkResult Not Success , message ： {}" ,this.message);
            }
        }
        if(throwable!=null){
            log.warn("throw info : {}" ,throwable);

        }
    }


    public <T> ThinkResult<T> appendMessage(String message){
        if (StringUtil.isEmpty(message)) {
            this.message= message;
        }else{
            this.message += " "+message;
        }
        return (ThinkResult<T>) this;
    }





    public static final <T> ThinkResult successIfCollectionNotEmpty(Collection<T> collections){
        if (collections!=null && !collections.isEmpty()) {
            return ThinkResult.success(collections);
        }else {
            return ThinkResult.fastFail();
        }
    }


    public static final <T>  ThinkResult<T> successIfNotNull(T data){
        if (data!=null) {
            return ThinkResult.success(data);
        }else {
            return ThinkResult.fastFail();
        }
    }


    @Deprecated
    public static final <T> ThinkResult<T> successIfNoNull(T data){
        return successIfNotNull(data);
    }


    public static final <T> ThinkResult<T> success(){
        return new ThinkResult(true,false,"",null,null,ResultCode.SUCCESS);
    }

    public static final <T> ThinkResult<T> success(T t){
        return new ThinkResult(true,false,"",t,null,ResultCode.SUCCESS);
    }

    public static final <T> ThinkResult<T> forbidden(String message){
        return new ThinkResult(false,false,message,null,null,ResultCode.SERVER_FORBIDDEN);
    }

    public static final <T> ThinkResult<T> paramsMissionOrError(String message){
        return new ThinkResult(false,false,message,null,null,ResultCode.REQUEST_PARAM_ERROR);
    }

    public static final <T> ThinkResult<T> notSupport(String message){
        return new ThinkResult(false,false,message,null,null,ResultCode.SERVER_NOT_SUPPORT);
    }

    public static final <T> ThinkResult<T> notSupport(){
        return notSupport("");
    }


    public static final <T> ThinkResult<T> notYet(T t){
        return new ThinkResult(false,false,"尚未完成",t,null,ResultCode.REQUIRED_NOT_YET);
    }

    public static final <T> ThinkResult<T> notYet(){
        return notYet(null);
    }



    public static final <T> ThinkResult<T> fail( String message,ResultCode code){
        return new ThinkResult(false,false,message,null,null,code);
    }

    public static final <T> ThinkResult<T> fail(T t,String message,ResultCode code ){
        return new ThinkResult(false,false,message,t,null,code);
    }

    public static final <T> ThinkResult<T> fastFail(){
        return new ThinkResult(false,false,"",null,null,ResultCode.SERVER_ERROR);
    }


    public static final <T> ThinkResult<T> error(Throwable throwable){
        return new ThinkResult(false,true,throwable.getMessage(),null,throwable,ResultCode.SERVER_ERROR);
    }

    public static final <T> ThinkResult<T> error(String message,Throwable throwable){
        return new ThinkResult(false,true,message,null,throwable,ResultCode.SERVER_ERROR);
    }


    public long getThreadId() {
        return threadId;
    }

    public long getProcessId() {
        return processId;
    }

    public long getTime() {
        return time;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isNotSuccess(){
        return !success;
    }

    /**
     * 快出抛出异常，如果是 未成功的话
     * @return
     */
    @Remark(value = "快出抛出异常，如果是 未成功的话",description = "在需要事务的方法里，通过此方法可以方便有效简化我们的编码方式")
    public ThinkResult<T> fastThrowExceptionIfNotSuccess() throws Exception{
        if(isNotSuccess()){
            if(this.getThrowable()!=null){
                throw new Exception(getMessage(),throwable);
            }else{
                throw new Exception(getMessage());
            }
        }
        return this;
    }

    @Remark(value = "快出抛出异常，如果是 未成功的话",description = "在需要事务的方法里，通过此方法可以方便有效简化我们的编码方式")
    public ThinkResult<T> fastThrowExceptionIfNotSuccess(String errorMessage) throws Exception{
        if(isNotSuccess()){
            if (log.isDebugEnabled()) {
                log.debug("thinkResult原生消息 :{}",getMessage());
            }
            if(this.getThrowable()!=null){
                throw new Exception(errorMessage + " :: "+getMessage(),throwable);
            }else{
                throw new Exception(errorMessage + " :: "+getMessage(),throwable);

            }
        }
        return this;
    }


    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public T getResultData() {
        return resultData;
    }

    public int getResultLength() {
        return resultLength;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public int getSplitSuffix() {
        return splitSuffix;
    }

    public void bindSplitSuffix(int splitSuffix) {
        this.splitSuffix = splitSuffix;
    }

    public ResultCode getCode() {
        return code;
    }


    public ThinkResult<T> setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    public static ThinkResult<Integer> resultFromLong2Int(ThinkResult<Long> result){
        return new ThinkResult<Integer>(result.isSuccess(),result.isError(),result.getMessage(),result.getResultData().intValue(),result.getThrowable(),result.getCode());
    }

    public static ThinkResult<Long> resultFromInt2Long(ThinkResult<Integer> result){
        return new ThinkResult<Long>(result.isSuccess(),result.isError(),result.getMessage(),result.getResultData().longValue(),result.getThrowable(),result.getCode());
    }

    public ThinkResult<T> setData(T t){
        this.resultData =t;
        return this;
    }


    @Remark("如果内容非Int，也许会丢失数据")
    public final ThinkResult<Integer> intResult(){
        Object t = getResultData();
        Integer r =0;
        if(t != null ){
            if (t instanceof Number) {
                r = ((Number) t).intValue();
            }
        }
        return new ThinkResult<Integer>(this.isSuccess(),this.isError(),this.getMessage(),r,this.getThrowable(),this.getCode());
    }

    @Remark("如果内容非Long 或者Int short ，也许会丢失数据")
    public final ThinkResult<Long> longResult(){
        Object t = getResultData();
        Long r =0L;
        if(t != null ){
            if (t instanceof Number) {
                r = ((Number) t).longValue();
            }
        }
        return new ThinkResult<Long>(this.isSuccess(),this.isError(),this.getMessage(),r,this.getThrowable(),this.getCode());
    }
}
