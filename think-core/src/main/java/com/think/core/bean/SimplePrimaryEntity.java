package com.think.core.bean;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.util.DateUtil;
import com.think.common.util.IdUtil;
import com.think.core.annotations.Remark;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 基础类 ...
 *  因为考虑到 按照 时间线来做切割，所有的主表， 都 依赖本类作为基础父类，而从表，如订单明细表，需要 依赖 SimpleRefEntity 作为 父类
 */
@Remark("主表基础类")
@Data
@Accessors(chain = true)
public abstract class SimplePrimaryEntity extends _Entity {
    private static final long serialVersionUID = -3855081976206446802L;


    /**
     * 仅对 时间分割表 有意义 ，用于计算 需要被存储的数据库表
     * @return
     */
    @ApiModelProperty(value = "仅对分割表有意义，时间分割分区",hidden = true)
    public int getSplitYear(){
        if (getId()!=null) {
            return DateUtil.year(IdUtil.idToDate(getId()));
        }
        return DateUtil.year(IdUtil.idToDate(-1L));

    }


    @Remark("构建一个空的filter")
    public <T extends SimplePrimaryEntity> ThinkSqlFilter<T> buildEmptyFilter(int limit){
        return (ThinkSqlFilter<T>) ThinkSqlFilter.build(getClass(),limit);
    }

    @Remark("构建一个空的updateMapper")
    public <T extends SimplePrimaryEntity> ThinkUpdateMapper<T> buildEmptyUpdateMapper(){
        return (ThinkUpdateMapper<T>) ThinkUpdateMapper.build(getClass());
    }

    /**
     * 构建包含当前id的 updateMapper ，无法在设置 filter
     * @return
     */
    @Remark(value = " 构建包含当前id的 updateMapper ，无法在设置 filter",description = "如果id不存在，返回空的updateMapper")
    public  <T extends SimplePrimaryEntity> ThinkUpdateMapper<T>  buildUpdateMapperWithCurrentId(){
        if(this.getId() !=null && this.getId()>0) {
            return (ThinkUpdateMapper<T>) ThinkUpdateMapper.build(getClass()).setTargetDataId(this.getId());
        }
        return this.buildEmptyUpdateMapper();
    }



}
