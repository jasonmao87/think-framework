package com.think.core.executor.schedule;

import com.think.common.util.ThinkMilliSecond;
import com.think.core.bean.schedules.ThinkScheduleCronConfig;
import com.think.core.executor.ThinkAsyncTask;
import com.think.core.security.ThinkToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

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



    public static  void hold(ThinkAsyncTask task, ThinkScheduleCronConfig config, ThinkToken token){
        scheduledTaskQueue.add(new ScheduledTask(task,config,token));
    }


    /**
     * 尝试获取 一个 需要执行的任务
     * @return
     */
    public static final Optional<ScheduledTask> getTask(){
        checkTask();
        return Optional.ofNullable(currentTaskQueue.poll());
    }


    private static synchronized boolean allowCheck(){
        long now = ThinkMilliSecond.currentTimeMillis();
        if(now - lastCheck >900) {
            lastCheck = now;
            return true;
        }
        return false;
    }

    private static void checkTask(){
        if(allowCheck()) {
            List<ScheduledTask> tempList = new ArrayList<>();
            ScheduledTask t = scheduledTaskQueue.poll();
            while (t != null) {
                if (t.getScheduledConfig().tryTrigger()) {
                    //如果触发 ，那么 则 加入到 执行队列
                    currentTaskQueue.add(t);
                }
                if (t.canDestroy()) {
                    continue;
                } else {
                    //如果尚未达到 销毁 要求，则放入 临时列表，准备 放回队列
                    tempList.add(t);
                }
            }
            for (ScheduledTask scheduledTask : tempList) {
                ThinkScheduledTaskHolder.scheduledTaskQueue.add(scheduledTask);
            }
            tempList.clear();
        }
    }




}
