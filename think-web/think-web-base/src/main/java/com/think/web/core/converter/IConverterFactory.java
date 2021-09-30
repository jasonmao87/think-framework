package com.think.web.core.converter;


import org.springframework.core.convert.converter.Converter;

/**
 * @Date :2021/9/26
 * @Name :EnumCobnvertFactory
 * @Description : 请输入
 */
public interface IConverterFactory<S,R> {
    <T extends R> Converter<S,T> getConverter(Class<T> targetType);
}
