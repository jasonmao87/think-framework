package com.think.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/3/23 15:56
 * @description : 编码 构造工具 ，协助系统构造 连续，且统一的 不重复的编码
 */
@Slf4j
public class CodeBuilder {
    private static long lastCodeTime = 0L;
    private static Queue<String> queue = new ArrayBlockingQueue(1024);

    private static volatile boolean lockState = false;


    private static final int firstIndexedYear = 2022;
    private static final  char[] yearCharArray = {
            '0','1','2','3','4','5','6','7','8',
            '9','A','B','C','D','E','F','G','H',
            'I','J','K','L','M','N','O','P','Q',
            'R','S','T','U','V','W','X','Y','Z'
    };

    private static synchronized  boolean tryLock(){
        if(lockState){
            return false;
        }
        lockState =true;
        return true;
    }

    private static synchronized final boolean unlock(){
        if(lockState){
            lockState = false;
            return true;
        }
        return false;
    }


    private static final char firstRootChar(){
        int year = DateUtil.year();
        return firstRootChar(year);
    }

    private static final char firstRootChar(int year){
        int index = year - firstIndexedYear;
        if(index < 0 ){
            return '@';
        }else if(index >= yearCharArray.length){
            return '#';
        }
        return yearCharArray[index];
    }


    private static final long millisOfYear(){
        Date now = DateUtil.now();
        Date beginOfYear = DateUtil.beginOfYear(now);
        return (now.getTime() - beginOfYear.getTime());
    }

    private static final String buildCode( long value ){
        return new StringBuilder("")
                .append(firstRootChar())
                .append(Long.toString(value,36).toUpperCase())
                .toString();
    }
    private static final String indexCode(int index){
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toString(index,36).toUpperCase());
        while (sb.length() < 2){
            sb.insert(0,"0");
        }
        return sb.toString();
    }

    private static final void initQueue(){
        if(tryLock()){
            int limitExecuteDuration  = 10 ;
            long value = 0L;
            if( ( ThinkMilliSecond.currentTimeMillis() - lastCodeTime) > limitExecuteDuration) {
                value = millisOfYear();

                int size = 1024;
                for (int i = 0; i < size; i++) {
                    StringBuilder codeBuilder = new StringBuilder("")
                            .append(buildCode(value))
                            .append(indexCode(i));
                    queue.offer(codeBuilder.toString());
                }
                lastCodeTime = ThinkMilliSecond.currentTimeMillis();
            }

            //---------------
            unlock();
        }else{
//           log.warn("暂时无法锁定资源，本次初始化未成功");
        }

    }


    /**
     *  首位编码 会比较长 ，包含  年月日 各一位
     *  year + month + day
     * @return
     */
    public static final String newRootCode(){
        String code = queue.poll();
        int tryCount = 0 ;
        while (code == null){
            tryCount ++;

            TimeUtil.sleep(tryCount + 5, TimeUnit.MILLISECONDS);
            initQueue();
            code = queue.poll();
        }
        return code;
    }

//
//
//    @Deprecated
//    private static void mainTEST(String[] args) {
//
//        final Set<String> set =new HashSet<>();
//        for (int x.log = 0; x.log < 200; x.log++) {
//            new Thread(()->{
//                for (int i = 0; i < 2048; i++) {
//                    try {
//                        String code = newRootCode();
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        System.out.println("-----" + i +"________________ 遇到拒绝 ");
////                        TimeUtil.sleep(100,TimeUnit.MILLISECONDS);
//                    }
//
//                }
//            }).start();
//        }
//
//        int  time = 0 ;
//        while (true){
//            time ++ ;
//            TimeUtil.sleep(1,TimeUnit.SECONDS);
//            System.out.println("当前构建：" + set.size() + " 耗时" + time +"S ");
//        }
//
//    }


}
