package com.think.core.enums;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/3/17 10:56
 * @description : TODO
 */
public interface TStateEnum<T extends Enum> extends TEnum{


    T emptyEnum();

    T unsafeChangeToState(T t) throws Exception;

    T changeToState(T t) throws Exception;


}
