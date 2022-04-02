package com.think.core.executor;

import com.think.common.util.StringUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.security.ThinkToken;
import com.think.exception.ThinkRuntimeException;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/2 15:27
 * @description : TODO
 */
public class BackTaskHolder implements Serializable {
    private static final long serialVersionUID = 8917493391513621677L;
    private String id;
    private ThinkBackgroundTask task;
    private long lastExecuteTime;
    private int interval;
    private int maxLoop;

    private ThinkToken token;

    // @Remark("延迟时间")
    private long delayMillis = 0;
    private long initTime;

    public BackTaskHolder(ThinkBackgroundTask task, int interval, int maxLoop, ThinkToken token) {
        this.id = StringUtil.uuid();
        this.task = task;
        this.lastExecuteTime = 0L;
        this.interval = interval;
        this.maxLoop = maxLoop;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.token = token;
        if (maxLoop == 0) {
            throw new ThinkRuntimeException("后台任务的轮询次数不能设置为0，必须设置为大于0的数，需要永远轮询下去，请设置为-1");
        }
    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public String getId() {
        return id;
    }

    public int getInterval() {
        return interval;
    }

    public long getLastExecuteTime() {
        return lastExecuteTime;
    }

    public ThinkBackgroundTask getTask() {
        return task;
    }

    public ThinkToken getToken() {
        return token;
    }

    public int getMaxLoop() {
        return maxLoop;
    }

    /**
     * 是否允许执行？
     *
     * @return
     */
    public boolean canRun() {
        long time = ThinkMilliSecond.currentTimeMillis();
        return time - initTime > delayMillis;
    }


    public boolean canRemove() {
        return maxLoop == 0;
    }


    public void setLastExecuteTime(long lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
        if (maxLoop > 0) {
            this.maxLoop--;
        }
    }


}
