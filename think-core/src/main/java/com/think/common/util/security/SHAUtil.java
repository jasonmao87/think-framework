package com.think.common.util.security;

import com.think.common.util.StringUtil;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

public class SHAUtil {

    public static String sha1(String str){
        return sha(str,1);
    }

    public static String sha224(String str){
        return sha(str,224);
    }
    /**
     * SHA-256
     * @param str
     * @return
     */
    public static String sha256(String str){
        return sha(str,256);
    }

    public static String sha384(String str){
        return sha(str,384);
    }

    public static String sha512(String str){
       return sha(str,512);
    }



    private static String sha(String str,int code){
        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-"+ code);
            messageDigest.reset();
            byte[] hash = messageDigest.digest(str.getBytes(StringUtil.UTF8));
            return Hex.encodeHexString(hash);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
