package com.think.common.data.mysql.subSql;

import com.think.common.data.ThinkFilterOp;
import com.think.common.data.mysql.ThinkFilterBean;
import com.think.core.annotations.Remark;
import com.think.core.bean._Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/2/22 15:35
 * @description :
 */
public class ThinkSubSqlFilter<T extends _Entity> implements Serializable {


    private String mainQueryKey ;
    private String subQueryKey ;

    private Class<T> targetClass;

    private List<ThinkFilterBean> beans = new ArrayList<>();

    @Remark("操作符")
    private ThinkFilterOp op ;





}
