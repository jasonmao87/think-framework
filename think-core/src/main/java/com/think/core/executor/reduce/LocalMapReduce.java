package com.think.core.executor.reduce;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 15:42
 * @description :
 */
public class LocalMapReduce<S,V> {

    private ILocalMapTask<S> mapTask;

    private ILocalReduceTask<S,V> reduceTask;

    private ILocalCombiner<V> combiner;


    public LocalMapReduce() {
    }

    public void mapTask(ILocalMapTask<S> task){
        this.mapTask = task;
    }
    public void reduceTask(ILocalReduceTask<S,V> task){
        this.reduceTask = task;
    }

    public void combiner(ILocalCombiner<V> combiner){
        this.combiner = combiner;
    }
    public LocalMapReduce(ILocalMapTask mapTask, ILocalReduceTask reduceTask, ILocalCombiner combiner) {
        this.mapTask = mapTask;
        this.reduceTask = reduceTask;
        this.combiner = combiner;
    }


    public ILocalCombiner<V> getCombiner() {
        return combiner;
    }

    public ILocalMapTask<S> getMapTask() {
        return mapTask;
    }

    public ILocalReduceTask<S,V> getReduceTask() {
        return reduceTask;
    }
}
