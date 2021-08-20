package com.think.core.executor;

import com.think.core.security.ThinkToken;
import lombok.extern.slf4j.Slf4j;

/**
 * @Date :2021/3/24
 * @Name :ThinkExecuteThreadSharedMessage
 * @Description : 请输入
 */
@Slf4j
public class ThinkExecuteThreadSharedTokenManager {


    private static IAsyncTokenFilter asyncTokenFilter = null;

    protected static final ThreadLocal<String> sharedTokenString = new ThreadLocal<>();

    protected static final void trySet(){
        try{
            if (asyncTokenFilter != null) {
                ThinkToken token = asyncTokenFilter.getToken();
                if (log.isDebugEnabled()) {
                    log.debug("------------------------------------------------------------------------------------------------------------------------");
                    log.debug("THREAD[{}] 将注入TOKEN INFO  :{}   ",Thread.currentThread().getId(),token);
                    log.debug("------------------------------------------------------------------------------------------------------------------------");
                }
                sharedTokenString.set(token.toTokenString());
            }
            }catch (Exception e){}
    }

    protected static final ThinkToken getFromFilter(){
        if (asyncTokenFilter != null) {
            return asyncTokenFilter.getToken();

        }
        return null;
    }


    protected static void set(ThinkToken token){
        if(token!=null) {
            set(token.toTokenString());
        }else{
//            if (log.isDebugEnabled()) {
//                log.debug("未这只任何线程通信token，原因未无法获取到这个token ");
//            }
        }
    }

    protected static void set(String tokenString){
        sharedTokenString.set(tokenString);
    }


    /**
     * 优先获取 线程变量中得token ，如果无法获取，则会从当前运行线程的本地其他资源变量中获取，比如webRequest等
     * @return
     */
    public static final  ThinkToken get(){
        if(sharedTokenString.get()!=null) {
            try {
                return ThinkToken.parseOfJsonString(sharedTokenString.get());
            } catch (Exception e) {
                return null;
            }
        }else{
            if (asyncTokenFilter != null) {
                return asyncTokenFilter.getToken();
            }
            return null;
        }
    }

    public static final String getTokenString(){
        ThinkToken token =get();
        if(token!=null){
            return token.toTokenString();
        }
        return null;
    }

    protected static void remove(){
        sharedTokenString.remove();
    }


    public static void setAsyncTokenFilter(IAsyncTokenFilter asyncTokenFilter) {
        ThinkExecuteThreadSharedTokenManager.asyncTokenFilter = asyncTokenFilter;
    }
}
