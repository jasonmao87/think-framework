package com.think.data.model;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkColumn;
import com.think.core.annotations.bean.ThinkTable;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/8/29 10:46
 * @description : TODO
 */
@Data
@ThinkTable(value = "sys_struct_auto_alter_log",partitionAble = false)
public class TbStructAlterSqlLog extends SimplePrimaryEntity {
    private static final long serialVersionUID = -3563718286657249216L;


    @ThinkColumn(length = 256)
    private String tbObjectClass ="" ;

    @ThinkColumn(length = 128)
    private String tableName = "";


    @Remark("执行的SQL")
    @ThinkColumn(usingText = true)
    private String alterSql ="";

    @Remark("逆向操作的SQL")
    @ThinkColumn(usingText = true)
    private String reverseSql = "";

    private boolean stateFlag = false;




    @Override
    public ThinkSqlFilter buildEmptyFilter(int limit) {
        return null;
    }

    @Override
    public ThinkSqlFilter buildEmptyFilter(int limit, Class aClass) {
        return null;
    }

    @Override
    public ThinkUpdateMapper buildEmptyUpdateMapper() {
        return null;
    }

    @Override
    public ThinkUpdateMapper buildEmptyUpdateMapper(Class aClass) {
        return null;
    }

    @Override
    public ThinkUpdateMapper buildUpdateMapperWithCurrentId() {
        return null;
    }
}
