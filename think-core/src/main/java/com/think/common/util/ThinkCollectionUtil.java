package com.think.common.util;

import com.think.structure.ThinkReadOnlyList;

import java.util.ArrayList;
import java.util.List;

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
}
