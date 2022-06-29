package com.think.core.bean;

import com.think.core.annotations.bean.ThinkColumn;
import com.think.core.annotations.bean.ThinkTable;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/27 20:06
 * @description : 二进制相关的 持久化类.对于 巨大的 data ，会自动拆分成多个，读取时候 ，在组合成1个
 */
@ThinkTable(value = "tb_binary_data" )
public class TbBinaryEntity implements Serializable {
    private static final long serialVersionUID = -2285426342492785154L;

    private long id ;

    private byte[] binaryData ;

    private int dataLength ;

    private String name ;

    private String typeName ;

    private Date createTime ;

    private Date updateTime ;

//    private boolean mainDataState  ;
//
//    private int splitCount ;
//
//    private int splitIndex ;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    //    public boolean isMainDataState() {
//        return mainDataState;
//    }
//
//    public void setMainDataState(boolean mainDataState) {
//        this.mainDataState = mainDataState;
//    }
//
//    public int getSplitCount() {
//        return splitCount;
//    }
//
//    public void setSplitCount(int splitCount) {
//        this.splitCount = splitCount;
//    }
//
//    public int getSplitIndex() {
//        return splitIndex;
//    }
//
//    public void setSplitIndex(int splitIndex) {
//        this.splitIndex = splitIndex;
//    }
}
