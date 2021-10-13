package com.think.common.util;

/**
 * @Date :2021/3/12
 * @Name :CompareUtil
 * @Description :
 *  比较值的UTIL 类 ，防止 long value ==  Long value 等情况出现 思维理解上的误差
 *      如 3.14F ==3.14D  is false
 *
 *
 */
public class NumberCompareUtil {

    public static final boolean isItEqual(Number x ,Number y){
        // TODO: /./////////////////
        return x.toString().equals(y.toString());
    }

}
