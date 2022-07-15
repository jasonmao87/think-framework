package com.think.common.util;

import com.think.structure.ThinkReadOnlyList;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

/**
 * @Date :2021/6/9
 * @Name :ThinkCollectionUtil
 * @Description : 请输入
 */
public class ThinkCollectionUtil {

    private static final List EMPTY_LIST = new ThinkReadOnlyList();

    public static final List emptyList(){
        return EMPTY_LIST;
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }


    public static boolean isNotEmpty(@Nullable Map<?, ?> map){
        if(isEmpty(map)){
            return false;
        }
        return map.size() > 0 ;
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.size() >0 ;
    }


    /**
     * 由于 再少量数据，和 非 多线程 stram 提供的 findAny方法，随机性 太弱 ，故 提供该方法
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> Optional<T> findAny(@Nullable Collection<T> collection){
//        for (T t : collection) {
//            if (RandomUtil.nextBoolean()) {
//                return Optional.ofNullable(t);
//            }
//        }
//        return collection.stream().findAny();
        return findAny(collection,t->true);
    }

    public static <T> Optional<T> findAny(@Nullable Collection<T> collection, Predicate<T> predicate){
        for (T t : collection) {
            if (predicate.test(t)) {
                if (RandomUtil.nextBoolean()) {
                    return Optional.ofNullable(t);
                }
            }
        }
        return collection.stream().filter(predicate).findAny();
    }


    public static  final <T>  void removeIf(@Nullable Collection<T> collection, Predicate<T> predicate){
        Iterator<T> iterator = collection.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (predicate.test(next)) {
                iterator.remove();
            }
        }

    }


    public static void main(String[] args) {
        List<String> list= new ArrayList<>();
        list.add("x");
        list.add("x");list.add("x");

        ThinkCollectionUtil.findAny(list,(t)->{

           return true;
        });



    }
}
