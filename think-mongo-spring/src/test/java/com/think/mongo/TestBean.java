package com.think.mongo;


import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class TestBean implements Serializable {

    @Id
    private long id ;
    private String name ;
    private String remark ;

    private TestNEntity testNEntity ;

    private List<TestNEntity> list ;
}
