package com.think.data.bean.api;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.core.annotations.Remark;

@Remark("执行string filter 前调用方法")
public interface ThinkBeanFilterAction {



    void action(ThinkSqlFilter stringSqlFilter);

}
