package com.think.core.security.token;

import com.alibaba.fastjson.JSONObject;
import com.think.common.result.ThinkResult;
import com.think.common.util.FastJsonUtil;
import com.think.common.util.security.Base64Util;
import com.think.core.annotations.Remark;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;


/**
 * @Date :2021/9/30
 * @Name :ThinkSecurityToken
 * @author JasonMao
 * @Description :    token version 2
 */
@Slf4j
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
    private Map<String,String> sessionData =new HashMap<>();


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
            this.sessionData.forEach((k, v) -> {
                this.tokenData.put(k, new String[]{v});
            });
        }else{
            throw new RuntimeException("非法的构建，不允许如此设置sessionData");
        }
    }

    public Map<String, String> getSessionData() {
//        return sessionData;
        return getFixedSessionData();
    }



    /**
     * 获取 fixed session data Map  ，补全 sessionData
     * @return
     */
    public Map<String, String> getFixedSessionData() {
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
//        if (containsUserDataInfo(key,value)) {
            sessionData.put(key,value);
        return ThinkResult.success(1);
//        }
//        return ThinkResult.forbidden("token中未包含key:"+key+"的value:"+value);

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


//    public static void main(String[] args) {
//        String  json = "{\"sessionData\":{\"deptId\":\"725723693275152385\"},\"tokenData\":{\"tokenDataRegion\":[\"A7\"],\"sysRole\":[\"USER\"],\"tokenUserLoginId\":[\"8ns2haa\"],\"departmentIds\":[\"725723693275152385\",\"724455928133320705\",\"721046482071846913\",\"706710703719841793\",\"702277187267461121\",\"696640323230629889\",\"696572008278786049\",\"696571964162048001\",\"696114862185512961\",\"696113979962949633\",\"695654535572488193\"],\"mscId\":[\"695327730125766657\"],\"tokenDataId\":[\"695331901491118081\"],\"userLoginId\":[\"8ns2haa\"],\"customerAccountId\":[\"694883152758309889\"],\"hospitalId\":[\"695338450281299969\"],\"hospitalManager\":[\"true\"],\"committeeIds\":[\"695972089603031041\",\"695969384026341377\"],\"tokenDataNick\":[\"林旭\"],\"workGroupIds\":[\"706353836013387777\",\"696044539466481665\",\"696042001390370817\",\"696041672461516801\"]}}";
//        System.out.println(json);
//        final ThinkSecurityToken thinkSecurityToken = ThinkSecurityToken.valueOfJsonString(json);
//        System.out.println(ThinkSecurityTokenTransferManager.buildFullTransferStringByToken(thinkSecurityToken));
//
//    }



    @Remark("从tokenString还原TOKEN，不包含SESSION Data")
    public static ThinkSecurityToken valueOfJsonString(String jsonString){
        try {
            String tokenDataKey = "tokenData";
            String sessionDataKey = "sessionData";
            JSONObject json = FastJsonUtil.parseToJson(jsonString);
            if (json!= null && json.containsKey(tokenDataKey)) {
                Map<String, Object> tokenData = json.getJSONObject(tokenDataKey);
                ThinkSecurityToken token = ofTokenData(tokenData);
                if (json.containsKey(sessionDataKey)) {
                    Map<String, Object> sessionData = json.getJSONObject(sessionDataKey);
                    if (sessionData != null) {
                        for (Map.Entry<String, Object> entry : sessionData.entrySet()) {
                            token.getSessionData().put(entry.getKey(), (String) entry.getValue());
                        }
                    }
                }
                return token;
            } else {
                return tokenDataJsonString(jsonString);

            }
        }catch (Exception e){
//            e.printStackTrace();
            return tokenDataJsonString(jsonString);
        }

    }


//    public static void main(String[] args) {
//        String json = "{\"x\":{\"deptId\":\"725723693275152385\"},\"tokenData\":{\"tokenDataRegion\":[\"A7\"],\"sysRole\":[\"USER\"],\"tokenUserLoginId\":[\"8ns2haa\"],\"departmentIds\":[\"725723693275152385\",\"724455928133320705\",\"721046482071846913\",\"706710703719841793\",\"702277187267461121\",\"696640323230629889\",\"696572008278786049\",\"696571964162048001\",\"696114862185512961\",\"696113979962949633\",\"695654535572488193\"],\"mscId\":[\"695327730125766657\"],\"tokenDataId\":[\"695331901491118081\"],\"userLoginId\":[\"8ns2haa\"],\"customerAccountId\":[\"694883152758309889\"],\"hospitalId\":[\"695338450281299969\"],\"hospitalManager\":[\"true\"],\"committeeIds\":[\"695972089603031041\",\"695969384026341377\"],\"tokenDataNick\":[\"林旭\"],\"workGroupIds\":[\"706353836013387777\",\"696044539466481665\",\"696042001390370817\",\"696041672461516801\"]}}";
//
//        final ThinkSecurityToken token = valueOfJsonString(json);
//        System.out.println(FastJsonUtil.toPrettyString(token));
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        System.out.println(token.getSessionData());
//
//    }



    private static final ThinkSecurityToken tokenDataJsonString(String jsonString){
        Map<String, Object> json = FastJsonUtil.getMapFromJSON(jsonString);
        return ofTokenData(json);
    }

    private static final ThinkSecurityToken ofTokenData(Map<String,Object> json){
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







//
//    public static final ThinkSecurityToken valueOfJsonStringWithSessionData(String jsonString){
//        Map<String, Object> json = FastJsonUtil.getMapFromJSON(jsonString);
//        json.containsKey("jason")
//
//
//    }

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

    @Remark("主token，主要用于签名，不包含sessionData ")
    public String getTokenJsonString(){
        return FastJsonUtil.parseToJSON(this.tokenData);
    }


    @ApiModelProperty("base64编码的TOKENString，（不包含SessionData）每次返回，且用于签名")
    public String getBase64TokenString(){
        return Base64Util.encodeToString(getTokenJsonString());
    }


    public String getFullJsonString(){
        return ThinkSecurityTokenTransferManager.buildFullTransferStringByToken(this);
    }





}
