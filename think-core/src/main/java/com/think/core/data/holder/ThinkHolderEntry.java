package com.think.core.data.holder;

import com.think.common.util.ThinkMilliSecond;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/14 22:11
 * @description : TODO
 */
public class ThinkHolderEntry<T> {
    private T value ;

    private int orderValue ;

    private int resultOrderValue ;

    private long lastHit = 0L;

    public ThinkHolderEntry(T value, int orderValue) {
        this.value = value;
        this.orderValue = orderValue;
        if(orderValue<1){
            orderValue =1 ;
        }
    }

    public ThinkHolderEntry(T value) {
        this.value = value;
    }

    public T getValue() {
        return getValueAndHit();
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(int orderValue) {
        if(orderValue <1 ){
            orderValue =1 ;
        }
        this.orderValue = orderValue;
    }


    public int resultOrderValue(){
        if(resultOrderValue == 0){
            resultOrderValue = orderValue%10  ;
        }
        return resultOrderValue;
    }

    public T getValueAndHit(){
        this.lastHit = ThinkMilliSecond.currentTimeMillis();
        return this.value;
    }

    public long getLastHit() {
        return lastHit;
    }
}
