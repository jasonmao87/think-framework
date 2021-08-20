package com.think.common.util;

import com.think.structure.ThinkReadOnlyList;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Date :2021/6/9
 * @Name :ThinkCollectionUtil
 * @Description : 请输入
 */
public class ThinkCollectionUtil {

    private static final List EMPTY_LIST = new ThinkReadOnlyList();

    public static final List emptyList(){
        return EMPTY_LIST;
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
