package com.think.common.util;

public class BinaryUtil {


    public byte[] getByteArrayOfBinaryString(String s ){
        while (s.length() %8 !=0){
            s = "0"+s;
        }
        int len = s.length() /8;
        byte[] bts = new byte[len];
        for(int i= 0 ; i < len; i++){
            int start = (i*8);
            int end = start +8 ;
            String temp = s.substring(start,end);
            byte b =  Byte.valueOf(temp,2);
            bts[i] = b;
        }

        return bts;

    }


    public static boolean checkPositionIsTrue(byte[] bytes , int position){
        if(position>(bytes.length * 8)){
            return false;
        }
        int len = bytes.length;
        int index = 1  ;
        byte target = bytes[len -index];
        while (position > 8){
            position -= 8 ;
            index ++ ;
        }
        return checkPositionIsTrue(target,position);
    }

    /**
     * postion 位置 0 - 31，从 右到左
     * @param v
     * @param postion
     * @return
     */
    public static boolean checkPositionIsTrue(int v, int postion){
        return  1== ( (v>>(postion ) ) &1  );
    }


    /**
     * 设置从右到左 第postion位置（0-31）为1
     * @param v
     * @param postion
     * @return
     */
    public static int setPositionTrue(int v, int postion){
        if(checkPositionIsTrue(v,postion)){
            return v;
        }
        v += 1<<(postion);
        return v;
    }

    /**
     * 设置从右到做第postion位的 值 为 0
     * @param v
     * @param postion
     * @return
     */
    public static int setPostionFalse(int v ,int postion){
        if(checkPositionIsTrue(v,postion)){
            v -= 1<<(postion);
        }
        return v;
    }


    public static String display(int v){

        StringBuilder sb = new StringBuilder();
        for(int i=32;i>=0;i--){
            sb.append("\t"+i);
        }
        sb.append("\n");
        for(int i=32;i>=0;i--){
            sb.append("\t").append(checkPositionIsTrue(v,i)?"1":"0");
        }
        sb.append("\n");
        return sb.toString();

    }


    public static String displayJson(int v ,int len ,String[] indexArray){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i= 0;i<len;i++){
            if(i>0){
                sb.append(",");
            }
            sb.append("'").append(i ).append(" - ").append(indexArray[i]).append("':").append(checkPositionIsTrue(v,i));

        }
        sb.append("}");
        return sb.toString();
    }

}
