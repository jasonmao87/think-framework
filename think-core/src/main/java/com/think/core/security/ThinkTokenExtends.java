package com.think.core.security;

import com.think.common.util.FastJsonUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直接简单点，自由度高点，所以废弃
 *
 */
@Deprecated
@Data
@Accessors(chain = true)
public class ThinkTokenExtends implements Serializable {

    @ApiModelProperty("当前扩展，用于服务直接读取的（多可选时候，用于选中的值）")
    private Map<String,Object> currentExtend = new HashMap<>();

    @ApiModelProperty("可选择的扩展（注意：当前扩展，必须在list中）")
    private List<Map<String,Object>> optionalExtend = new ArrayList<>();

    /**
     * 检查 扩展是否合法
     * @return
     */
    private boolean available( ){
        for(Map<String,Object> optional : optionalExtend){
            if(!optional.toString().trim().equalsIgnoreCase(currentExtend.toString().trim())){
                return false;
            }
        }
        return true;
    }

    protected String securityString(){
        int index = 0 ;
        StringBuilder securityString = new StringBuilder("");
        for(Map<String,Object> optional : optionalExtend){
            if(index > 0){
                securityString.append("]&[");
            }
            securityString.append(FastJsonUtil.parseToJSON(optional));
            index ++ ;
        }
        return securityString.toString();
    }


    /**
     * 第一个 ADD的 opt 会被设置为 current！
     * @param opt
     * @return
     */
    public ThinkTokenExtends addOptionalExtend(Map<String,Object> opt){
        boolean exits = false;
        for(Map<String,Object> optional : optionalExtend){
            if(optional.toString().trim().equalsIgnoreCase(currentExtend.toString().trim())){
                exits =true;
            }
        }
        if(exits == false){
            optionalExtend.add(opt);
        }
        if(this.optionalExtend.size() ==1){
            this.currentExtend = opt;
        }
        return this;
    }

    /**
     * 自主设置 当前 选中的 扩展 信息
     * @param currentExtend
     * @return
     */
    public ThinkTokenExtends setCurrent(Map<String,Object> currentExtend){
        this.currentExtend = currentExtend;
        if(!this.available()){
            this.optionalExtend.add(currentExtend);
        }
        return this;
    }
}
