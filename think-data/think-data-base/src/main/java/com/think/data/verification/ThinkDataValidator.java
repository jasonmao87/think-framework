package com.think.data.verification;

import com.think.data.Manager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ThinkDataValidator {
    public static final Map<Class,ThinkKeyValidator> validatorHolder = new HashMap<>();

    /**
     * 是否需要做数据校验
     * @return
     */
    public static final boolean isEnable(){
        return Manager.verificationAble();
    }

    /**
     * 默认校验器
     */
    private static final ThinkKeyValidator defaultValidator = new ThinkSimpleDataValidator();


    /**
     * 制定对象注入校验器
     * @param targetClass
     * @param validator
     */
    public static final void registerBeanValidator(Class targetClass ,ThinkKeyValidator validator) {
        validatorHolder.put(targetClass,validator);
    }

    /**
     * 执行校验
     * @param targetClass
     * @param k
     * @param v
     * @throws RuntimeException
     */
    public static final void verification(Class targetClass,String k ,  Object v) throws RuntimeException{
        validatorHolder.getOrDefault(targetClass,defaultValidator).verification(targetClass,k,v);
    }

}
