package com.think.core.bean;

import com.think.common.util.DateUtil;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkColumn;
import com.think.core.annotations.bean.ThinkIgnore;
import com.think.core.bean.util.ClassUtil;
import com.think.core.bean.util.ObjectUtil;
import com.think.structure.ThinkExplainList;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public abstract class _Entity implements Serializable {
    private static final long serialVersionUID = 8257751739282533823L;


    @ApiModelProperty(hidden = true)
    private Long id = null;

    @Remark(value = "think关联专用辅助",description = "辅助关联字段，按需自用！一般不适用于按年拆分表，按年拆分基本是业务数据表！")
    @ThinkColumn(nullable = false,length = 24 ,editAble =false)
    @ApiModelProperty(value = "think关联专用id",hidden = true )
    private String thinkLinkedId = "";


    @ThinkColumn(nullable = false,length = 8)
    @ApiModelProperty(hidden = true)
    private String partitionRegion = "";

//    @ThinkColumn(nullable = false)
//    @ApiModelProperty(value = "删除状态",hidden = true)
//    private boolean deleteState  = false;
//
//    @ThinkColumn(nullable = false)
//    @ApiModelProperty(value = "删除时间",hidden = true)da
//    private Date deleteTime  = DateUtil.zeroDate();

    @ApiModelProperty(value = "删除时间（虚拟）" ,hidden = true)
    public Date getDeleteTime(){
        if(this.id != null && this.id < 0){
            return this.lastUpdateTime;
        }
        return DateUtil.zeroDate();
    }

    @ApiModelProperty(value = "是否删除（虚拟）",hidden = true)
    public boolean isDeleteState(){
        if(this.id != null && this.id < 0){
            return true;
        }
        return false;
    }


    @ThinkColumn(nullable = false,editAble = false)
    @ApiModelProperty(value = "创建时间",hidden = true )
    private Date createTime  = DateUtil.now();

    @ThinkColumn(nullable = false)
    @ApiModelProperty(value = "最后修改时间",hidden = true)
    private Date lastUpdateTime  = DateUtil.zeroDate() ;

    @ThinkColumn(nullable = false,length = 32)
    @ApiModelProperty(value = "创建人UserId",hidden = true)
    private String createUserId ="";

    @ThinkColumn(nullable = false,length = 32)
    @ApiModelProperty(value = "最后人UserId",hidden = true)
    private String updateUserId = "";

    @ApiModelProperty(value = "数据版本号",hidden = true)
    private int version = 0 ;


    @ThinkIgnore
    @ApiModelProperty(value = "是否已经持久化(虚拟字段)",hidden = true)
    @Remark(value = "是否已经持久化（虚拟 不映射,辅助判断字段）",description = "不要人工区设置它，他是用于您们判断用的 ")
    private boolean dbPersistent = false;


    @Remark("不会有任何意义，您人工设置不了它！！！！！！！！！！！！！！")
    public final void setDbPersistent(boolean dbPersistent) {

//        if (log.isDebugEnabled()) {
//            log.debug("无法人工设置该值");
//        }
//        // do nothing !
    }

    public boolean isDbPersistent(){
        return dbPersistent;
    }

    @Remark(value = "只允许ID未设置的 情况下设置id，不然不会有任何作用！！！",description = "")
    public _Entity setId(Long id){
        if(this.id == null || this.id < 1) {
            this.id = id;
        }else{
            Logger log = LoggerFactory.getLogger(getClass());
            if (log.isWarnEnabled()) {
                log.warn("拒绝动态修改Id！");
            }
        }
        return this;
    }


    @Remark("枚举解释器")
    @ThinkIgnore
    @ApiModelProperty(hidden = true)
    private static ThinkExplainList EnumsValueExplain = null;


    @ApiModelProperty(value = "枚举解释" ,hidden = true)
    public ThinkExplainList getThinkTEnumsValueExplain() {
        if (EnumsValueExplain ==null) {
            EnumsValueExplain = new ThinkExplainList();
            ObjectUtil.doThinkEntityTEnumExplain(this);
        }
        return EnumsValueExplain;
    }


    @ApiModelProperty(value = "模型对象类型（可快速通过数据字典获取详情）" ,hidden = true)
    public String getThinkModelType(){
        return this.getClass().getSimpleName();
    }
}
