package com.think.core.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/27 15:43
 * @description : TODO
 */
public final class BlobEntity implements Serializable {
    private static final long serialVersionUID = -4390597867527856562L;

    private long id ;

    private String dataKey ;

    private String dataRegion ;

    private String dataName ;

    private String dataType ;

    private byte[] data;

    private String remark ;

    private Date createTime ;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getDataRegion() {
        return dataRegion;
    }

    public void setDataRegion(String dataRegion) {
        this.dataRegion = dataRegion;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
