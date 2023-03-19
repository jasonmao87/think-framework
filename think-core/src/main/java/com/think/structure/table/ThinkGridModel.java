package com.think.structure.table;

import com.think.core.annotations.Remark;

import java.io.Serializable;
import java.util.List;

/**
 * @Date :2021/6/1
 * @Name :ThinkTable
 * @Description : Table 模型结构，简化的Excel 模型
 */
public class ThinkGridModel implements Serializable {
    private static final long serialVersionUID = -4520411971083832057L;
    @Remark("唯一id")
    private String id ;
    @Remark("标题栏目内容")
    private String title ;

    @Remark("列模型")
    private List<ThinkGridColumnModel> columnModels;

    @Remark("允许渲染数据的columns")
    private List<ThinkGridColumnModel> dataColumns;



}
