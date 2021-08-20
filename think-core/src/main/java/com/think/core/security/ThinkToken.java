package com.think.core.security;

import com.alibaba.fastjson.JSONObject;
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

    @ApiModelProperty("初始化时间")
    private long initTime;

    @ApiModelProperty("过期时间")
    private long expireTime;


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
                .append("extends=")
                .append(FastJsonUtil.parseToJSON(extend))
                .append("}");
        return securitySource.toString()
                .replaceAll("\"","");
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
            return JSONObject.parseObject(json, ThinkToken.class);
        }catch (Exception e){
            if (log.isWarnEnabled()) {
                log.warn("无法从JSONString构建token，或许是非法得tokenString,tokenString= {}",json);
            }
            return null;
        }
    }

//
//    public static void main(String[] args) {
//        List<String> list =new ArrayList<>();
//        list.add("dada");
//        list.add("acacafa");
//
//        Map<String,Object> current = new HashMap<>();
//        current.put("role","root");
//        current.put("hgroup",985524545001L);
//        current.put("hgroupName","浙江医共体");
//        current.put("masterHospital" ,"true");
//        current.put("currentHospital",198000054411120L);
//        current.put("currentDeptTypeId",5200011345454521L);
//        current.put("currentDeptId" ,97851103666000154L);
//        current.put("currentRole" , 95200555700120771L);
//        current.put("list",list);
//        Map<String,Object> opt = new HashMap<>();
//        opt.put("role","root");
//        opt.put("hgroup",12685544116878L);
//        opt.put("hgroupName","浙江医共体");
//        opt.put("masterHospital" ,"true");
//        opt.put("currentHospital",298000054411120L);
//        opt.put("currentDeptTypeId",4200011345454521L);
//        opt.put("currentDeptId" ,58851103666000154L);
//        opt.put("currentRole" , 15200555700120771L);
//        opt.put("list",list);
//
//        Map<String,Map<String,Object>> extend = new HashMap<>();
//        extend.put("current",current);
//        extend.put("opt",opt);
//        ThinkTokenExtends extent = new ThinkTokenExtends();
//        extent.addOptionalExtend(opt);
//        extent.setCurrent(current);
//
//        ThinkToken token = new ThinkToken();
//        token.setClientId("123456")
//                .setCurrentRegion("A1")
//                .setAccountId(123L)
//                .setNick("Jackson")
//                .setIpAddr("127.0.0.1")
//                .setExtend(extend);
//
//
//        String tokenJson = token.toTokenString();
//
//        ThinkToken reToken = ThinkToken.parseOfJsonString(tokenJson);
//        System.out.println("原始加密字符串"+token.securityString());
//        System.out.println("还原加密字符串"+reToken.securityString());
//        System.out.println("原始"+token.toTokenString());
//        System.out.println("还原"+reToken.toTokenString());
//
//
//
//    }
}
