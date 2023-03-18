package com.think.core.executor.reduce;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 14:47
 * @description : 用于逐个处理收集好的数据
 */
public interface ILocalReduceTask<S,V> {

    V reduce(S t);
}
