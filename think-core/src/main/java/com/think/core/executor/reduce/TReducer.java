package com.think.core.executor.reduce;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 20:27
 * @description :
 */
public class TReducer<Source,Result> {


    private ILocalMapTask<Source> mapper;

    private ILocalReduceTask<Source,Result> reducer;

    private ILocalCombiner<Result> combiner;



}
