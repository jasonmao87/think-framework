package com.think.core.security.token;

import com.think.common.result.ThinkResult;
import com.think.common.util.FastJsonUtil;
import com.think.common.util.security.Base64Util;
import com.think.core.annotations.Remark;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.*;


/**
 * @Date :2021/9/30
 * @Name :ThinkSecurityToken
 * @author JasonMao
 * @Description :    token version 2
 */
public class ThinkSecurityToken implements Serializable {
    private static final long serialVersionUID = -1234567890987654321L;

    @ApiModelProperty("用户的系统账户id(long类型)")
    @Remark( "账户的id" )
    private long id ;

    @ApiModelProperty("用户的登录名(String 类型， 如 : administrator  )")
    @Remark("用户的登录名")
    private String userLoginId;

    @Remark(value="账户的用户姓名" )
    private String nickName;

    @Remark("账户数据分区")
    private String currentRegion;

    @Remark("系统角色")
    private String sysRole = "";


    @Remark("当前SESSION数据,一次性数据，不参与签名")
    private Map<String,String> sessionData;


    @Remark("用户token数据，参与签名")
    private Map<String,String[]> tokenData = new HashMap<>();


    protected ThinkSecurityToken() {
    }


    protected void setCurrentRegion(String currentRegion) {
        if(currentRegion ==null){
            currentRegion = "";
        }
        this.currentRegion = currentRegion;
        this.tokenData.put("tokenDataRegion",new String[]{currentRegion});
    }

    protected void setNickName(String nickName) {
        this.nickName = nickName;
        this.tokenData.put("tokenDataNick",new String[]{nickName});
    }

    protected void setUserLoginId(String userLoginId) {
        this.userLoginId = userLoginId;
        this.tokenData.put("tokenUserLoginId",new String[]{userLoginId});
    }

    public void setSysRole(String sysRole) {
        this.sysRole = sysRole;
    }


    public String getUserLoginId() {
        return userLoginId;
    }

    public String getSysRole() {
        return sysRole;
    }

    protected void setId(long id) {
        this.id = id;
        this.tokenData.put("tokenDataId",new String[]{String.valueOf(id)});
    }

    /**
     * 不安全的set方法，只允许 传递时候使用
     * @param sessionData
     */
    protected void setSessionData(Map<String, String> sessionData) {
        if(this.tokenData.isEmpty()) {
            this.sessionData = sessionData;
            this.sessionData.forEach((k, v) -> {
                this.tokenData.put(k, new String[]{v});
            });
        }else{
            throw new RuntimeException("非法的构建，不允许如此设置sessionData");
        }
    }

    public Map<String, String> getSessionData() {
        return sessionData;
    }


    /**
     * 获取 fixed session data Map  ，补全 sessionData
     * @return
     */
    public Map<String, String> getFixedSessionData() {
        if(this.sessionData == null){
            this.sessionData = new HashMap<>();
        }
        this.sessionData.put("tokenDataId", String.valueOf(id));
        this.sessionData.put("tokenUserLoginId",userLoginId);
        this.sessionData.put("tokenDataNick",nickName);
        this.sessionData.put("tokenDataRegion",currentRegion);
        return sessionData;
    }


    @Remark("获取当前session中使用的值，比如准确的权限指定")
    public String getSessionValue(String key) {
        return sessionData.get(key);
    }


    @Remark("sessionData，临时会话数据设置，但是会在线程中传递，不会传递回WEB 前端")
    public ThinkResult<Integer> setSessionValue(String key, String value) {
        if(this.sessionData == null) {
            this.sessionData = new HashMap<>();
        }
        if (containsUserDataInfo(key,value)) {
            sessionData.put(key,value);
            return ThinkResult.success(1);
        }
        return ThinkResult.forbidden("token中未包含key:"+key+"的value:"+value);

    }

    public long getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getCurrentRegion() {
        return currentRegion;
    }

    public Map<String, String[]> getTokenData() {
        return tokenData;
    }

    public final void setTokenDataInfo(String key , String[] values) {
        if(values!=null && values.length>0) {
            this.tokenData.put(key, values);
        }else{
        }
    }



    @Remark("从tokenString还原TOKEN，不包含SESSION Data")
    public static ThinkSecurityToken valueOfJsonString(String jsonString){
        Map<String, Object> json = FastJsonUtil.getMapFromJSON(jsonString);
        ThinkSecurityToken token = new ThinkSecurityToken();
        json.forEach((k,v)->{
            String[] strings;
            if(v instanceof List){
                strings = ((List<?>) v).stream().toArray(String[]::new);
                token.setTokenDataInfo(k,strings);
            }else if(v instanceof Set){
                strings = ((Set<?>) v).stream().toArray(String[]::new);
                token.setTokenDataInfo(k,strings);
            }else if(v instanceof String[]){
                strings =(String[] ) v;
                token.setTokenDataInfo(k, (String[]) v);
            }else{
                throw new RuntimeException("无法解析 "+k +" 类型：" + v.getClass());
            }

            if(k.equals("tokenDataId")){
                token.setId(Long.valueOf(strings[0]));
            }else if(k.equals("tokenDataNick")){
                token.setNickName(strings[0]);
            }else if(k.equals("tokenDataRegion")){
                token.setCurrentRegion(strings[0]);
            }else if(k.equals("tokenUserLoginId")){
                token.setUserLoginId(String.valueOf(strings[0]));
            }else if(k.equals("sysRole")){
                token.setSysRole(strings[0]);
            }
        });
        return token;
    }

    /**
     *  userTokenData中是否包含制定的key 和 value
     * @param key   关键字
     * @param v
     * @return
     */
    private final boolean containsUserDataInfo(String key ,String v){
        if (isUserDataContainsKey(key)) {
            for (String s : this.tokenData.get(key)) {
                if(s.trim().equals(v.trim())){
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isUserDataContainsKey(String key){
        return this.tokenData.containsKey(key);
    }

    public String getTokenJsonString(){
        return FastJsonUtil.parseToJSON(this.tokenData);
    }


    @ApiModelProperty("base64编码的TOKENString，每次返回，且用于签名")
    public String getBase64TokenString(){
        return Base64Util.encodeToString(getTokenJsonString());
    }




}
