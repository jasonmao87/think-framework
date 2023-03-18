package com.think.core.data;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/14 17:30
 * @description : TODO
 */
public class DynamicArray<T>   {

    private int size ;
    private int freeSzie ;
    private T[] values ;

    private Class<T> targetClass;
    public DynamicArray( Class<T> targetClass ,int initSize) {
        this.targetClass = targetClass;
        values = (T[]) Array.newInstance(targetClass,initSize );
        size = 0 ;
        freeSzie = initSize;
    }

    public DynamicArray( Class<T> targetClass) {
        this.targetClass = targetClass;
        int initSize = 7;
        values = (T[]) Array.newInstance(targetClass,initSize );
        size = 0;
        freeSzie = 7;

    }


    public boolean containsValue(T value){
        for (int i = 0; i < values.length; i++) {
            if (values[i]!=null) {
                if (values[i].equals(values)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int remove(Predicate<T> predicate){
        int count =0;
        for (int i = 0; i < values.length; i++) {
            T v = values[i];
            if (predicate.test(v)) {
                if (remove(i)) {
                    count++;
                }
            }
        }
        return count;
    }

    private Stream<T> stream(){
        return Arrays.stream(values).filter(t->t!=null);
    }



    public Optional<T> findFirst(Predicate<T> predicate){
        return stream().filter(predicate).findFirst();
    }

    public Optional<T> findAny(Predicate<T> predicate){
        return stream().filter(predicate).findAny();
    }

    public long count(Predicate<T> predicate){
        return stream().filter(predicate).count();
    }

    public T[] findMatched(Predicate<T> predicate){
        Long count = stream().filter(predicate).count();
        T[] arr = (T[]) Array.newInstance(targetClass,count.intValue());
        return (T[]) stream().filter(predicate).collect(Collectors.toList()).toArray(arr);
     }


     public T[] copyArray(){
        int index =0 ;
        T[] arr =  (T[]) Array.newInstance(targetClass,size);
         for (T value : values) {
             if(value!=null){
                 arr[index] =value;
                 index++;
             }
         }
         return arr;
     }

    public synchronized boolean remove(int index){
        if (values[index] !=null) {
            values[index] = null;
            this.onRemove();
            return true;
        }
        return false;
    }

    public synchronized boolean add(T value ){
        if(containsValue(value)){
            return false;
        }
        if(freeSzie < 2 ){
            this.expansion();
        }
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                values[i] = value;
                this.onAdd();
                return true;
            }
        }
        return false;
    }


    public int size() {
        return size;
    }

    /**
     * 扩容
     */
    private void expansion(){
        int size = 7 ;
        freeSzie += size;
        T[] newArray;
        newArray = (T[]) Array.newInstance(targetClass,values.length +size);
        int index = 0 ;
        for (T value : values) {
            newArray[index] = value;
            index++;
        }
        values = (T[]) newArray;
    }

    private void onAdd(){
        freeSzie-- ;
        size ++ ;
    }

    private void onRemove(){
        freeSzie++ ;
        size -- ;
    }
    private void onClear(){
        size = 0;
        freeSzie = values.length;
    }


}
