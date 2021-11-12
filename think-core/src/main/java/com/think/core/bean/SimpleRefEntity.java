package com.think.core.bean;


import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.util.DateUtil;
import com.think.common.util.IdUtil;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Remark("从表基础类")
@Data
@Accessors(chain = true)
public abstract class SimpleRefEntity<T extends SimplePrimaryEntity> extends _Entity<T> {
    private static final long serialVersionUID = -6512682758436782849L;

    @Remark("关联业务主表的id，主表只允许是顶级的，即使是从表的关联数据，也应该输入顶级表的主键Id")
    @ThinkColumn(nullable = false)
    private long rootPrimaryId =-1;



    public final <T extends SimpleRefEntity> T setRootPrimaryId(long rootPrimaryId) {
        T t = (T) this;
        if(rootPrimaryId > 0){
            this.rootPrimaryId = rootPrimaryId;
        }else{
        }
        return t;
    }

    /**
     * 仅对 时间分割表 有意义 ，用于计算 需要被存储的数据库表
     * @return
     */
    @ApiModelProperty(value = "仅对分割表有意义，时间分割分区",hidden = true)
    public int getSplitYear(){
//        long targetId = getRootPrimaryId();
        if(getRootPrimaryId() < 0){
           // throw new RuntimeException("rootPrimaryId 未指定");
            return -1;
        }
        return DateUtil.year(IdUtil.idToDate(getRootPrimaryId()));
    }


    @Remark("构建一个空的filter")
    public ThinkSqlFilter<T> buildEmptyFilter(int limit){
        return ThinkSqlFilter.build(getSelfClass(),limit);
    }

    @Remark("构建一个空的updateMapper")
    public ThinkUpdateMapper<T> buildEmptyUpdateMapper(Class<T> tClass){
        ThinkUpdateMapper<T> updateMapper = (ThinkUpdateMapper<T>) ThinkUpdateMapper.build(tClass);
        if(this.rootPrimaryId >0) {
            updateMapper.getFilter().eq("rootPrimaryId", this.rootPrimaryId);
        }
        return updateMapper;
    }

    /**
     * 构建包含当前id的 updateMapper ，无法在设置 filter
     * @return
     */
    @Remark(value = " 构建包含当前id的 updateMapper ，无法在设置 filter",description = "如果id不存在，返回空的updateMapper")
    public ThinkUpdateMapper<T>  buildUpdateMapperWithCurrentId(Class<T> tClass){
        if(this.getId() !=null && this.getId()>0) {
            ThinkUpdateMapper<T> tThinkUpdateMapper = (ThinkUpdateMapper<T>) ThinkUpdateMapper.build(tClass).setTargetDataId(this.getId());
            if(this.rootPrimaryId >0) {
                tThinkUpdateMapper.getFilter().eq("rootPrimaryId", this.rootPrimaryId);
            }
            return tThinkUpdateMapper;
        }
        return this.buildEmptyUpdateMapper(tClass);
    }

}
