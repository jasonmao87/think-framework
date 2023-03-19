package com.think.common.util.security;

import com.think.common.util.StringUtil;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/30 21:46
 * @description :
 */
public class ThinkOrderedHashUtil {


    public static void main(String[] args) {

        String s = "";
        for (int i = 0; i < 32; i++) {
            s+="f";
        }
        System.out.println(StringUtil.radixChange(s, 16, 35).length());
        System.out.println(StringUtil.radixChange(s, 16, 35));
        System.out.println(StringUtil.radixChange(s, 16, 36));
        System.out.println(StringUtil.radixChange(s, 16, 35).length());

    }
    /**
     *
     * @param str
     * @return
     */
    public static final String hashCode(String str){
        if(StringUtil.isNotEmpty(str)){


        }
        return "";


    }

}
