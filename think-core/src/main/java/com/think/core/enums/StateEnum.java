package com.think.core.enums;

import com.think.core.bean.TEnumExplain;

public enum StateEnum implements TStateEnum<StateEnum> {
    ;






    @Override
    public TEnumExplain explain(String keyName) {
        throw new RuntimeException("需要使用Think状态枚举，请自行构建继承StateEnum的枚举实现");
    }

    @Override
    public StateEnum emptyEnum() {
        throw new RuntimeException("需要使用Think状态枚举，请自行构建继承StateEnum的枚举实现");
    }

    @Override
    public StateEnum unsafeChangeToState(StateEnum tStateEnum) throws Exception {
        return null;
    }

    @Override
    public StateEnum changeToState(StateEnum tStateEnum) throws Exception {
        return null;
    }




}
