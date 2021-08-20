package com.think.data.bean.api;

import java.io.Serializable;
import java.util.Map;

/**
 * @Date :2021/8/18
 * @Name :ThinkUpdateMethodFilter
 * @Description : 请输入
 */
public interface ThinkApiUpdateMethodFilter {
    public Map<String, Serializable> doFilter(Map<String, Serializable> updateMap);
}
