package com.think.core.executor;

import com.think.common.util.DateUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.annotations.Remark;
import com.think.core.bean.ThinkSchedule;
import com.think.core.security.ThinkToken;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台待执行任务 托管对象
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/2 16:00
 * @description :  后台待执行任务 托管对象
 */
public class BackgroundTaskHolder implements Serializable {

    private String name ;

    private static final long serialVersionUID = 427127647885710171L;
    @Remark("初始化时间")
    private long initTime ;

    private long autoDestroyTime ;

    @Remark("携带的token信息")
    private ThinkToken token;

    @Remark("实际需要执行的后台任务")
    private ThinkBackgroundTask task;

    @Remark("定时schedule")
    private ThinkSchedule schedule;

    @Remark("是否允许执行")
    protected boolean canExecute(){
        if (this.isSafe()) {
            return this.schedule.tryHit();
        }
        return false;
    }

    public boolean isSafe(){
        return this.task != null && this.schedule != null;
    }


    @Remark("是否允许销毁")
    protected boolean canDestroy(){
        if(this.autoDestroyTime < 0){
            return false;
        }
        return ThinkMilliSecond.currentTimeMillis() > this.autoDestroyTime;
    }

    public BackgroundTaskHolder() {
        this.initTime = ThinkMilliSecond.currentTimeMillis();
    }

    public BackgroundTaskHolder(String name ,long initTime, long autoDestroyTime, ThinkToken token, ThinkBackgroundTask task, ThinkSchedule schedule) {
        this.initTime = initTime;
        this.autoDestroyTime = autoDestroyTime;
        this.token = token;
        this.task = task;
        this.schedule = schedule;
        this.name = name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAutoDestroyTime(long autoDestroyTime) {
        if(this.autoDestroyTime ==0) {
            this.autoDestroyTime = autoDestroyTime;
        }
    }

    public void setSchedule(ThinkSchedule schedule) {
        if(this.schedule == null) {
            this.schedule = schedule;
        }
    }

    public void setTask(ThinkBackgroundTask task) {
        if(this.task == null) {
            this.task = task;
        }
    }

    public void setToken(ThinkToken token) {
        if(this.token == null) {
            this.token = token;
        }
    }

    public void setInitTime(long initTime) {
        if(this.initTime ==  0) {
            this.initTime = initTime;
        }
    }

    public long getInitTime() {
        return initTime;
    }

    public long getAutoDestroyTime() {
        return autoDestroyTime;
    }

    public ThinkToken getToken() {
        return token;
    }

    public ThinkBackgroundTask getTask() {
        return task;
    }

    public ThinkSchedule getSchedule() {
        return schedule;
    }

    public long lastExecuteTime(){
        return this.schedule.getLastHitTime();
    }
}
