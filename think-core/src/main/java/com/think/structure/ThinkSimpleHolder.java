package com.think.structure;

import org.omg.CORBA.Object;

import java.util.List;
import java.util.Set;

/**
 * @Date :2021/4/28
 * @Name :ThinkSimpleHolder
 * @Description : 请输入
 */
public class ThinkSimpleHolder<T> {
    /**
     * 初始化最大容量，不可变
     */
    private final int maxSize ;


    /**
     * 数据总量
     */
    private int dataCount = 0 ;

    /**
     * 最后一次读取的 下标
     */
    private int lastIndex = -1 ;
    private T[] array;
    public ThinkSimpleHolder(int maxSize) {
        this.maxSize = maxSize;
        if(maxSize<1){
            throw new RuntimeException("最大容量不可小于1");
        }
        array = (T[]) new Object[maxSize];
    }

    public ThinkSimpleHolder(int maxSize, List<T> initList){
        this.maxSize = maxSize;
        if(maxSize<1){
            throw new RuntimeException("最大容量不可小于1");
        }
        array = (T[]) new Object[maxSize];
        if(initList!=null ){
            if(initList.size() > maxSize) {
                throw new RuntimeException("初始化数据的容量超过了最大限制");
            }

            for (T t : initList) {
                add(t);
            }
        }
    }

    public synchronized boolean add(T t){
        return addOneIfNotContains(t);
    }



    /**
     * 检查是否包含该对象
     * @param t
     * @return
     */
    public boolean contains(T t){
        return indexOf(t)>0;

    }

    /**
     * 获得 数据对象所在序列
     * @param t
     * @return
     */
    public int indexOf(T t){
        for (int i = 0; i < maxSize; i++) {
            if(array[i] == null){
                continue;
            }
            if(array[i].equals(t)){
                return i;
            }
        }
        return -1;
    }


    private synchronized boolean addOneIfNotContains(T t){
        for (int i = 0; i < maxSize; i++) {
            if(array[i] == null){
                continue;
            }
            if(array[i].equals(t)){
                return true;
            }
        }
        return justAdd(t);
    }

    private synchronized boolean justAdd(T t)  {
        if(t == null){
            return false;
        }
        if(this.dataCount ==  maxSize){
            return false;
        }
        if(contains(t)){
            return false;
        }
        for(int i =0 ; i< maxSize ;i ++){
            if (array[i] == null) {
                array[i] =t;
                dataCount +=1 ;
                return true;
            }
        }
        return false;
    }


    private synchronized void reSort(){
        int resortCount = 0 ;
        for(int i=0;i<maxSize;i ++){
            T t = this.array[i];
            if(t  ==null){

            }

        }
    }



}
