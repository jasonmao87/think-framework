package com.think.core.executor.thread;

import com.think.core.executor.ThinkAsyncExecutor;
import com.think.core.executor.ThinkAsyncTask;
import com.think.core.executor.ThinkThreadExecutor;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/21 17:25
 * @description : TODO
 */
public abstract class ThinkStopAbleTasker {


    private int loopCount  = 0;

    private boolean stopFlag = false;
    private boolean sotpWhileException = true;



    public void doWhileTask(ThinkAsyncTask task) throws Exception{
        Exception exception = null;
        while (stopFlag){
            try {
                task.execute();
            }catch (Exception e){
                if(sotpWhileException){
                    exception = e ;
                    break;
                }
            }
        }
        if(exception!=null){
            throw exception;
        }

    }






}
