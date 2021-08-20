package com.think.common.util.security;

import com.think.common.util.StringUtil;

/**
 * @Date :2021/7/9
 * @Name :ThinkHash
 * @Description : 请输入
 */
public class ThinkHashUtil {


    public static final String simpleStrHashcode(String str ){
        if(StringUtil.isNotEmpty(str)){
            int len = str.length();
            int code = str.hashCode();
            return Integer.toString(len,36) + "_" + Integer.toString(code,36);
        }

        return "0_0";
    }


    public static void main(String[] args) {
//        System.out.println(Integer.toString(Integer.MIN_VALUE, 36));
//        System.out.println(Integer.toString(Integer.MAX_VALUE, 36));
//        System.out.println(Integer.toString(65535, 36));
//        System.out.println(Integer.valueOf("-zik0zk", 36));
//        Integer.hashCode(1)

    }


}
