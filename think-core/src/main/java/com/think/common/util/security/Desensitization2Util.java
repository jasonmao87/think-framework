package com.think.common.util.security;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 数据脱敏 / 还原工具类
 */
public class Desensitization2Util {

    private String[] dicSource = {};


    /**
     * 数据脱敏
     * @param source
     * @return
     */
    public static final String encode(String source){
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            c += 36;
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 数据脱敏  忽略 字符  ，仅用于 like 查询时候 ！
     * @param source
     * @param ignore
     * @return
     */
    public static final String encodeWithIgnore(String source ,char ignore){
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            if(c != ignore){
                c += 36;
                sb.append(c);
            }else{
                sb.append(c);
            }

        }
        return sb.toString();
    }


    /**
     * 脱敏数据还原
     * @param source
     * @return
     */
    public static final String decode(String source){
        StringBuilder sb = new StringBuilder("");

        for(char c : source.toCharArray()){

            c -= 36;
            sb.append(c);

        }
        return sb.toString();

    }

    public static void main(String[] args) {

        for (int i = 58; i <96 ; i++) {
            if(i<65 || i > 90) {
                System.out.print((char) i);
            }
        }
        System.out.println("");


        String str  = ":;<=>?@[\\]^_1234567890qwertyuiopasdfghjklzxcvbnm";//"QWERTYUIOPASDFGHJKLZXCVBNM";
        System.out.println(str.length());
        List<String> list = new ArrayList<>();
        String[] x = {"",""};

        for (char c : str.toCharArray()) {
            if(!list.contains(c+"")){
                list.add(c+"");
            }
        }
        Collections.sort(list);
        System.out.println("{");
        list.stream().forEach(t->{
            System.out.print( "'"+t + "',");
        });
        System.out.println("}");
        System.out.println(list.size());



        int num = 55;
        while (num>48){

            final int i = num % 48;
            System.out.print(i + " ");
            num = num/48;
        }
        System.out.print(num );




    }



}
