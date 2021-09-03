package com.think.data.model;

import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.util.FastJsonUtil;
import com.think.core.annotations.bean.ThinkTable;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import com.think.data.Manager;
import com.think.exception.ThinkRuntimeException;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @Date :2021/9/4
 * @Name :ThinkUpdaterParse
 * @Description : 请输入
 */
@Slf4j
public class ThinkUpdaterBuilder {



    public static final<T extends _Entity> ThinkUpdateMapper<T> build(T t ,String... keys) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        ThinkTableModel tableModel = Manager.getModelBuilder().get(t.getClass());
        ThinkUpdateMapper<? extends _Entity> updateMapper = ThinkUpdateMapper.build(t.getClass());
        if(t.getId() ==null){
            throw new ThinkRuntimeException("对象尚未持久化或无法找到主键id，无法构建");
        }
        updateMapper.setTargetDataId(t.getId());
//        for(String k : keys){
//            tableModel.containsSortKey(k);
//            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(k, t.getClass());
//
//            Introspector.getBeanInfo(t.getClass()).getMethodDescriptors()[0].getMethod().in
//             propertyDescriptor.
//            Method readMethod = propertyDescriptor.getReadMethod();
//            System.out.println(readMethod.getName());
//            System.out.println(readMethod.getReturnType());
//
//        }





        return null;
    }

    @Override
    public String toString() {

        return super.toString();
    }


    public static void main(String[] args) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        SX dada = new SX().setAge(12)
                .setName("dada");
        dada.setId(123L);
        ThinkUpdateMapper<SX> build = (ThinkUpdateMapper<SX>) build(dada, "age", "name");

    }
}
