package com.think.common.data;

import com.think.common.data.mysql.ThinkSqlFilter;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/3/24 19:03
 * @description : TODO
 */
public interface IThinkQueryFilter {

    void translateSqlFilter(ThinkSqlFilter sqlFilter);
}
