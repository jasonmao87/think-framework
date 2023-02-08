package com.think.core.executor.reduce;

import java.util.Queue;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 15:39
 * @description : 用于收集待处理数据
 */
public interface ILocalMapTask<D> {
    void map(Queue<D> dataCollectionQueue);
}
