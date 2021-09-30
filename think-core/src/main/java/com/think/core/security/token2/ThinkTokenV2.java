package com.think.core.security.token2;

import java.io.Serializable;

/**
 * @Date :2021/9/30
 * @Name :ThinkTokenV2
 * @Description : 请输入
 */
public class ThinkTokenV2  implements Serializable {
    private String userId ;
    private long accountId ;
    private String nick;
    private String currentRegion;
    private long initTime;
    private long expireTime;
    private long ipAddr ;
}
