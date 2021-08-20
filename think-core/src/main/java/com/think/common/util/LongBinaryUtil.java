package com.think.common.util;


import com.think.common.util.security.Base64Util;
import com.think.core.annotations.Remark;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @Date :2021/1/25
 * @Name :LongBinaryUtil
 * @Description : 超长二进制处理类
 */
public class LongBinaryUtil {

    /**
     * 设置 byte【】中 从左 到右数 第 position为 1   setPositionTrue(new byte[1]{0} ,1) --   00000000   - 00000001
     * @param source
     * @param position
     * @return
     */
    @Remark("设置数组转成二进制位的第position位置（从右到左）的值为1。position从1开始")
    public static final byte[] setPositionTrue(byte[] source ,int position){
        if(position<1 && position > source.length*8){
            throw new RuntimeException("position取值从1开始，最大值为" + (source.length *8));
        }
        if(checkPosition(source,position)){
            return source;
        }
        int index = 1 ;
        while (position > 8){
            index ++ ;
            position -= 8 ;
        }
        byte target = source[source.length - index] ;
        target += 1<<(position -1);
        source[source.length - index] = target;
        return source;
    }


    public static final byte[] setPositionsTrue(byte[] source,int... poss ){
        for(int pos : poss){
            source = setPositionTrue(source,pos);
        }
        return source;
    }


    @Remark("检查二进制位的第position位置（从右到左）的值为1。position从1开始")
    public static boolean checkPosition(byte[] source , int position){
        if(position<1 && position > source.length*8){
            throw new RuntimeException("position取值从1开始，最大值为" + (source.length *8));
        }
        int index = 1 ;
        while (position > 8){
            index ++ ;
            position -= 8 ;
        }
        byte target = source[source.length - index] ;
        return  1== ( (target>>(position-1) ) &1  );
    }

    /**
     *  转成 BASE64 string
     * @param source
     * @return
     */
    public static String asBase64String(byte[] source ){
        return Base64.getEncoder().encodeToString(source);
    }


    /**
     * 从Base64 转回byte[]
     * @param base64Source
     * @return
     */
    public static byte[] fromBase64String(String base64Source ){
        return Base64.getDecoder().decode(base64Source);
    }


    public static byte[] initByteArray(int positionLen){
        byte[] bytes = new byte[positionLen / 8 + (positionLen % 8 == 0 ? 1 : 0)];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
        }
        return bytes;
    }

    public static final Integer[] getTruePositionAsInteger(byte[] source){
        int max = source.length *8 ;
        List<Integer> integerList= new ArrayList<>();
        for (int i = 0; i < max; i++) {
            if(checkPosition(source,i)){
                integerList.add(i+1);
            }
        }
        return integerList.toArray(new Integer[integerList.size()]);
    }


    public static final int[] getTruePositionAsInt(byte[] source){
        int max = source.length *8 ;
        List<Integer> integerList= new ArrayList<>();
        for (int i = 0; i < max; i++) {
            if(checkPosition(source,i)){
                integerList.add(i+1);
            }
        }
        int len = integerList.size();
        int[] arr = new int[len];
        for (int i = 0; i < integerList.size(); i++) {
            arr[i] = integerList.get(i) ;
        }
        return arr;
    }




    public static final String toString(byte[] bytes){
        int max = bytes.length *8 ;
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------------------------------------------------------------\n");
        for(int i=max-1;i>=0;i--){

            sb.append("\t").append(checkPosition(bytes,i+1)?"1":"0");

            if(i%8 == 0 ){
                sb.append("\n");
            }
        }
        sb.append("\n-------------------------------------------------------------------------");
        return sb.toString();
    }






}
