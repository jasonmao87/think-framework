package com.think.common.util.security;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/10/24 15:03
 * @description :
 */
public class StrCompressionUtil {


    public static void main(String[] args) {



        String source = "abcdefghijklimz中国";
        System.out.println(source.getBytes().length);
        for (char c : source.toCharArray()) {
            System.out.println((int) c );
        }

        for (byte aByte : source.getBytes()) {
            System.out.print(aByte);
            System.out.print("-");
        }


        System.out.println();
        System.out.println(Byte.MAX_VALUE);

        char x = 0 ;
        System.out.println((byte)x);
    }
}
