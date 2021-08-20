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

    public static final boolean isITEqual(Number x ,Number y){
        // TODO: /.///////////////// 
        return x.toString().equals(y.toString());

//        return x.doubleValue() == y.doubleValue();
        //1.0D/3 ,1.0f/3
    }
//
//    public static final boolean isItEqual(Long x ,Long y){
//        return x.longValue() == y.longValue();
//    }


//
//    public static void main(String[] args) {
//
//        long max = Long.MAX_VALUE;
//        Long id = new Long(max);
//        Long z = new Long(max);
//        long x = max-1 +1 ;
//        long y = max;
//        System.out.println(x == id);
//        System.out.println( y == x );
//        System.out.println(y == id );
//        System.out.println(z ==id );
//        System.out.println(isITEqual(id ,z ));
//        System.out.println(Double.MAX_VALUE);
//        System.out.println(Long.MAX_VALUE);
//        System.out.println( (Double.MAX_VALUE - Long.MAX_VALUE) + ">");
//        System.out.println(isITEqual(3.14F,3.14D));
//        System.out.println(  isITEqual(1.0D/3 ,1.0f/3));
//    }

}
