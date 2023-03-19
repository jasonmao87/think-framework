package com.think.core.security;

import com.think.common.util.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/6 11:08
 * @description :
 */
public class ThinkLock implements Lock {

    private thinkInner inner;

    public ThinkLock() {
        this.inner = new ThinkLock.thinkInner();
    }

    @Override
    public void lock() {
        while (!inner.lock()){
            System.out.println("FAIL ----");

            try{
                synchronized (this){
                    this.wait();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        synchronized (this) {
            this.inner.unlock();
            try {
                this.notifyAll();
            }catch (Exception e){
                System.out.println("ERROR : " + Thread.currentThread().getName() );
                e.printStackTrace();
            }
        }
    }

    @NotNull
    @Override
    public Condition newCondition() {
        return null;
    }


    private static class thinkInner{
        private String threadName ;
        private boolean locked = false;

        public thinkInner() {
            this.threadName = Thread.currentThread().getName();
        }

        public String getThreadName() {
            return threadName;
        }

        public boolean isLocked() {
            return locked;
        }

        public synchronized boolean lock(){
            if(locked){
                return false;
            }else{
                locked = true;
                return true;
            }
        }

        public void unlock(){
            this.locked = false;

        }
    }


    public static void main(String[] args) {
        Lock lock = new ThinkLock();
        for(int i= 0 ; i < 2; i++){
            final Thread thread = new Thread(() -> {
                lock.lock();
                System.out.println("locked");
                TimeUtil.sleep(3, TimeUnit.SECONDS);
                System.out.println("unlock");
                lock.unlock();

            });
            thread.start();

        }


    }
}
