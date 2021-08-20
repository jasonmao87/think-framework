package com.think.core.annotations.bean;

import com.think.core.annotations.Remark;

import java.lang.annotation.*;

@Deprecated
@Documented
@Remark("此注解，标识这个值需要存储到K-V结构中，该键保存得是k，需要在k-v系统中找到真正得值")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ThinkMongoKV {
}
