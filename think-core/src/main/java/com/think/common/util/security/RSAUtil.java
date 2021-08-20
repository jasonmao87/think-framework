package com.think.common.util.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.PSource;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public class RSAUtil {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";                     //签名算法
    private static final String KEY_ALGORITHM = "RSA";        //加密算法RSA

    /**
     * 公钥验签
     *
     * @param text      原字符串
     * @param sign      签名结果
     * @param publicKey 公钥
     * @return 验签结果
     */
    public static boolean verify(String text, String sign, String publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            PublicKey key = KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(publicKey)));
            signature.initVerify(key);
            signature.update(text.getBytes());
            return signature.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            log.error("验签失败:text={},sign={}", text, sign, e);
        }
        return false;
    }

    /**
     * 签名字符串
     *
     * @param text       需要签名的字符串
     * @param privateKey 私钥(BASE64编码)
     * @return 签名结果(BASE64编码)
     */
    public static String sign(String text, String privateKey) {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateK);
            signature.update(text.getBytes());
            byte[] result = signature.sign();
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            if(log.isErrorEnabled()){
                log.error("签名失败,text={}",text, e);
            }
        }
        return null;
    }

//    /**
//     * RSA公钥加密
//     *
//     *            加密字符串
//     * @param publicKey
//     *            公钥
//     * @return 密文
//     * @throws Exception
//     *             加密过程中的异常信息
//     */
//    public static String encrypt( String message, String publicKey ) {
//        //base64编码的公钥
//        try {
//            byte[] decoded = Base64.decodeBase64(publicKey);
//            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
//            //RSA加密
//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//            String encryptMessage = Base64.encodeBase64String(cipher.doFinal(message.getBytes(StringUtil.UTF8)));
//            return encryptMessage;
//        } catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException
//                | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
//            if(log.isErrorEnabled()) {
//                log.error("使用公钥对数据加密异常", e);
//            }
//        }
//        return null;
//    }
//
//    /**
//     * RSA私钥解密
//     *
//     *            加密字符串
//     * @param privateKey
//     *            私钥
//     * @return 铭文
//     * @throws Exception
//     *             解密过程中的异常信息
//     */
//    public static String decrypt(String message, String privateKey) {
//        try {
//            //转义
////            message=URLDecoder.decode(message,StringUtil.UTF8);
//            //64位解码加密后的字符串
//            byte[] inputByte = Base64.decodeBase64(message.getBytes(StringUtil.UTF8));
//            //base64编码的私钥
//            byte[] decoded = Base64.decodeBase64(privateKey);
//            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
//            //RSA解密
//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.DECRYPT_MODE, priKey);
//            String decryptMessage = new String(cipher.doFinal(inputByte));
//            return decryptMessage;
//        } catch (InvalidKeyException | UnsupportedEncodingException | InvalidKeySpecException | NoSuchAlgorithmException
//                | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
//            if(log.isErrorEnabled()) {
//                log.error("使用私钥对数据解密异常", e);
//            }
//        }
//        return null;
//    }


//    public static void main(String[] args) {
//        String pubKey="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAL+d7qm9J8qoHVJOiFDPaWAMqY/AypV5\n" +
//                "jSSyHSh8GxLUQSax+Aee/dQ4bHikj0zna2VOJ6S2elQ0xQgQYRC6IccCAwEAAQ==".replaceAll("\n","");
//        String key="MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAv53uqb0nyqgdUk6I\n" +
//                "UM9pYAypj8DKlXmNJLIdKHwbEtRBJrH4B5791DhseKSPTOdrZU4npLZ6VDTFCBBh\n" +
//                "ELohxwIDAQABAkB8aRdHP9gxHVwET5b0GObLBQ2mlz8xM71vYPHLkmClpOgTp8z0\n" +
//                "YjU7pNnlyTwA4hit7ZAYmpWjggGx7EgB5YaBAiEA6zs0qoTn3lBdWHhF4BubRiSO\n" +
//                "qLAv5mShg0u/xPUxYgcCIQDQiO/hNbxUW3GXRgOM8OX0gjXFOYRCmEM9F2hw2WZS\n" +
//                "QQIgfpGxydqPZGh7gYHdnzNbfgdnl06Nx3r4CMx2WATSWHsCIQCMuXw3nI2k17jF\n" +
//                "/udyezaTVDN5DqFkV2A4n81JKkxUgQIhALvIv7xsmB6pfOiDYLsmUpjcYAngNfHK\n" +
//                "+heIwtAd01P7".replaceAll("\n","");
//        String str = "https://dss1.bdstatic.com/dadad";
//        long start = System.currentTimeMillis();
//        String sign = sign(str,key);
//        System.out.println("签名用时"+(System.currentTimeMillis() - start) );
//        System.out.println(sign);
//        start = System.currentTimeMillis();
//        System.out.println(verify("a",sign,pubKey));
//        System.out.println("验证用时"+(System.currentTimeMillis() - start) );
//        start = System.currentTimeMillis();
////        String encrptStr = encrypt("+*+++-+-+-++hehe贺贺 样？？？？}",pubKey);
////        System.out.println("加密用时"+(System.currentTimeMillis() - start) );
////        start = System.currentTimeMillis();
////        String decodeStr = decrypt(encrptStr,key);
////        System.out.println("解密用时"+(System.currentTimeMillis() - start) );
////        start = System.currentTimeMillis();
////
////        System.out.println(encrptStr);
////        System.out.println(decodeStr);
//
//
//    }
}
