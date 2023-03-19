package com.think.data.model;

import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.core.bean._Entity;
import com.think.data.Manager;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

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
