package com.think.core.security.token;

import com.think.moudles.auth.UserAccountModel;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/20 23:09
 * @description : TODO
 */
public class ThinkSecurityTokenUtil {


    /**
     * 通过内置的账户模型构建
     * @param model
     * @return
     */
    public static final ThinkSecurityToken buildNewTokenByAccountModel(UserAccountModel model){
        ThinkSecurityToken token = new ThinkSecurityToken();
        return buildCustom(model.getId(), model.getUserId(), model.getUserName(),model.getPartitionRegion());

    }

    /**
     * 自定义构建
     * @param id
     * @param nickName
     * @param currentRegion
     * @return
     */
    public static final ThinkSecurityToken buildCustom(long id ,String userLoginId, String nickName , String currentRegion ) {
        ThinkSecurityToken token = new ThinkSecurityToken();
        token.setId(id);
        token.setUserLoginId(userLoginId);
        token.setNickName(nickName);
        token.setCurrentRegion(currentRegion);
        return token;
    }


}

