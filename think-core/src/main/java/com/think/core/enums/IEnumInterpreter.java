package com.think.core.enums;

import com.think.core.bean.TEnumExplain;

import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/5/26 13:40
 * @description : TODO
 */
public interface IEnumInterpreter {

    /**
     * 解释枚举
     * @return
     */
    void explainEnum(EnumExplainMapper explainMapper);

}
