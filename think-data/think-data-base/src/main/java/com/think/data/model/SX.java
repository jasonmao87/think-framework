package com.think.data.model;

import com.think.core.annotations.bean.ThinkTable;
import com.think.core.bean.SimplePrimaryEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Date :2021/9/4
 * @Name :SX
 * @Description : 请输入
 */
@Data
@Accessors(chain = true)
@ThinkTable(value = "dadax")
public class SX  extends SimplePrimaryEntity {

    private int age =1 ;
    private String name ;
}
