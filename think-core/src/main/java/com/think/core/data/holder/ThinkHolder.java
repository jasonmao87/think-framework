package com.think.core.data.holder;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/14 15:09
 * @description : TODO
 */
public interface ThinkHolder<T> {

    boolean contains(String key);

    boolean hold(String key ,T v);

    boolean hold(String key ,T v ,int orderValue) ;

    T get(String key) ;


    List<T> getEntrys(String key);

    boolean removeKey(String key);

    boolean clearAll();

    /**
     * 有条件从指定的key中的删除袁术
     * @param key
     * @param predicate
     * @return
     */
    boolean removeDataByPredicate(String key , Predicate<ThinkHolderEntry<T>> predicate);


    int size();

//    int dataSizeOfKey(String key);
//
//    int totalDataSize();
//
    Set<String> getKeyset();

}

