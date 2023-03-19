package com.think.core.executor.schedule;

import com.think.core.bean.schedules.ThinkScheduleCronConfig;
import com.think.core.executor.ThinkAsyncTask;
import com.think.core.security.token.ThinkSecurityToken;
import com.think.exception.ThinkRuntimeException;

import java.io.Serializable;


public class ScheduledTask implements Serializable {

    private static final long serialVersionUID = -2615299348205229925L;
    private ThinkAsyncTask task;
    private ThinkScheduleCronConfig scheduledConfig;

    private ThinkSecurityToken token;


    /**
     * 不指定token的构建
     * @param task
     * @param scheduledConfig
     */
    public ScheduledTask(ThinkAsyncTask task, ThinkScheduleCronConfig scheduledConfig) {
        this.task = task;
        this.scheduledConfig = scheduledConfig;
    }

    /**
     * 指定token的构建
     * @param task
     * @param scheduledConfig
     * @param token
     */
    public ScheduledTask(ThinkAsyncTask task, ThinkScheduleCronConfig scheduledConfig , ThinkSecurityToken token) {
        this.task = task;
        this.scheduledConfig = scheduledConfig;
        this.token = token;
        if(!this.scheduledConfig.isEnable()){
            throw new ThinkRuntimeException("定时配置为完成，拒绝启动定时任务！");
        }
    }

    public ThinkScheduleCronConfig getScheduledConfig() {
        return scheduledConfig;
    }

    public ThinkAsyncTask getTask() {
        return task;
    }

    public boolean canDestroy(){
        return getScheduledConfig().canDestroy();
    }

    public ThinkSecurityToken getToken() {
        return token;
    }

    //    public void execute() throws Exception {
//        try {
//            this.getTask().execute();
//        }catch (Exception e){
//
//
//        }
//    }
}
