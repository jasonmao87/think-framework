package com.think.core.enums;

import com.think.common.util.FastJsonUtil;
import com.think.core.bean.TEnumExplain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date :2021/9/29
 * @Name :EnableRequired
 * @Description : 请输入
 */
public enum TEnableRequired implements TEnum {
    MATCH_ENABLE ,MATCH_DISABLE ,MATCH_ALL ;



    public static String toCN(TEnableRequired e){
        switch (e){
            case MATCH_ALL:     return "匹配全部";
            case MATCH_DISABLE: return "匹配禁用";
            case MATCH_ENABLE:  return "匹配启用";
        }
        return null;
    }

    public static Map<String,String> explainMap(){
        Map<String,String> map =new HashMap<>();
        for(TEnableRequired enableRequiredEnum : TEnableRequired.values()){
            map.put(enableRequiredEnum.name(),toCN(enableRequiredEnum));
        }

        return map;
    }





    @Override
    public TEnumExplain explain() {
        return TEnumExplain.build(this,"启禁用配置要求",(mapper)->{
            for (TEnableRequired value : TEnableRequired.values()) {
                switch (value){
                    case MATCH_ALL: mapper.mapping(value,"匹配全部");
                    case MATCH_DISABLE:mapper.mapping(value,"匹配禁用");
                    case MATCH_ENABLE:mapper.mapping(value,"匹配启用");
                }
            }
        });
    }



    public static final List<Map<String,String>> list(){
        final TEnableRequired value = values()[0];

        return value.explain().getExplain();
    }

    public static void main(String[] args) {

        System.out.println(FastJsonUtil.parseToJSON(MATCH_ENABLE.explain().getExplain()));
        System.out.println(FastJsonUtil.parseToJSON(list()));
    }
}
