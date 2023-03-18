package com.think.core.bean.state;

import com.think.common.util.DateUtil;
import com.think.core.annotations.Remark;
import com.think.core.enums.TEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/3/17 10:50
 * @description : TODO
 */
public class ThinkState<T extends TEnum> implements Serializable {

    @Remark("状态用途名称")
    private String name ;

    @Remark("最后改变时间")
    private Date sateTime  = DateUtil.zeroDate();


    @Remark("当前枚举值")
    T stateValue;




}
