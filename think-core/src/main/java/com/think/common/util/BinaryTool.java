package com.think.common.util;

import com.think.exception.ThinkNotSupportException;

public class BinaryTool {

    /**
     * 检查 二进制表示下 第 posIndex 位是否是 TRUE
     * @param v
     * @param posIndex
     * @return
     */
    public static boolean checkPositionIsTrue(long v, int posIndex){
        return  1== ( (v>>(posIndex ) ) &1  );
    }

    /**
     * 使在指定长度的占位中，让文字居中。
     * @param v
     * @param len
     * @return
     */
    private static final String _formatTextAlignCenter(String v , int len){
        v =v!=null?v.trim():"";
        int length = v.length();
        if(length < len){
            int s = len -length ;
            StringBuilder sb = new StringBuilder("");
            sb.append(v);
            int  index = 0 ;
            while (s>0){
                s --;
                index++ ;
                if(index%2 ==0){
                    sb.append(" ");
                }else{
                    sb.insert(0," ");
                }
            }
            return sb.toString();
        }
        return v;
    }


    /**
     * 横向 表格打印的 输出
     * @param number
     * @return
     * @throws ThinkNotSupportException
     */
    public static final String toHorizontalString(Number number) throws ThinkNotSupportException{
        boolean[] booleanArray = toBooleanArray(number);
        StringBuilder sb= new StringBuilder("");
        sb.append("\033[1;31m INT:: ")
                .append(number.longValue()).append(" \033[0m ")
                .append("[ HEX:: ").append(StringUtil.toHexString(number))
                .append("; Binary:: ").append(Long.toBinaryString(number.longValue()))
                .append("]\n");
        for (int i = booleanArray.length; i >0 ; i--) {
            int index = i-1 ;
            if (booleanArray[index]) {
                sb.append("\033[1;43m");
            }else{
                sb.append("\033[30;47m");
            }
            sb.append(_formatTextAlignCenter(index + "",4))
                    .append("\033[0m");
        }
        sb.append("\n");
        for (int i = booleanArray.length; i >0 ; i--) {
            int index = i-1 ;
            if (booleanArray[index]) {
                sb.append("\033[1;31;46m");
                sb.append(_formatTextAlignCenter("1",4));
            }else{
                sb.append("\033[1;46m");
                sb.append(_formatTextAlignCenter("0",4));
            }
            sb.append("\033[0m");
        }

        return sb.toString();
    }


    private static final boolean[] toBooleanArray(Number value) throws ThinkNotSupportException{
        boolean[] array ;
        if (value instanceof Long) {
            array = new boolean[64];
        }else if(value instanceof Integer){
            array = new boolean[32];
        }else if (value instanceof Short) {
            array = new boolean[16];
        }else if (value instanceof Byte) {
            array = new boolean[8];
        }else {
            throw new ThinkNotSupportException("不支持该数据类型的转换 : "+value.getClass() );
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = checkPositionIsTrue(value.longValue(),i);
        }
        return array;
    }

    private static final boolean[] toBooleanArrayWithoutTypeCheck(Number value) throws ThinkNotSupportException{
        boolean[] array ;
        if (value instanceof Long) {
            array = new boolean[64];
        }else if(value instanceof Integer){
            array = new boolean[32];
        }else if (value instanceof Short) {
            array = new boolean[16];
        }else if (value instanceof Byte) {
            array = new boolean[8];
        }else {
            throw new ThinkNotSupportException("不支持该数据类型的转换 : "+value.getClass() );
        }
        for (int i = 0; i < array.length; i++) {
            array[i] = checkPositionIsTrue(value.longValue(),i);
        }
        return array;
    }


    public static final Number valueOfBooleanArray(boolean[] array){
        long x = 0 ;
        for (int i = 0; i < array.length; i++) {
            x= (x | (array[i]?1:0)<< i);
        }
        return x;
    }


    public static final Number valueOfBinaryString(String binary){
        return Long.valueOf(binary,2);
    }
}

