package com.think.core.executor.reduce;

import com.think.core.executor.ThinkAsyncExecutor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 14:49
 * @description :
 */
public class ThinkLocalMapReduceExecutor<S,V> {

    Queue<S> dataQueue ;

    private LocalMapReduce<S,V> mapReduce;
    protected ThinkLocalMapReduceExecutor(LocalMapReduce localMapReduce) {
        this.mapReduce = localMapReduce;
        dataQueue = new ConcurrentLinkedQueue<>();
    }

    private ILocalMapTask mapTask;


    public static final<S,V> ThinkLocalMapReduceExecutor<S,V> getInstance(LocalMapReduce localMapReduce){
        return new ThinkLocalMapReduceExecutor(localMapReduce);
    }

    private boolean mapDone = false;
    public void start(){
        /**执行收集收据  ---by JasonMao @ 2023/2/8 15:51 */
        ThinkAsyncExecutor.execute(()->{
            this.mapReduce.getMapTask().map(dataQueue);
            this.mapDone = true;
            System.out.println("map 完成 ");
        });
        AtomicInteger integer =new AtomicInteger();
        CombinerResult<V> result = new CombinerResult<>();
        for (int i = 0; i < 5; i++) {
            ThinkAsyncExecutor.execute(()->{

                integer.incrementAndGet();
                System.out.println("开始reduce " + integer.get());
                S data = dataQueue.poll();
                while (data !=null || mapDone == false){
                    if(data!=null){
                        V reduce = this.mapReduce.getReduceTask().reduce(data);
                        this.mapReduce.getCombiner().combiner(result,reduce);
                        data = null;

                    }
                    data = dataQueue.poll();
                }

                System.out.println( dataQueue.size() +"退出 ---data --- " +data + " >>> map done =" + mapDone);
                integer.decrementAndGet();
                System.out.println("完成reduce " + integer.get());
            });
        }

        while (integer.get()  >0  || dataQueue.size() >0 ){
            System.out.println(integer.get() + ": ");
            System.out.println("任务运行未完成" + dataQueue.size());
            try{
                Thread.sleep(1000);
            }catch (Exception e){}
        }
        System.out.println("任务完成 " + result.getResult());


    }


    public static void main(String[] args) {
        final LocalMapReduce<Integer, Integer> localMapReduce = new LocalMapReduce<>();
        localMapReduce.mapTask((q)->{
            for (int i = 0; i < 99999; i++) {
                q.add(i);
            }
        });
        localMapReduce.reduceTask((x)->{
           return x ;
        });
        localMapReduce.combiner((x,y)->{
            if(x.getResult() !=null){
            }
            x.update(y);
            return x;
        });

        ThinkLocalMapReduceExecutor.getInstance(localMapReduce).start();



    }
}
