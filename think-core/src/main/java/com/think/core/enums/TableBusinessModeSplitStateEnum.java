package com.think.core.enums;

import com.think.common.util.TVerification;

public enum TableBusinessModeSplitStateEnum {
    ENABLE,
    DISABLE,
    DEFAULT;


    private static TableBusinessModeSplitStateEnum DEFAULT_VALUE = null;

    public static void defaultStateSet(TableBusinessModeSplitStateEnum state){
        synchronized (TableBusinessModeSplitStateEnum.class) {
            if (DEFAULT_VALUE != null) {
                throw new RuntimeException("业务区分隔离模式默认值已经设置过了");
            }
            if (state == DEFAULT){
                throw new RuntimeException("业务区分隔离模式默认值不能设置为DEFAULT");
            }
            if (state == null){
                throw new RuntimeException("业务区分隔离模式默认值不能设置为NULL");
            }
            DEFAULT_VALUE = state;
        }
    }

    public boolean isEnable(){
        if(this == DEFAULT){
            TVerification.valueOf(DEFAULT_VALUE).throwIfNull("为设置业务区分隔离模式默认值");
        }
        TVerification.valueOf(DEFAULT_VALUE == DEFAULT).throwIfTrue("业务区分隔离模式默认值设置错误,只允许设置为ENABLE或者DISABLE");
        return this == ENABLE;
    }

}
