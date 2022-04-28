package com.think.core.executor.schedule;

import com.think.common.util.ThinkMilliSecond;
import com.think.core.bean.schedules.ThinkScheduleCronConfig;
import com.think.core.executor.ThinkAsyncTask;
import com.think.core.security.token.ThinkSecurityToken;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
public class ThinkScheduledTaskHolder {

    private static long lastCheck = 0L;

    /**
     * 缓存的 所有 任务
     */
    public static final Queue<ScheduledTask> scheduledTaskQueue = new ArrayBlockingQueue<>(1024);

    /**
     * 需要立即执行的 任务队列
     * @return
     */
    private static Queue<ScheduledTask> currentTaskQueue = new ArrayBlockingQueue<>(128);



    public static  void hold(ThinkAsyncTask task, ThinkScheduleCronConfig config, ThinkSecurityToken token){
        scheduledTaskQueue.add(new ScheduledTask(task,config,token));
    }


    /**
     * 尝试获取 一个 需要执行的任务
     * @return
     */
    public static final Optional<ScheduledTask> getTask(){
        try {
            return Optional.ofNullable(currentTaskQueue.poll());
        }finally {
            checkTask();
        }
    }


    private static synchronized boolean allowCheck(){
        int waitTime = 900 ;
        long now = ThinkMilliSecond.currentTimeMillis();
        if(now - lastCheck > waitTime ) {
            lastCheck = now;
            return true;
        }
        return false;
    }

    private static void checkTask(){
        if(allowCheck()) {
            List<ScheduledTask> tempList = new ArrayList<>();
//            log.info("执行任务检查，队列长度为 {}" ,scheduledTaskQueue.size());
            ScheduledTask t = scheduledTaskQueue.poll();
            while (t != null) {
//                log.info("该任务将在{}触发" , StringUtil.fmtAsDatetime(t.getScheduledConfig().nextTriggerTime()) );

                if (t.getScheduledConfig().tryTrigger()) {
//                    log.info("添加到 即将执行的任务 ....{}" ,StringUtil.fmtAsDatetime(t.getScheduledConfig().nextTriggerTime()));
                    //如果触发 ，那么 则 加入到 执行队列
                    currentTaskQueue.add(t);
//                    log.info(" finish ...add ");
                }
                if (!t.canDestroy()) {
                    tempList.add(t);
                }
                t = scheduledTaskQueue.poll();
            }
            // end of run ...
            for (ScheduledTask scheduledTask : tempList) {
                ThinkScheduledTaskHolder.scheduledTaskQueue.add(scheduledTask);
            }
            tempList.clear();
//            log.info("检查完毕");

        }
    }




}
