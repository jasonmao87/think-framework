package com.think.core.security.token;


import com.alibaba.fastjson.JSONObject;
import com.think.common.util.StringUtil;
import com.think.core.annotations.Remark;
import com.think.core.security.token.filter.IThinkSecurityAsyncTokenFilter;
import com.think.core.threadLocal.ThreadLocalBean;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/24 22:47
 * @description : token 传递管理器
 */
public class ThinkSecurityTokenTransferManager {

    private static final ThreadLocal<ThreadLocalBean<String>> tokenThreadLocal = new ThreadLocal<>();

    private static final List<IThinkSecurityAsyncTokenFilter> filterList = new ArrayList<>();






    public static final String buildFullTransferStringByToken(ThinkSecurityToken token){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionData",token.getSessionData());
        jsonObject.put("tokenData",token.getTokenData());
        return jsonObject.toJSONString();
    }

    @Remark("调整---不在使用精简token ")
    public static final String buildSimpleTransferStringByToken(ThinkSecurityToken token){
        return buildFullTransferStringByToken(token);
        /*
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sessionData",token.getFixedSessionData());
        return jsonObject.toJSONString();
        */
    }

    private static final ThinkSecurityToken buildTokenByTransferString(String transferString){
        return ThinkSecurityToken.valueOfJsonString(transferString);
        /*
        JSONObject jsonObject = JSONObject.parseObject(transferString);
        if(jsonObject == null){
            if (log.isTraceEnabled()) {
                log.trace("transferString is null,无法反序列出TOKEN");
            }
            return null;
        }
        ThinkSecurityToken token ;
        Map<String, Object> sessionDataMap ;
        Map<String, String> sessionData = new HashMap<>();
        final boolean containsKey = jsonObject.containsKey("tokenData");
        if(jsonObject.containsKey("sessionData")) {
            sessionDataMap = jsonObject.getJSONObject("sessionData").getInnerMap();
            sessionDataMap.forEach((k, v) -> {
                sessionData.put(k, (String) v);
            });
        }else{
            sessionDataMap = new HashMap<>();
        }
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
         */
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

        final String transferJson = usingTokenOnlyTransferSessionData?buildSimpleTransferStringByToken(token):buildFullTransferStringByToken(token);
        log.info("{} --本地线程注入 token 信息 >>>>>{}" ,tinfo() ,transferJson);
        tokenThreadLocal.set(new ThreadLocalBean(transferJson));

//        ThinkThreadLocal.set(transferJson);
    }

    private static final ThinkSecurityToken getTokenFromThreadLocal(){
        final ThreadLocalBean<String> threadLocalBean = tokenThreadLocal.get();
        if(threadLocalBean==null){
            return null;
        }
//        if(threadLocalBean.isExpire()){
//            log.warn("本地缓存的线程TOKEN时间过长，主动销毁");
//            removeTokenFromThreadLocal();
//        }

        String transferTokenString = threadLocalBean.getValue();
        if (log.isTraceEnabled()) {
            log.trace("{} --本地线程读取 token >>>>> {}" ,tinfo(),transferTokenString);
        }
        if (StringUtil.isEmpty(transferTokenString)) {
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
//            log.info("from filter to getTOKEN  : {}" ,tokenFilter.getClass().getName());
            final ThinkSecurityToken asyncToken = tokenFilter.getAsyncTokenFromWebRequestInfo();
            if(asyncToken!=null){
                return asyncToken;
            }
        }
        return null;
    }

    public static final void removeTokenFromThreadLocal(){

//        log.info(" {} 本地线程移除 token -----",tinfo());
        tokenThreadLocal.remove();
    }



    public static final String  tinfo(){
        return Thread.currentThread().getId() + "@" + Thread.currentThread().getName();
    }

}
