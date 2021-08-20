package com.think.structure.table;

import com.think.core.annotations.Remark;

import java.io.Serializable;
import java.util.List;

/**
 * @Date :2021/6/1
 * @Name :ThinkTableColumn
 * @Description : 请输入
 */
public class ThinkGridColumnModel implements Serializable {

    private static final long serialVersionUID = -7348316547638870634L;
    @Remark("模型id")
    private String id ;
    @Remark("占据列数")
    private int columnSize ;

    @Remark("列名")
    private String name ;

    @Remark(value = "子列模型",description = "可以理解为 我们的表头部分分了两层，如统计数据为主列，下面又分了几个子列（参加人数、达标人数，达标百分比等）")
    private List<ThinkGridColumnModel> subColumnModels ;

    @Remark("允许渲染数据，只允许在最子层列模型渲染数据。")
    private boolean dateAble;

    @Remark("数据类型，long,doule ,date ,boolean ,string")
    private String dataType ;

}
