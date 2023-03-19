package com.think.core.security;

import com.think.common.util.security.AESUtil;
import com.think.common.util.security.SHAUtil;
import com.think.core.security.token.ThinkSecurityToken;
import com.think.exception.ThinkException;

/**
 * 单例
 * 框架核心 安全签名 器
 */
public class ThinkSecurityManager {

    private final static ThinkSecurityManager instance = new ThinkSecurityManager() ;

    protected ThinkSecurityManager() {
    }

    public static ThinkSecurityManager getInstance(){
        return instance;
    }

    public void setSecurityKey(String key) throws ThinkException {
        ThinkSecurityKey.setSecurityKey(key);
    }
    /**
     * 动态的获取最新的 私钥
     * @return
     */
    private String getKey(){
        return ThinkSecurityKey.getSecurityKey();
    }

    /**
     * 从JSON 解析出 token 对象
     * @param tokenJsonString
     * @return
     */
    public static ThinkToken parseJsonString(String tokenJsonString){
        if(tokenJsonString.startsWith("{")){
            return ThinkToken.parseOfJsonString(tokenJsonString);
        }else{
            return null;
        }
    }

    /**
     * 计算 签名的密钥。主要用于授权签名
     * @return
     */
    public static String buildSignPrimaryKey(ThinkToken token){
        String key = getInstance().getKey();
        String securityString = token.securityString();
        securityString = securityString+"&"+key;
        return SHAUtil.sha256(securityString);
    }


    public static String buildSignPrimaryKey(ThinkSecurityToken token){
        String key = getInstance().getKey();
        String securityString = token.getTokenJsonString();
        return SHAUtil.sha256(securityString);
    }

    /**
     * 计算 次要 签名， 主要用于 更新token
     * @param token
     * @return
     */
    public static String buildSignSecondaryKey(ThinkToken token){
        String key = getInstance().getKey();
        String securityString = token.securityString();
        securityString =  key+"&"+ securityString ;
        return SHAUtil.sha256(securityString);
    }

    public static String AESEncode(String  source){
        try{
            return AESUtil.encrypt(source,getInstance().getKey());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String AESDecode(String secretSource){
        try{
            return AESUtil.decrypt(secretSource,getInstance().getKey());
        }catch (Exception e){}
        return "";
    }



}