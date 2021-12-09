package com.think.common.data;

import com.think.core.bean.SimplePrimaryEntity;

public class TbA extends SimplePrimaryEntity {

    private String workNo;

    private String name ;

    public String getWorkNo() {
        return workNo;
    }

    public void setWorkNo(String workNo) {
        this.workNo = workNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
