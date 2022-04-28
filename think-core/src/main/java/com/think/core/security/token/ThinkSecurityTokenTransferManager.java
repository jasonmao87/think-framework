package com.think.core.security.token;


import com.alibaba.fastjson.JSONObject;
import com.think.common.util.FastJsonUtil;
import com.think.core.security.token.filter.IThinkSecurityAsyncTokenFilter;
import com.think.core.threadLocal.ThinkThreadLocal;

import java.util.*;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/24 22:47
 * @description : token 传递管理器
 */
public class ThinkSecurityTokenTransferManager {

    private static final List<IThinkSecurityAsyncTokenFilter> filterList = new ArrayList<>();




    public static final String buildFullTransferStringByToken(ThinkSecurityToken token){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionData",token.getSessionData());
        jsonObject.put("tokenData",token.getTokenData());
        return jsonObject.toJSONString();
    }

    public static final String buildSimpleTransferStringByToken(ThinkSecurityToken token){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionData",token.getFixedSessionData());
        return jsonObject.toJSONString();
    }

    public static final ThinkSecurityToken buildTokenByTransferString(String transferString){
        JSONObject jsonObject = JSONObject.parseObject(transferString);
        ThinkSecurityToken token ;
        Map<String, Object> sessionDataMap = jsonObject.getJSONObject("sessionData").getInnerMap();
        final boolean containsKey = jsonObject.containsKey("tokenData");
        Map<String,String> sessionData = new HashMap<>();
        sessionDataMap.forEach((k,v)->{
            sessionData.put(k, (String) v);
        });
        if(containsKey){
            token = ThinkSecurityToken.valueOfJsonString(jsonObject.getJSONObject("tokenData").toJSONString());
        }else{
            final Map<String,String[]> tokenDataDefaultMap =new HashMap<>();
            sessionDataMap.forEach((k,v)->{
                tokenDataDefaultMap.put( k,new String[]{(String) v});
            });
            token = ThinkSecurityToken.valueOfJsonString(FastJsonUtil.parseToJSON(tokenDataDefaultMap));
        }
        sessionData.forEach((k,v)->{
            token.setSessionValue(k,v);
        });

        return token;
    }

    public static final void addFilter(IThinkSecurityAsyncTokenFilter filter){
        filterList.add(filter);
        filterList.sort((a,b)->{
            return Integer.valueOf(a.order()).compareTo(Integer.valueOf(b.order()));
        });
    }


    /**
     * 放到 本地线程边栏中
     * @param token
     * @param usingTokenOnlyTransferSessionData
     */
    public static final void setThreadLocal(ThinkSecurityToken token, boolean usingTokenOnlyTransferSessionData){
        String transferJson = usingTokenOnlyTransferSessionData?buildSimpleTransferStringByToken(token):buildFullTransferStringByToken(token);
        ThinkThreadLocal.set(transferJson);
    }

    public static final ThinkSecurityToken getTokenFromThreadLocal(){
        final String transferTokenString = ThinkThreadLocal.getString();
        if(transferTokenString == null){
            return null;
        }
        ThinkSecurityToken token = buildTokenByTransferString(transferTokenString);
        return token;
    }


    public static final ThinkSecurityToken getToken(){
        ThinkSecurityToken t = getTokenFromThreadLocal();
        if(t != null){
            return t;
        }

        for (IThinkSecurityAsyncTokenFilter tokenFilter : filterList) {
            final ThinkSecurityToken asyncToken = tokenFilter.getAsyncToken();
            if(asyncToken!=null){
                return asyncToken;
            }
        }
        return null;
    }

    public static final void removeTokenFromThreadLocal(){
        ThinkThreadLocal.remove();
    }


}
