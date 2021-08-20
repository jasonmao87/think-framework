package com.think.core.security;

import com.think.common.util.security.Base64Util;
import com.think.moudles.auth.UserAccountModel;
import com.think.web.util.WebUtil;

import java.util.Map;

public class WebTokenBuilder {
        public static final ThinkToken buildToken(UserAccountModel accountModal, String currentRegion, Map<String, Map<String,Object>> extents){
        ThinkToken thinkToken = new ThinkToken();
        thinkToken.setIpAddr(WebUtil.ip())
                .setClientId(WebUtil.clientId())
                .setAccountId(accountModal.getId())
                .setNick(accountModal.getUserName())
                .setUserId(accountModal.getUserId())
                .setCurrentRegion(currentRegion)
                .setExtend(extents);
        return thinkToken;
    }

    public static final ThinkToken buildToken(String tokenString){
        tokenString = Base64Util.decodeToString(tokenString);
        try{
            ThinkToken token = ThinkToken.parseOfJsonString(tokenString);
            if(token!=null){
                return token;
            }
        }catch (Exception e){}
        return null;
    }


//    static {
//
//        ThinkExecuteThreadSharedMessageManager.setAsyncTokenFilter(new IAsyncTokenFilter() {
//            @Override
//            public ThinkToken getToken() {
//                if(WebUtil)
//                return WebUtil.getToken().get();
//            }
//        });
//
//    }
}
