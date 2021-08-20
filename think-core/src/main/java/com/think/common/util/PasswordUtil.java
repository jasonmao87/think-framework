package com.think.common.util;

import com.think.common.result.ThinkResult;
import com.think.common.util.security.Base64Util;
import com.think.common.util.security.MD5Util;
import com.think.common.util.security.SHAUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PasswordUtil {

    public static final int encodeTypeMD5 = 0 ;
    public static final int encodeTypeSHA1 = 1 ;
    public static final int encodeTypeSHA256 = 2 ;

    private static int encodeType = encodeTypeSHA256;

    public static final void encodeType(int type){
        if(type >=0 && type<3) {
            encodeType = type;
            if(log.isDebugEnabled()){
                log.debug("设置PASSWORD UTIL编码方式为 {}"  , typeName(type)  );
            }
        }else{
            if(log.isWarnEnabled()){
                log.warn("无效的type值{}，当前PASSWORD UTIL编码方式为 {}",type,typeName(type));
            }

        }
    }

    /**
     * Password 编码
     * @param userId          用户id
     * @param sourcePassword  密码明文
     * @param randomStr       随机加盐字符串
     * @return
     */
    public static final String encodePassword(String userId ,String sourcePassword ,String randomStr){
        String sourceString = userId+randomStr+sourcePassword;
        switch (encodeType){
            case encodeTypeMD5 :{
                return MD5Util.encryptMd5(sourceString);
            }
            case encodeTypeSHA1:{
                return SHAUtil.sha1(sourceString);
            }
            case encodeTypeSHA256:{
                return SHAUtil.sha256(sourceString);
            }
        }
        if(log.isWarnEnabled()){
            log.warn("未找到正确的编码方式，默认返回明文");
        }
        return sourcePassword;
    }


    /**
     * 密码强度[0 - 5]
     *  密码长度小于 6 直接是 0
     *  包含小写字母、大写字母、数字、特殊符号 分别占 1的值
     * @param sourcePassword
     * @return
     */
    public static PasswordCheckInfo checkInfo(String sourcePassword){
        PasswordCheckInfo checkInfo = new PasswordCheckInfo();
                sourcePassword = sourcePassword.trim();
        if(sourcePassword.length() < 6){
            checkInfo.setLenToShort(true);
            return checkInfo;
        }
        if(sourcePassword.matches("^.*[0-9].*")){
            if(log.isDebugEnabled()){
                log.debug("{}>包含数字" ,sourcePassword);
            }
            checkInfo.setContainsNumber(true);
        }else{
            checkInfo.setContainsNumber(false);
        }
        if(sourcePassword.matches("^.*[A-Z].*")){
            if(log.isDebugEnabled()){
                log.debug("{}>包含大写字母" ,sourcePassword);
            }
            checkInfo.setContainsUpperCase(true);

        }else {
            checkInfo.setContainsUpperCase(false);
        }
        if(sourcePassword.matches("^.*[a-z].*")){
            if(log.isDebugEnabled()){
                log.debug("{}>包含小写字母" ,sourcePassword);
            }
            checkInfo.setContainsLowerCase(true);
        }else {
            checkInfo.setContainsLowerCase(false);
        }
        if(sourcePassword.matches("^.*[@#_/\\+\\-\\.:].*")){
            if(log.isDebugEnabled()){
                log.debug("{}>包含特殊字符" ,sourcePassword);
            }
            checkInfo.setContainsSymbol(true);
        }else {
            checkInfo.setContainsSymbol(false);
        }
        return checkInfo;
    }

    private static final String typeName(int type){
        switch (type){
            case encodeTypeMD5 : {return "MD5";}
            case encodeTypeSHA1 : {return "SHA1";}
            case encodeTypeSHA256 : {return "SHA256";}
        }
        return "UNKNOW";
    }


}


class PasswordCheckInfo{

    private Boolean lenToShort = null;

    private Boolean containsUpperCase = null ;

    private Boolean containsLowerCase =null;

    private Boolean containsNumber =null;

    private Boolean containsSymbol =null;

    protected PasswordCheckInfo() {
    }

    public Boolean getLenToShort() {
        return lenToShort;
    }

    public void setLenToShort(Boolean lenToShort) {
        this.lenToShort = lenToShort;
    }

    public Boolean getContainsUpperCase() {
        return containsUpperCase;
    }

    public void setContainsUpperCase(Boolean containsUpperCase) {
        this.containsUpperCase = containsUpperCase;
    }

    public Boolean getContainsLowerCase() {
        return containsLowerCase;
    }

    public void setContainsLowerCase(Boolean containsLowerCase) {
        this.containsLowerCase = containsLowerCase;
    }

    public Boolean getContainsNumber() {
        return containsNumber;
    }

    public void setContainsNumber(Boolean containsNumber) {
        this.containsNumber = containsNumber;
    }

    public Boolean getContainsSymbol() {
        return containsSymbol;
    }

    public void setContainsSymbol(Boolean containsSymbol) {
        this.containsSymbol = containsSymbol;
    }

    public int getSafeLevel() {
        int i = 0 ;
        if(is(this.containsNumber)){
            i++;
        }
        if(is(this.containsSymbol)){
            i++;
        }
        if(is(this.containsLowerCase)){
            i++;
        }
        if(is(this.containsUpperCase)){
            i++;
        }
        return i;
    }


    private boolean is(Boolean b){
        return b!=null?b:false;
    }
}
