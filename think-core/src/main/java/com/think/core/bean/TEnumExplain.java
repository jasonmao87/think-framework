package com.think.core.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Date :2021/9/26
 * @Name :EnumRemark
 * @Description : TEnum解释模型
 */
public class TEnumExplain implements Serializable {
    private static final long serialVersionUID = 1010101010101010010L;

    @ApiModelProperty(hidden = true)
    private String keyName ;

    @ApiModelProperty(hidden = true)
    private String typeName ;

    @ApiModelProperty(hidden = true)
    private String remark ;

    @ApiModelProperty(hidden = true)
    List<Map<String,String>> explain;

    public TEnumExplain(String keyName ,String typeName, String remark ,List<Map<String,String>> explainList) {
        this.keyName = keyName;
        this.typeName = typeName;
        this.remark = remark;
        this.explain = explainList;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getRemark() {
        return remark;
    }

    public List<Map<String, String>> getExplain() {
        return explain;
    }


}
