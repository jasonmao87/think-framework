package com.think.core.data.holder;

import com.think.common.util.RandomUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.data.DynamicArray;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/14 16:38
 * @description : TODO
 */
public class SimpleThinkHolder<T> implements ThinkHolder<T>{


    private Map<String,DynamicArray<ThinkHolderEntry<T>>> holderMap ;

    public SimpleThinkHolder() {
       holderMap = new HashMap<>();
    }

    @Override
    public boolean contains(String key) {
        return holderMap.containsKey(key);
    }

    @Override
    public boolean hold(String key, T v) {
        return this.hold(key,v,9);
    }

    @Override
    public boolean hold(String key, T v, int orderValue) {
        if(!contains(key)){
            this.holderMap.put(key,new DynamicArray(ThinkHolderEntry.class) );
        }
        return this.holderMap.get(key).add(new ThinkHolderEntry(v,orderValue));    }

    private T getOne(ThinkHolderEntry[] matched){
        if(matched!=null &&matched.length > 0){
            if(matched.length ==1){
                return (T) matched[0].getValueAndHit();
            }
            int index= RandomUtil.nextInt()% matched.length;
            return (T) matched[index].getValueAndHit();
        }
        return null;
    }

    @Override
    public T get(String key) {
        final DynamicArray<ThinkHolderEntry<T>> array = this.holderMap.get(key);
        if(array.size() ==0){
            return null;
        }
        int test =  RandomUtil.nextInt() % 10;
        int minOrderValue = test;
        long now =ThinkMilliSecond.currentTimeMillis();
        ThinkHolderEntry[] matched = array.findMatched(x -> {
            if (x.resultOrderValue() >= minOrderValue) {
                return now - x.getLastHit() > 3;
            }
            return false;
        });
        T one = getOne(matched);
        if(one!=null) {
            return one;
        }
        matched = array.findMatched(x -> x.resultOrderValue() >= minOrderValue);
        one = getOne(matched);
        if(one!=null) {
            return one;
        }
        matched = array.findMatched(t -> t != null);
        one = getOne(matched);
        if(one!=null) {
            return one;
        }
        return null;
    }

    @Override
    public List<T> getEntrys(String key) {
        final ThinkHolderEntry[] copyArray = holderMap.get(key).copyArray();
        final List collect = Arrays.stream(copyArray)
                .filter(t -> t != null)
                .map(ThinkHolderEntry::getValue)
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public boolean removeKey(String key) {
        return holderMap.remove(key)!=null;
    }

    @Override
    public boolean clearAll() {
        holderMap.clear();
        return holderMap.isEmpty();
    }

    @Override
    public boolean removeDataByPredicate(String key, Predicate<ThinkHolderEntry<T>> predicate) {
        return this.holderMap.get(key).remove( predicate) > 0;
    }

    @Override
    public int size() {
        return holderMap.size();
    }

    @Override
    public Set<String> getKeyset() {
        return holderMap.keySet();
    }

    //    @Override
//    public int dataSizeOfKey(String key) {
//        int index = 0 ;
//
//        return 1;
//    }
//
//    @Override
//    public int totalDataSize() {
//        return 0;
//    }



    public static void main(String[] args) {
//       SimpleThinkHolder<Integer> holder =new SimpleThinkHolder<>();
//        for (int i = 1; i <= 9; i++) {
//            holder.hold("x",i,i);
//        }
//
//
//        int[] arrs =new int[10];
//
//        for (int i = 0; i < 1000000; i++) {
//            int x = holder.get("x");
//            arrs[x] ++;
//            System.out.print(x + " ");
//            if(i%150==0 && i>0){
//                System.out.println();
//            }
//        }
//
//
//        System.out.println();
//        System.out.println("en d");
//
//        int i =0;
//        for (int arr : arrs) {
//
//            System.out.print( " "+i + "<" + arr+">");
//            i++;
//        }


    }
}
