package com.think.common.util.security;

import com.think.common.util.StringUtil;

import java.util.Base64;

public class Base64Util {

    public static String decodeToString(String base64Source)  {
        try{
            return new String(Base64.getDecoder().decode(base64Source.trim()), StringUtil.UTF8);
        }catch (Exception e){
            return null;
        }
    }

    public static String encodeToString(String source){
        return Base64.getEncoder().encodeToString( source.trim().getBytes());
    }
}
