package com.think.core.security;

import com.think.common.util.ThinkMilliSecond;
import com.think.common.util.security.AESUtil;
import com.think.core.annotations.Remark;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/27 21:36
 * @description : TODO
 */
@Slf4j
public class AccessKey implements Serializable {
    private static final long serialVersionUID = 1289509354248566593L;
    private long id;
    private long expireTime;
    private int uaHashCode;



    private long timeAfter30Minutes(){
        return ThinkMilliSecond.currentTimeMillis() + TimeUnit.MINUTES.toMillis(32);
    }

    protected AccessKey(long id,String ua ) {
        this.id = id;
        this.expireTime = timeAfter30Minutes();
        this.uaHashCode = 0;
        try {
            this.uaHashCode = ua.hashCode();
        } catch (Exception e) {

        }
    }

    protected AccessKey(long id, String ua,long expireTime) {
        this.id = id;
        this.expireTime = expireTime;
        this.uaHashCode = 0;
        if(ua!=null){
            this.uaHashCode = ua.hashCode();
        }else{
            this.uaHashCode = 0 ;
        }

    }

    private void setUaHashCode(int uaHashCode) {
        this.uaHashCode = uaHashCode;
    }

    protected static final AccessKey valueOf(String accessKey,String ua) throws RuntimeException {
        int uaHashCode =0 ;
        try {
            final String decrypt = AESUtil.decrypt(accessKey, WebSecurityUtil.getInstance().getKey());
            final String[] split = decrypt.split("@");
            long expire = Long.valueOf(split[0]).longValue();
            long id = Long.valueOf(split[1]);
            uaHashCode = Integer.valueOf(split[2]);
            AccessKey ak = new AccessKey(id, ua,expire);
            ak.setUaHashCode(uaHashCode);
            return ak;
        } catch (Exception e) {
            log.error("接收到的ACCESS KEY信息\n\t ACCESS KEY = {} ,\n\tUser-Agent = {} ,\n\t UA-HASH = {}" , accessKey ,ua, uaHashCode);
            //throw new RuntimeException("[此异常输出不会中断程序]非法的ACCESS KEY ");
        }
        return null;
    }

    public long getId() {
        return id;
    }


    public int getUaHashCode() {
        return uaHashCode;
    }

    public long getExpireTime() {
        return expireTime;
    }

    @Remark("获取AKString")
    public String getAccessKeyString() {
        StringBuilder sb = new StringBuilder("")
                .append(expireTime).append("@")
                .append(id).append("@").append(uaHashCode);
        try {
            String securityKey = WebSecurityUtil.getInstance().getKey();
            String sourceString = sb.toString();
            if (log.isDebugEnabled()) {
            }
            return AESUtil.encrypt(sourceString,securityKey);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("错误----",e);
            }

            return WebSecurityUtil.getInstance().errBulidInfo;
        }
    }

    public AccessKey renewAccessKey() {
        this.expireTime = timeAfter30Minutes();
        return this;
    }

    @Remark("是否环境未改变(非网络环境，必为TRUE )")
    public boolean isUaSafe(String ua) {
        try {
            return ua.hashCode() == this.uaHashCode;
        } catch (Exception e) {
        }
        return true;
    }


    @Remark("是否过期 ")
    public boolean isExpire() {
        return ThinkMilliSecond.currentTimeMillis() - getExpireTime() > 0;
    }

    @Remark("是否建议续期")
    public boolean canRenew() {
        return  getExpireTime() - ThinkMilliSecond.currentTimeMillis() > TimeUnit.MINUTES.toMillis(15);
    }

    @Remark("获取AKString，即将续期会自动续期")
    public String getAccessKeyByAutoRenew() {
        if (canRenew()) {
            this.renewAccessKey();
        }
        return this.getAccessKeyString();
    }


    public boolean isNew(){
        return getExpireTime() -ThinkMilliSecond.currentTimeMillis() > TimeUnit.MINUTES.toMillis(30);


    }

}
