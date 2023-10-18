package com.think.core.security;

import com.think.common.util.StringUtil;
import com.think.common.util.security.SHAUtil;
import com.think.core.security.token.ThinkSecurityToken;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class ThinkWebSecuritySigner {

    /**
     * 拼装待签名字符串
     * 格式 ：   params={请求参数字符串}&header={header字符串}&secret=token的通信签名密钥
     *      其中 ： 请求参数字符串 =  param1=paramValue1&param2=paramValue2 ..... 字典序排序
     *              header字符串 = time=header中传递的time值&uri=请求的URI地址（不包含域名和端口信息，如/user/get 必须是/开头 ）
     *
     * @param requestParams
     * @param time
     * @param uri
     * @param secretKey
     * @return
     */
    private static final String buildSourceString(Map<String,String> requestParams , long time , String uri , String secretKey ){
        List<String> list = new ArrayList();
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
        sourceStr.append("}&header={")
                .append("time=").append(time)
                .append("&uri=").append(uri)
                .append("}&secret=")
                .append(secretKey);
        if(log.isDebugEnabled()){
            log.debug("待签名字符串 : {}" ,sourceStr.toString());
        }
        return sourceStr.toString();
    }

    /**
     * 直接SHA256 签名
     * @param token
     * @param requestParams
     * @param time
     * @param uri
     * @return
     */
    public static final String sign( ThinkToken token,Map<String,String> requestParams ,long time ,String uri ){
        String secretKey = ThinkSecurityManager.buildSignPrimaryKey(token);
        return SHAUtil.sha256(buildSourceString(requestParams,time,uri,secretKey));
    }


    public static final String sign(ThinkSecurityToken token,Map<String,String> requestParams ,long time ,String uri){
        String secretKey = ThinkSecurityManager.buildSignPrimaryKey(token);
        return SHAUtil.sha256(buildSourceString(requestParams,time,uri,secretKey));

    }

    /**
     * 比对 签名
     * @param token
     * @param requestParams
     * @param time
     * @param uri
     * @param signStr
     * @return
     */
    public static final boolean checkSign( ThinkToken token ,Map<String,String> requestParams ,long time ,String uri ,String signStr){
        try {
            if (log.isDebugEnabled()) {
                log.debug("开始检查签名.....");
            }
            if(uri.contains("//")){
                uri = uri.replaceAll("//","/");
            }
            if(uri.startsWith("/") == false){
                uri = "/" + uri;
            }
            if (token == null) {
                if (log.isDebugEnabled()) {
                    log.debug("TOKEN 缺失，无法进行签名比对校验！");
                }
                return false;
            }
            if (StringUtil.isEmpty(signStr)) {
                if (log.isDebugEnabled()) {
                    log.debug("缺少可比对得签名原文，无法进行签名比对校验");
                }
            }
            String signReal = sign(token, requestParams, time, uri);
            if (log.isDebugEnabled()) {
                log.debug("准确的签名值= {}",signReal);
                log.debug("当前的签名之= {}",signStr);

            }
            return signReal.equals(signStr);
        }catch (Exception e){

            log.error(" 签名检查异常:" ,e );
            return false;
        }
    }


    public static final boolean checkSign( ThinkSecurityToken token ,Map<String,String> requestParams ,long time ,String uri ,String signStr){
        try {
            if(uri.contains("//")){
                uri = uri.replaceAll("//","/");
            }
            if(uri.startsWith("/") == false){
                uri = "/" + uri;
            }
            if (token == null) {
                if (log.isDebugEnabled()) {
                    log.debug("TOKEN 缺失，无法进行签名比对校验！");
                }
                return false;
            }
            if (StringUtil.isEmpty(signStr)) {
                if (log.isDebugEnabled()) {
                    log.debug("缺少可比对得签名原文，无法进行签名比对校验");
                }
            }
            String signReal = sign(token, requestParams, time, uri);
            if (log.isDebugEnabled()) {
                log.debug("准确的签名值= {}",signReal);
                log.debug("当前的签名之= {}",signStr);

            }
            return signReal.equals(signStr);
        }catch (Exception e){
            return false;
        }
    }
}
