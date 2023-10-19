package com.think.core.security;

import lombok.extern.slf4j.Slf4j;
import com.think.common.util.security.MD5Util;
import com.think.common.util.security.SHAUtil;
import com.think.core.enums.WebSignType;
import com.think.core.security.sm.SM3Utils;
import com.think.core.security.token.ThinkSecurityToken;
import org.bouncycastle.pqc.legacy.math.linearalgebra.ByteUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class ThinkWebSignerV2 {
    /**
     * 拼接待签名字符串
     * @param requestParams
     * @param time
     * @param uri
     * @param secretKey
     * @return
     */
    public static final String buildSourceString(Map<String,String> requestParams , long time , String uri , String secretKey ){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            list.add(entry.getKey());
        }
        Collections.sort(list);
        StringBuilder sourceStr = new StringBuilder("params={");
        int index = 0 ;
        for(String key : list){
            if(index > 0){
                sourceStr.append("&");
            }
            sourceStr.append(key).append("=").append(requestParams.get(key));
            index ++ ;
        }
        sourceStr.append("}&header={").append("time=").append(time)
                .append("&uri=").append(uri)
                .append("}&secret=").append(secretKey);
        if(log.isTraceEnabled()){
            log.trace("待签名字符串 : {}" ,sourceStr.toString());
        }
        return sourceStr.toString();
    }

    private static String doSign(WebSignType webSignType,String source){
        switch (webSignType){
            case SM3: return SM3Utils.sm3(source);
            case SHA1: return SHAUtil.sha1(source);
            case SHA224: return SHAUtil.sha224(source);
            case SHA256: return SHAUtil.sha256(source);
            case SHA384: return SHAUtil.sha384(source);
            case MD5: return MD5Util.encryptMd5(source);
        }
        return SM3Utils.sm3(source);
    }

    public static final String sign(WebSignType webSignType, ThinkToken token, Map<String,String> requestParams , long time , String uri ){
        String secretKey = ThinkSecurityManager.buildSignPrimaryKey(token);
        String source = buildSourceString(requestParams, time, uri, secretKey);
        return doSign(webSignType,source);
    }


    public static final String sign(WebSignType webSignType,ThinkSecurityToken token, Map<String,String> requestParams , long time , String uri){
        String secretKey = ThinkSecurityManager.buildSignPrimaryKey(token);
        String source = buildSourceString(requestParams, time, uri, secretKey);
        return doSign(webSignType,source);
    }


    /**
     * 检验 签名是否正确
     * @param signType
     * @param source
     * @param signHexString
     * @return
     */
    public static boolean verify(WebSignType signType ,String source ,String signHexString){
        byte[] signHash = ByteUtils.fromHexString(signHexString);
        byte[] sourceHash = ByteUtils.fromHexString(doSign(signType,source));
        return ByteUtils.equals(signHash,sourceHash);
    }


}
