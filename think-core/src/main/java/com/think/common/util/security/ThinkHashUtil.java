package com.think.common.util.security;

import com.think.common.util.StringUtil;

/**
 * @Date :2021/7/9
 * @Name :ThinkHash
 * @Description : 请输入
 */
public class ThinkHashUtil {

    public static final int intHashFromZeroToN(String str,int n){
        int hash = str.hashCode();
        if(hash < 0){
            hash =-hash;
        }
        return hash% n;
    }


    public static final String simpleStrHashcode(String str ){
        if(StringUtil.isNotEmpty(str)){
            int len = str.length();
            int code = str.hashCode();
            if(code <0){
                code = Integer.MAX_VALUE + code;
            }
            String pre = Integer.toString(code,36);
            String end = Integer.toString(len,36);
            StringBuilder strHash = new StringBuilder("");
            int size = pre.length();
            while (size < 6){
                size ++ ;
                strHash.append("0");
            }
            strHash.append(pre);
            size = end.length();
            while (size<6){
                size++ ;
                strHash.append("0");
            }
            strHash.append(end);

            return strHash.toString();
        }
        return "000000_000000".intern();
    }




}
