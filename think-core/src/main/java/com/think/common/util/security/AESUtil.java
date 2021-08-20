package com.think.common.util.security;

import com.think.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

@Slf4j
public class AESUtil {

    private static final int DEFAULT_SECURE_KEY_LENGTH = 16;

    public static byte[] encrypt(byte[] source, byte[] securityKey) throws Exception {
        // 获得密匙数据
        // 从原始密匙数据创建KeySpec对象
        SecretKeySpec key = new SecretKeySpec(securityKey, "AES");
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[DEFAULT_SECURE_KEY_LENGTH];
        if (source == null || source.length == 0) {
            return source;
        }
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        // 正式执行加密操作
        return cipher.doFinal(source);
    }

    public static byte[] decrypt(byte[] data, byte[] securityKey) throws Exception{
        if (data == null || data.length == 0) {
            return data;
        }
        // 从原始密匙数据创建一个KeySpec对象
        SecretKeySpec key = new SecretKeySpec(securityKey, "AES");
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[DEFAULT_SECURE_KEY_LENGTH];
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        try {
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();

            throw new Exception(e);
        }
    }



    public static String encrypt(String source, String securityKey) throws Exception{
        byte[] data = encrypt(source.getBytes(StringUtil.UTF8) ,  initKey(securityKey));
        return Base64.encodeBase64String(data);
    }

    public static String decrypt(String data, String securityKey) throws Exception{
        byte[] source =  decrypt(Base64.decodeBase64(data) , initKey(securityKey));
        return  new String(source,StringUtil.UTF8);
    }





    private static byte[] initKey(String key) throws UnsupportedEncodingException {
        byte[] keyBytes;keyBytes = key.getBytes(StringUtil.UTF8);
        byte[] keyBytes128 = new byte[DEFAULT_SECURE_KEY_LENGTH];
        System.arraycopy(
                keyBytes, 0, keyBytes128, 0, Math.min(keyBytes.length, DEFAULT_SECURE_KEY_LENGTH));
        return keyBytes128;
    }

}
