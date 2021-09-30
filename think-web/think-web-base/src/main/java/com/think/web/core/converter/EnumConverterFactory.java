package com.think.web.core.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Date :2021/9/26
 * @Name :EnumConvertFactory
 * @Description : 请输入
 */
@Component
@Slf4j
public class EnumConverterFactory implements IConverterFactory<String,ConvertAbleEnum> {

    @Override
    public <T extends ConvertAbleEnum> Converter<String, T> getConverter(Class<T> targetType) {
        new StringToCovertAbleEnum<>(targetType);
        return null;
    }

    @SuppressWarnings("all")
    private static class StringToCovertAbleEnum<T extends ConvertAbleEnum> implements Converter<String, T> {
        private Class<T> targerType;
        public StringToCovertAbleEnum(Class<T> targerType) {
            this.targerType = targerType;
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isEmpty(source)) {
                return null;
            }
            return (T) EnumConverterFactory.getIEnum(this.targerType, source);
        }
    }


    public static <T extends ConvertAbleEnum> Object getIEnum(Class<T> targerType, String source) {
        for (T enumObj : targerType.getEnumConstants()) {
            if (source.equals(String.valueOf(enumObj.getStringValue()))) {
                return enumObj;
            }
        }
        return null;
    }
}
