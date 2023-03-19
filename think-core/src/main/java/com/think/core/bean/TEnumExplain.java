package com.think.core.bean;

import com.think.core.enums.EnumExplainMapper;
import com.think.core.enums.IEnumInterpreter;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date :2021/9/26
 * @Name :EnumRemark
 * @Description : TEnum解释模型
 */
public class TEnumExplain implements Serializable {
    private static final long serialVersionUID = 1010101010101010010L;

    private static final Map<String,EnumExplainMapper> cache = new HashMap<>();

    private static final void cache(String cacheId , EnumExplainMapper explainMap){

        if (!cache.containsKey(cacheId)) {
            cache.put(cacheId,explainMap);
        }else {
            System.out.println(cache.get(cacheId));
        }
    }


    public static final TEnumExplain build(Enum enumObj , String remark ,IEnumInterpreter interpreter){

        final String typeName = enumObj.getClass().getTypeName();
        String key = enumObj.name();
        if(cache.containsKey(typeName)){
//            final String value = cache.get(emCls.getTypeName()).get(key);
            return new TEnumExplain(key,typeName,remark);
        }else{
            EnumExplainMapper explainMapper = new EnumExplainMapper();
            interpreter.explainEnum(explainMapper);
            cache(typeName,explainMapper);
            return build(enumObj,remark,interpreter);
        }


    }




    @ApiModelProperty(hidden = true)
    private String keyName ;

    @ApiModelProperty(hidden = true)
    private String typeName ;

    @ApiModelProperty(hidden = true)
    private String remark ;
//
//    @ApiModelProperty(hidden = true)
//    Map<String,String> explain;

    @Deprecated
    private TEnumExplain(String keyName ,String typeName, String remark) {
        this.keyName = keyName;
        this.typeName = typeName;
        this.remark = remark;
//        this.explain = explain;
    }

//    @Deprecated
//    private TEnumExplain(String keyName ,String typeName, String remark ,List<Map<String,String>> explainList) {
//        this.keyName = keyName;
//        this.typeName = typeName;
//        this.remark = remark;
//        Map<String,String> map = new HashMap<>();
//        for (Map<String, String> stringStringMap : explainList) {
//            map.putAll(stringStringMap);
//        }
//        this.explain = map;
//    }



    public String getKeyName() {
        return keyName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getRemark() {
        return remark;
    }

//    public Map<String, String> getExplain() {
//        return cache.get();
//    }

    public List<Map<String, String>> getExplain(){
        return cache.get(this.typeName).getList();

    }




}
