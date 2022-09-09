package com.think.core.executor;

import com.think.common.util.TimeUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/1 13:10
 * @description : TODO
 */
public class ThinkThreadExecutorMonitor {


    private static ThreadPoolExecutor getExecutor(){
        ThreadPoolExecutor poolExecutor =(ThreadPoolExecutor) ThinkThreadExecutor.getExecutor();
        return poolExecutor;
    }

    private static  String numberInfo(long x){
        StringBuilder info = new StringBuilder("").append(x);
        while (info.length() < 8){
            info.insert(0,'_');
        }
        return info.toString();
    }


    public static String monitorInfo(){
        ThreadPoolExecutor e = getExecutor();
        StringBuilder info = new StringBuilder("");
        info.append(">>>>>>>>>>>>>>>>>>>>>>-START********THINK - EXECUTOR - REPORT********START-<<<<<<<<<<<<<<<<<<<<<<<");
        info.append("\n");
        /*>>>>>>>>>>>>>>>>>>>>>>-返回主动执行任务的近似线程数-<<<<<<<<<<<<<<<<<<<<<<<*/
        int activeCount = e.getActiveCount();
        info.append("activeCount         : ").append(numberInfo(activeCount)).append(" -------------------主动执行任务的近似线程数") ;
        info.append("\n");
        /*>>>>>>>>>>>>>>>>>>>>>>-返回核心线程数-<<<<<<<<<<<<<<<<<<<<<<<*/
        int corePoolSize = e.getCorePoolSize();
        info.append("corePoolSize        : ").append(numberInfo(corePoolSize)).append(" -------------------核心线程数") ;
        info.append("\n");
        /*>>>>>>>>>>>>>>>>>>>>>>-  返回已完成执行的近似任务总数-<<<<<<<<<<<<<<<<<<<<<<<*/
        long completedTaskCount = e.getCompletedTaskCount();
        info.append("completedTaskCount  : ").append(numberInfo(completedTaskCount)).append(" -------------------已完成执行的近似任务总数") ;
        info.append("\n");
        /*>>>>>>>>>>>>>>>>>>>>>>-  返回曾经同时位于池中的最大线程数-<<<<<<<<<<<<<<<<<<<<<<<*/
        int largestPoolSize = e.getLargestPoolSize();
        info.append("largestPoolSize     : ").append(numberInfo(largestPoolSize)).append(" -------------------曾经同时位于池中的最大线程数") ;
        info.append("\n");
        /*>>>>>>>>>>>>>>>>>>>>>>-  返回允许的最大线程数-<<<<<<<<<<<<<<<<<<<<<<<*/
        int maximumPoolSize = e.getMaximumPoolSize();
        info.append("maximumPoolSize     : ").append(numberInfo(maximumPoolSize)).append(" -------------------允许的最大线程数") ;
        info.append("\n");
        /*>>>>>>>>>>>>>>>>>>>>>>- 返回曾计划执行的近似任务总数-<<<<<<<<<<<<<<<<<<<<<<<*/
        long taskCount = e.getTaskCount();
        info.append("taskCount           : ").append(numberInfo(taskCount)).append(" -------------------曾计划执行的近似任务总数") ;
        info.append("\n");
        info.append(">>>>>>>>>>>>>>>>>>>>>>-END  ********THINK - EXECUTOR - REPORT********  END-<<<<<<<<<<<<<<<<<<<<<<<");
        info.append("\n");
        return info.toString();
    }


    public static void main(String[] args) {
        ThinkAsyncExecutor.execute(()->{
            System.out.println("START ...");
            while (true){
                System.out.println(monitorInfo());
                TimeUtil.sleep(1,TimeUnit.SECONDS);
            }
        });
        for (int i = 0; i < 1000; i++) {
            TimeUtil.sleep(1,TimeUnit.SECONDS);
            ThinkAsyncExecutor.execute(()->{
                TimeUtil.sleep(1,TimeUnit.SECONDS);
                System.out.println("END ");
            });
        }

    }



}
