package com.think.common.util.rt;

import com.think.core.bean.SimplePrimaryEntity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ThinkDefaultTargetTypeUtil<T> {
    private Type type;
    private Class<T> classType;

    @SuppressWarnings("unchecked")
    public ThinkDefaultTargetTypeUtil() {
        Type superClass = getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        if (this.type instanceof ParameterizedType) {
            this.classType = (Class<T>) ((ParameterizedType) this.type).getRawType();
        } else {
            this.classType = (Class<T>) this.type;
        }
    }


    public Type getType() {
        return type;
    }

    public Class<T> getClassType() {
        return classType;
    }

    public static void main(String[] args) {
        Class<List<SimplePrimaryEntity>> classType = new ThinkDefaultTargetTypeUtil<List<SimplePrimaryEntity>>().getClassType();
        System.out.println(classType.getName());


    }
}
