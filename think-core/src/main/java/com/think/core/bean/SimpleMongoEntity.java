package com.think.core.bean;

import com.think.common.util.DateUtil;
import com.think.core.annotations.Remark;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import net.minidev.json.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@Data
@Remark("通用MongoDao")
@Accessors(chain = true)
public abstract class SimpleMongoEntity implements Serializable {

    @Remark("id")
    private String id ;

//    @Remark(value = "分区标识",description = "无需人工设置")
//    private String _partitionRegion = "";
//
//    @Remark(value = "年度切割标识",description = "无需人工设置")
//    private int _splitYear = -1;

    @ApiModelProperty(hidden = true,value = "数据过期时间，前端设置无效")
    @Remark(value = "数据过期时间",description = "设置了 expireAble 有效,数据将会被自动清除 ")
    private Date expireAt = DateUtil.now();


    @Remark(value = "think关联专用辅助",description = "辅助关联字段，按需自用")
    @ApiModelProperty(hidden = true,value = "think关联专用辅助")
    private String thinkLinkedId ;



    @ApiModelProperty(value = "更新钥匙，每次update都会比对这个值是否相等！",hidden = true)
    private long thinkUpdateKey =0L ;


    @JsonIgnore
    public long getThinkUpdateKey() {
        return thinkUpdateKey;
    }
}
