package com.think.core.executor.reduce;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 14:53
 * @description : 用于整合 处理完的数据
 */
public interface ILocalCombiner<V> {

    CombinerResult<V> combiner(CombinerResult<V> result,V data);


}
