package com.think.core.security;

import com.think.common.util.StringUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.common.util.security.AESUtil;
import com.think.core.annotations.Remark;
import com.think.core.security.token.ThinkSecurityToken;
import com.think.core.security.token.ThinkSecurityTokenUtil;
import com.think.exception.ThinkRuntimeException;
import com.think.moudles.auth.UserAccountModel;
import com.think.web.util.WebUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/22 23:41
 * @description : TODO
 */
@Slf4j
public class WebSecurityUtil {
    private static final WebSecurityUtil instance =  new WebSecurityUtil();
    protected final String errBulidInfo = "ERROR::CANNOT_INIT_ACCESS_KEY_CURRENT";
    public static final WebSecurityUtil getInstance(){
        return instance;
    }

    private static String customAESKEY = null;
    private static final String defaultAESKEY = "jasonMao@ThinkDid";

    /**
     * 通过标准账户模型构建token
     * @param accountModel
     * @return
     */
    public ThinkSecurityToken buildNewAccountModelAbleToken(UserAccountModel accountModel){
        return ThinkSecurityTokenUtil.buildNewTokenByAccountModel(accountModel);
    }

    /**
     * 通过 自定义构架你token
     * @param id
     * @param nickName
     * @param currentRegion
     * @return
     */
    public ThinkSecurityToken buildNewCustomToken(long id, String nickName , String currentRegion) {
        return ThinkSecurityTokenUtil.buildCustom(id,nickName,currentRegion);
    }


    @Remark("设置新的accessKey计算验证的密钥")
    public synchronized void setNewAccessBuilderSecurityKey(String accessBuilderSecurityKey) throws ThinkRuntimeException {
        String testSource ="THINK-DID";
        try {
            final String encrypt = AESUtil.encrypt(testSource, accessBuilderSecurityKey);
            if(testSource.equals(AESUtil.decrypt(encrypt,accessBuilderSecurityKey))){
                customAESKEY = accessBuilderSecurityKey;
            }else{
                throw new ThinkRuntimeException("不合适的ACCESS KEY的验证密钥");
            }
        }catch (Exception e){
            throw new ThinkRuntimeException("不合适的ACCESS KEY的验证密钥");
        }
    }
    protected String getKey(){
        if(StringUtil.isEmpty(customAESKEY)){
            return defaultAESKEY;
        }
        return customAESKEY;
    }

    public AccessKey getAccessKeyValueOfAkString(String accessKeyString){
        return AccessKey.valueOf(accessKeyString);
    }



    public  final AccessKey buildAccessKey(long systemAccountId){
        return new AccessKey(systemAccountId);
    }
    //替换字符串中出现的空格





}

