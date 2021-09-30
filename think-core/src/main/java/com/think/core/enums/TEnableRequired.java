package com.think.core.enums;

import com.think.core.bean.TEnumExplain;

import java.util.ArrayList;
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

    public static List<Map<String,String>> list(){
        List<Map<String,String>> list = new ArrayList<>();
        for(TEnableRequired enableRequiredEnum : TEnableRequired.values()){
            Map<String,String> map =new HashMap<>();
            map.put(enableRequiredEnum.name(),toCN(enableRequiredEnum));
            list.add(map);
        }
        return list;
    }

    @Override
    public TEnumExplain explain(String keyName) {
        return new TEnumExplain(keyName,getClass().getTypeName(),"启禁用匹配要求",list());
    }
}
