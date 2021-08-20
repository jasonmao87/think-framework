package com.think.core.bean;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ThinkVersionEntity extends SimpleMongoEntity implements Serializable {
    private static final long serialVersionUID = -7138656262273482187L;

    private long targetId ;

    private Object data;

    private int version;

}
