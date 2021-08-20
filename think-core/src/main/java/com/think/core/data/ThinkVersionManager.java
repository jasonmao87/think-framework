package com.think.core.data;

import com.think.core.annotations.Remark;
import com.think.core.bean._Entity;

import java.util.List;

/**
 * 统一的版本 管理 接口 ，不指定实现
 */
@Deprecated
public interface ThinkVersionManager {

    /**
     * 通过版本号获取 当时版本记录
     * @param targetId
     * @param version
     * @param targetClass
     * @param <T>
     * @return
     */
    @Remark("通过版本号获取历史版本详情")
    <T extends _Entity> T get(long targetId ,int version ,Class<T> targetClass);


    /**
     * 版本记录 清单
     * @param targetId
     * @param startVersion
     * @param endVersion
     * @param targetClass
     * @param <T>
     * @return
     */
    @Remark("获取区间范围的版本信息")
    <T extends _Entity> List<T> versionList(long targetId,int startVersion ,int endVersion ,Class<T> targetClass);

}

