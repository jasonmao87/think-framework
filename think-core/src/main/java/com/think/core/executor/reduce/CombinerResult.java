package com.think.core.executor.reduce;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/8 17:51
 * @description :
 */
public class CombinerResult<T> {

    private boolean success ;

    private T t ;

    public void update(T t){
        this.t = t;
    }

    public T getResult() {
        return t;
    }


    public CombinerResult() {
    }
}
