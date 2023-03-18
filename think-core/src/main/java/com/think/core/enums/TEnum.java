package com.think.core.enums;

import com.think.core.bean.TEnumExplain;

/**
 * @Date :2021/9/26
 * @Name :TEnum
 * @Description : 请输入
 */
//@ThinkEnum
public interface  TEnum {

    TEnumExplain explain();

    default void remark(String remark,EnumExplainMapper mapper){
        mapper.mapping((Enum) this,remark);
    }
}
