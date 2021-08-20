package com.think.data.provider;

import com.think.core.bean.SimplePrimaryEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class TestTableBean extends SimplePrimaryEntity {

    private boolean enable ;

    private String name;

    private String address;

    private int lv ;

    private Date birthDay ;

}
