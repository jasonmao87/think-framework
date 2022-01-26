package com.think.common.util;

import com.think.exception.ThinkDataVerificationException;
import org.apache.commons.math3.random.StableRandomGenerator;

import java.util.Collection;
import java.util.List;

/**
 * @Date :2021/9/26
 * @Name :TVerification
 * @Description : Think 校验器
 */
public class TVerification<T extends Object>{
    //verification

    private T t ;

    public static final <T> TVerification valueOf(T  t){
        Object x = t ;



        return new TVerification(t);
    }
    private TVerification(T assertData) {
        this.t = assertData;
//        if(assertData !=null) {
//            if (assertData.getClass() == long.class) {
//                t = (T) Long.valueOf((Long)assertData);
//            }else if(assertData.getClass() == int.class){
//                t = (T) Integer.valueOf((Integer) assertData);
//            }else if(assertData.getClass() == double.class){
//                t = (T) Double.valueOf((Double) assertData);
//            }else if(assertData.getClass() == boolean.class){
//                t = (T) Boolean.valueOf((Boolean) assertData);
//            }
//        }
//
//        if(t!=null) {
//            this.t = assertData;
//        }
    }

    public T getData() {
        return t;
    }

    /**
     * 校验数据对象是否 targetClass 类型 ，当不符合时候。抛出异常
     * @param targetClass
     * @return
     */
    public final TVerification<T> throwIfNotInstanceOfType(Class targetClass){
        throwIfNull();
        if (!targetClass.isInstance(t)) {
            errThrow("校验对象并非" + targetClass.getCanonicalName() +"类型");
        }
        return this;
    }

    public final  TVerification<T> throwIfNoInstanceOfString(){
        return throwIfNotInstanceOfType(String.class);
    }

    public final  TVerification<T> throwIfNoInstanceOfLong(){
        return throwIfNotInstanceOfType(Long.class);
    }

    public final  TVerification<T> throwIfNoInstanceOfBoolean(){
        return throwIfNotInstanceOfType(Boolean.class);
    }

    public final  TVerification<T> throwIfNoInstanceOfInteger(){
        return throwIfNotInstanceOfType(Integer.class);
    }

    public final  TVerification<T> throwIfNoInstanceOfDouble(){
        return throwIfNotInstanceOfType(Double.class);
    }

    public final TVerification<T> throwIfCollectionIsEmpty(String message){
        throwIfNull(message);
        T data = getData();
        Collection collection = (Collection) data;
        if (collection.isEmpty()) {
            errThrow(message);
        }
        return this;
    }

    public final TVerification<T> throwIfCollectionIsEmpty(){
        return throwIfCollectionIsEmpty("容器不存在任何数据");
    }



//    private boolean isThrowExceptionHappen(){
//        try{
//
//        }catch (Exception e){
//            return true;
//        }
//        return false;
//    }
//
//    public final boolean isNull(){
//        return t==null;
//    }
//
//    public final boolean isTrue(){
//        try{
//            this.throwIfFalse();
//            return  true;
//        }catch (Exception e){
//            return false;
//        }
//    }







    /**
     * 当 值为 null 时候 ，抛出异常
     * @param errMsg
     * @return
     */
    public final TVerification<T> throwIfNull(String errMsg){
        if(t == null){
            errThrow(errMsg);
        }
        return this;
    }

    public final TVerification<T> throwIfNull(){
        return throwIfNull("对象为NULL");
    }

    public final TVerification<T> throwIfStringIsEmpty(String errMsg){
        this.throwIfNoInstanceOfString();
        if (StringUtil.isEmpty((String) t)) {
            errThrow(errMsg);
        }
        return this;
    }

    public  final TVerification<T> throwIfStringIsEmpty(){
        return this.throwIfStringIsEmpty("字符为空");
    }

    public final TVerification<T> throwIfFalse(String errMsg){
        this.throwIfNoInstanceOfBoolean();
        Boolean b = (Boolean) t;
        if(b.booleanValue() !=true){
            errThrow(errMsg);
        }
        return this;
    }

    public final TVerification<T> throwIfFalse(){
        return this.throwIfFalse("对象值为假");
    }

    public final TVerification<T> throwIfTrue(String errMsg){
        this.throwIfNoInstanceOfBoolean();
        Boolean b = (Boolean) t;
        if(b.booleanValue() ==true){
            errThrow(errMsg);
        }
        return this;
    }

    public final TVerification<T> throwIfTrue(){
        return this.throwIfTrue("对象值为真");
    }


    private void errThrow(String errMsg){
        throw new ThinkDataVerificationException(errMsg);
    }

}
