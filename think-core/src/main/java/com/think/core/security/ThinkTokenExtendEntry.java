package com.think.core.security;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * token扩展信息
 *  * 直接简单点，自由度高点，所以废弃
 */
@Deprecated
@Data
@ApiModel("token扩展")
@Accessors(chain = true)
public class ThinkTokenExtendEntry implements Serializable {
    private static final long serialVersionUID = -84036762906158109L;
    @ApiModelProperty("token扩展key")
    private final String extendKey ;
    @ApiModelProperty("当前值")
    private String currentValue ;
    @ApiModelProperty("允许的值，当前用户，可以使用的值")
    private final List<String> allowValues;


    public ThinkTokenExtendEntry(String extendKey, String currentValue, List<String> allowValues) {
        this.extendKey = extendKey;
        this.currentValue = currentValue;
        this.allowValues = allowValues;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("extendKey" ,extendKey);
        map.put("currentValue" , currentValue);
        map.put("allowValues" ,allowValues);
        return map;
    }

    protected static final ThinkTokenExtendEntry ofMap(Map<String,Object> entryMap){
        String entryKey = (String)entryMap.get("extendKey");
        String currentValue = (String) entryMap.get("currentValue");
        List<String> allowValues = (List<String>) entryMap.get("allowValues");
        ThinkTokenExtendEntry entry = new ThinkTokenExtendEntry(entryKey,currentValue,allowValues);
        return entry;
    }

    /**
     * 避开 currentValue
     * @return
     */
    protected String toSecurityString(){
        StringBuilder securityString = new StringBuilder("extendItem=");
        securityString.append(this.extendKey).append("&").append(allowValues.size());
        for(String x : allowValues){
            securityString
                    .append("&")
                    .append(x);
        }
        return securityString.toString();
    }

    /**
     * 检查对象是否合法
     * @return
     */
    public boolean checkAvailable(){
        for(String x : allowValues){
            if(this.currentValue.equalsIgnoreCase(x)){
                return true;
            }
        }
        return false;
    }

}
