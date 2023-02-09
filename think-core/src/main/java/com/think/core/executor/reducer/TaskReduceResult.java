package com.think.core.executor.reducer;

import com.think.common.util.ThinkMilliSecond;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/9 9:56
 * @description :
 */
public abstract class TaskReduceResult {

    public TaskReduceResult() {
        this.initTime = ThinkMilliSecond.currentTimeMillis();
    }

    private boolean success =false;
    /**初始化时间  ---by JasonMao @ 2023/2/9 10:02 */
    private long initTime =-1 ;
    /**
     * 任务开始分布执行的时间 
     */
    private long startTime =-1L;
    /**任务完成时间   ---by JasonMao @ 2023/2/9 10:02 */
    private long completeTime =-1 ;

    /**执行的 线程数量  ---by JasonMao @ 2023/2/9 10:01 */
    private int threadCount = 0;



}
