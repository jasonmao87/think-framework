package com.think.core.enums;

import java.util.*;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/26 14:24
 * @description : TODO
 */
public class EnumExplainMapper {

    private List<Map<String,String>> list = new ArrayList<>();

    public void mapping(Enum enumValue, String remark){
        for (Map<String, String> entry : list) {
            if(entry.containsKey(enumValue.name())){
                return;
            }else{

            }
        }
        Map map = new HashMap();
        map.put(enumValue.name(),remark);
        list.add(map);


    }


    public List<Map<String, String>> getList() {
        return list;
    }


    public String get(String key){
        for (Map<String, String> entry : list) {
            if(entry.containsKey(key)){
                return entry.get(key);
            }
        }
        return "";
    }
}
