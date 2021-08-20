package com.think.core.executor;

import com.think.core.security.ThinkToken;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @Date :2021/3/24
 * @Name :ThinkAsyncExecutor
 * @Description : 异步任务执行器
 */
@Slf4j
public class ThinkAsyncExecutor {

    public static final ThinkToken getThreadLocalToken(){
        return ThinkExecuteThreadSharedTokenManager.get();
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
            try{
                Thread.sleep(1);
            }catch (Exception e){

            }
        }
//        ThinkExecuteThreadSharedTokenManager.trySet();
        ThinkToken sharedToken = getThreadLocalToken();


        if(sharedToken!=null) {
            return execute(task, sharedToken.toTokenString());
        }else{
            return execute(task,null);
        }


    }


    public static final CompletableFuture<Void> execute(final ThinkAsyncTask task ,final String thinkTokenString){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /**
                 * 初始化本地线程变量，一遍获取token
                 */
                ThinkToken token = ThinkToken.parseOfJsonString(thinkTokenString);
                ThinkExecuteThreadSharedTokenManager.set(token);
                try{
                    task.execute();
                }catch (Exception e){
                    if (log.isErrorEnabled()) {
                        log.error("",e );
                    }
                }finally {
                    ThinkExecuteThreadSharedTokenManager.remove();
                }
            }
        };
        return CompletableFuture.runAsync(runnable,ThinkThreadExecutor.getExecutor());

    }
//
//    public static void main(String[] args) throws IOException {
//
//        Path rootPath = Paths.get("X:\\tmp");
//
//
//        Files.walkFileTree(rootPath, new FileVisitor<Path>() {
//            @Override
//            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//
//                    System.out.println("--:" + file + "  owner:"+ Files.getOwner(file) + " lastModified:" + Files.getLastModifiedTime(file) + " SIZE:" + Files.size(file)/1024);
//
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//                System.out.println("visit file failed: " + file);
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                //System.out.println("<<<<: " + dir);
//                return FileVisitResult.CONTINUE;
//            }
//        });
//
//    }






}
