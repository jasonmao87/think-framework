package com.think.core.executor;

import com.think.common.util.FastJsonUtil;
import com.think.common.util.TimeUtil;
import com.think.core.security.token.ThinkSecurityToken;
import com.think.core.security.token.ThinkSecurityTokenTransferManager;
import com.think.core.security.token.ThinkSecurityTokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Date :2021/3/24
 * @Name :ThinkAsyncExecutor
 * @Description : 异步任务执行器
 * @author JasonMao
 */
@Slf4j
public class ThinkAsyncExecutor {

//    @Deprecated
//    public static final ThinkToken getThreadLocalToken(){
//        return ThinkExecuteThreadSharedTokenManager.get();
//    }

    public static final ThinkSecurityToken getSecurityToken(){
        return ThinkSecurityTokenTransferManager.getToken() ;
    }

    private static String lastAcceptThreadName = "";
    /**
     * 携带线程共享信息的 异步执行
     *  场景 ：在web开发环境，我们的主线程中包含用户的一些信息，但是当我们执行异步任务的时候，新启的线程是无法拿到我们的 request中的信息的。
     * @param task                     需要执行的任务的实现类
     * @return
     */
    public static final CompletableFuture<Void> execute(final ThinkAsyncTask task ){
        String threadName =Thread.currentThread().getName();
        if (threadName.equals(lastAcceptThreadName)) {
            TimeUtil.sleep(1, TimeUnit.MILLISECONDS);
        }
        ThinkSecurityToken securityToken = getSecurityToken();
        return executeWithToken(task,securityToken);
//        if(securityToken!=null){
//        }else{
//            return execute(task,null);
//        }

//        //
//        if(sharedToken!=null) {
//            return execute(task, sharedToken.toTokenString());
//        }else{
//            return execute(task,null);
//        }
//

    }

    /**
     * 同步的执行方法
     * @param task
     * @param token
     */
    public static final void runSyncWithToken(final ThinkAsyncTask task,final ThinkSecurityToken token){
        if(token!=null) {
            if (log.isTraceEnabled()) {
                log.trace("TOKEN 信息 === {}" , FastJsonUtil.parseToJSON(token));
            }
            ThinkSecurityTokenTransferManager.setThreadLocal(token, false);
        }
        try {
            if(token!=null) {
                ThinkThreadExecutor.noticeDataRegionChange(token.getCurrentRegion());
            }
            task.execute();
        }catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error("",e );
            }
        }finally {
            ThinkThreadExecutor.getChangedDataRagionAndRemove();
            ThinkSecurityTokenTransferManager.removeTokenFromThreadLocal();
        }
    }


    public static final CompletableFuture<Void> executeWithToken(final ThinkAsyncTask task,final ThinkSecurityToken token){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(token!=null) {
                    if (log.isTraceEnabled()) {
                        log.trace("异步的TOKEN 信息 === {}" , FastJsonUtil.parseToJSON(token));
                    }
                    ThinkSecurityTokenTransferManager.setThreadLocal(token, false);
                }
                try {
                    if(token!=null) {
                        ThinkThreadExecutor.noticeDataRegionChange(token.getCurrentRegion());
                    }
                    task.execute();
                }catch (Exception e){
                    if (log.isErrorEnabled()) {
                        log.error("",e );
                    }
                }finally {
                    ThinkThreadExecutor.getChangedDataRagionAndRemove();
                    ThinkSecurityTokenTransferManager.removeTokenFromThreadLocal();
                }
            }
        };
        return CompletableFuture.runAsync(runnable,ThinkThreadExecutor.getExecutor());
    }

//
//    public static final CompletableFuture<Void> execute(final ThinkAsyncTask task ,final String thinkTokenString){
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                /**
//                 * 初始化本地线程变量，一遍获取token
//                 */
//                ThinkSecurityToken token = ThinkSecurityToken.
//                ThinkExecuteThreadSharedTokenManager.set(token);
//                try{
//                    if(token!=null) {
//                        ThinkThreadExecutor.noticeDataRegionChange(token.getCurrentRegion());
//                    }
//                    task.execute();
//
//                }catch (Exception e){
//                    if (log.isErrorEnabled()) {
//                        log.error("",e );
//                    }
//                }finally {
//                    ThinkThreadExecutor.getChangedDataRagionAndRemove();
//                    ThinkExecuteThreadSharedTokenManager.remove();
//                }
//            }
//        };
//        return CompletableFuture.runAsync(runnable,ThinkThreadExecutor.getExecutor());
//
//    }




}
