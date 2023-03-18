package com.think.core.executor;

import com.think.common.util.TimeUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 后台执行得任务
 * @author JasonMao
 */
public interface ThinkBackgroundTask {
    /**
     * 执行！
     */
    void execute();
}
