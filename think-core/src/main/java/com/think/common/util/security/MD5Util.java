package com.think.common.util.security;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.Charset;
import java.security.MessageDigest;

public class MD5Util {
    public static String encryptMd5(String source) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(source.getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            String result = Hex.encodeHexString(resultByte);
            return result;
        }catch (Exception e){}
        return null;
    }

}
