package com.think.core.security;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.think.common.util.FastJsonUtil;
import com.think.common.util.StringUtil;
import com.think.common.util.ThinkMilliSecond;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

/**
 * 基础Token，webtoken需要基于 基础token
 */
@Data
@Slf4j
@Accessors(chain = true)
public final class ThinkToken implements Serializable {
    private static final long serialVersionUID = 1334719628496757795L;

    protected ThinkToken() {
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.expireTime = ThinkMilliSecond.currentTimeMillis()+ (1000 * 60 *60 * 1) ;
    }

    @ApiModelProperty("客户端标示")
    private String clientId = "";

    @ApiModelProperty("客户端IP")
    private String ipAddr = "";

    @ApiModelProperty("用户longId")
    private long accountId = -1L;
    @ApiModelProperty("用户Id")
    private String userId ="";

    @ApiModelProperty("用户昵称")
    private String nick = "";

    @ApiModelProperty("当前数据分区")
    private String currentRegion = "" ;

    @ApiModelProperty("扩展信息")
    private Map<String,Map<String,Object>> extend;

    @JsonIgnore
    @JSONField(serialize = false)
    @ApiModelProperty("附属信息")
    private ThinkTokenAttachment attachment ;

    @ApiModelProperty("附属信息Map")
    private JSONObject attachmentMap ;

    @ApiModelProperty("初始化时间")
    private long initTime;

    @ApiModelProperty("过期时间")
    private long expireTime;

    @ApiModelProperty("token类型")
    private String tokenType  ="DEFAULT";


    public JSONObject getAttachmentMap() {
        if(attachmentMap == null){
            if(attachment!=null) {
                String json = FastJsonUtil.parseToJSON(attachment);
                return FastJsonUtil.parseToJson(json);
            }
         }
        return attachmentMap;
    }

    public String getCurrentRegion() {
        if (StringUtil.isEmpty(this.currentRegion)) {
            return "";
        }
        return currentRegion;
    }

    public ThinkToken addExtend(ThinkTokenExtends ThinkTokenExtends){
        this.extend = extend;
        return this;
    }

    /**
     *  用户的token String
     * @return
     */
    @JsonIgnore
    public String toTokenString(){
        return FastJsonUtil.parseToJSON(this);
    }

    /**
     * 安全字符串，用于校验 和 签名验证等 使用
     * @return
     */
    protected String securityString(){
        StringBuilder securitySource = new StringBuilder("{base=");
        securitySource.append(this.accountId).append("&")
                .append(this.nick).append("&")
                .append(this.clientId).append("&")
                .append(this.initTime).append("&")
                .append(this.expireTime).append("&")
                .append(this.nick).append("&")
                .append(this.currentRegion).append(";")
//                .append("extends=").append(FastJsonUtil.parseToJSON(extend))
                .append("attachment=").append(attachmentString())
                .append("}");
        return securitySource.toString().replaceAll("'","").replaceAll("\"","");
    }

    private String attachmentString(){
        JSONObject map = getAttachmentMap();
        if(map == null || map.isEmpty()){
            return "EMPTY";
        }else{
            return map.toString();
        }
    }

    /**
     * 从json 实例 化BASE TOKEN
     * @param json
     * @return
     */
    public static ThinkToken parseOfJsonString(String json){
        if(StringUtil.isEmpty(json)){
            return null;
        }


        try{
            ThinkToken token = JSONObject.parseObject(json, ThinkToken.class);
            JSONObject jsonObject = FastJsonUtil.parseToJson(json);
            if(jsonObject.containsKey("attachmentMap")){
                token.setAttachmentMap( jsonObject.getJSONObject("attachmentMap"));
            }
            return token;
        }catch (Exception e){
            if (log.isWarnEnabled()) {
                log.warn("无法从JSONString构建token，或许是非法得tokenString,tokenString= {}",json);
            }
            return null;
        }
    }

    public static <T extends ThinkTokenAttachment> ThinkToken parseOfJsonStringWithAttachment(String json ,Class<T> tClass){
        JSONObject jsonObject = FastJsonUtil.parseToJson(json);
        ThinkToken token = parseOfJsonString(json);
        if(jsonObject.containsKey("attachmentMap")){
            JSONObject attachment = jsonObject.getJSONObject("attachmentMap");
            if(attachment!=null && attachment.isEmpty()==false){
                T t = FastJsonUtil.parseToClass(attachment.toJSONString(),tClass);
                token.setAttachment(t);
            }
        }
        return token;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenType() {
        return tokenType;
    }

    public ThinkTokenAttachment getAttachment() {
        return attachment;
    }

    @JsonIgnore
    public <T> ThinkTokenAttachment getAttachmentOfType(Class<T> tClass){
        if(attachment == null){
            if(this.attachmentMap!=null && this.attachmentMap.isEmpty()==false){
                return FastJsonUtil.parseToClass(attachmentMap.toJSONString(),tClass);

            }
        }
        return null;
    }

    public void setAttachment(ThinkTokenAttachment attachment) {
        this.attachment = attachment;
    }
}
