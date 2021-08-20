package com.think.moudles.auth;

import com.think.core.annotations.Remark;

import java.io.Serializable;

@Remark(value = "账户专用模型",description = "仅存储账号密码 和绑定的 id")
public class ThinkAuthModel implements Serializable {
    private String id ;
    private String pw ;
    private long bindId;

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public long getBindId() {
        return bindId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public void setBindId(long bindId) {
        this.bindId = bindId;
    }
}
