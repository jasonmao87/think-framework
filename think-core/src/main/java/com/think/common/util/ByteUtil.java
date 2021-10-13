package com.think.common.util;

import java.math.BigInteger;

public class ByteUtil {



    public static byte[] ofBinaryString(String binaryString){
        int length = binaryString.length();
//        if(binaryString.replaceAll("0","").replaceAll("1","").length() == 0){
//
//        }
        while (length % 8 != 0){
            length ++ ;
            binaryString = "0"+binaryString;
        }
        byte[] bytes = new byte[length/8];
        for(int i= 0 ; i < bytes.length; i++){
            int start = (i*8);
            int end = start +8 ;
            String temp = binaryString.substring(start,end);
            byte b =  Byte.valueOf(temp,2);
            bytes[i] = b;

        }

        return bytes;

    }


    public static String byteToHex(byte[] bytes){
        if(bytes == null){
            return "";
        }
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
        }
        return sb.toString().trim();
    }


    public static String byteToDecimal(byte[] bytes){
        return new BigInteger(bytes).toString();
    }
}
