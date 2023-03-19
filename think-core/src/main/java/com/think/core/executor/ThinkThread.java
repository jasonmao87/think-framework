package com.think.core.executor;

import com.think.common.util.ThinkMilliSecond;
import com.think.common.util.TimeUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/7 10:56
 * @description :
 */
public class ThinkThread {


    public static final void executeThread(Runnable runnable) {
        ThinkThreadExecutor.getExecutor().execute(runnable);
    }


    public static final void executeThread(Runnable runnable , int timeout, TimeUnit timeUnit) {
        Thread main = new Thread(()->{
            TTT t =new TTT( runnable);
            t.start();
            System.out.println(t.getName());
            long expire = ThinkMilliSecond.timeMillisAfter(timeout,timeUnit);
            boolean expireState = false;

            while (t.isAlive() && ThinkMilliSecond.currentTimeMillis() - expire < 0){
                System.out.println(" t is " + t.isAlive() );
                TimeUtil.sleep(300,TimeUnit.MILLISECONDS);
            }
            if(t.isAlive()){


                synchronized (t) {

                    try {

                        t.stops();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                        t.interrupt();
                }
//                t.stop();
                throw new RuntimeException("---超时啦");
            }
        });
        main.start();



    }


    public static void main(String[] args) {
        ThinkMilliSecond.currentTimeMillis();


        executeThread(()->{
            System.out.println("start " + Thread.currentThread().getName());
            int i= 0;
            for ( i=0;i<100; i++){
                TimeUtil.sleep(1000,TimeUnit.MILLISECONDS);
                System.out.println( i  +"<>>" + Thread.currentThread().isInterrupted());
//                System.out.println( i  +"<>>");
                if(i== 100){
                    i = 0;
                }
            }
        },5,TimeUnit.SECONDS);
    }
}

class TTT extends Thread{

    public TTT() {
    }

    public TTT(Runnable target) {
        super(target);
    }

    public void stops() throws InterruptedException{
        yield();
        Thread.yield();
        throw new InterruptedException("000");
    }
}
