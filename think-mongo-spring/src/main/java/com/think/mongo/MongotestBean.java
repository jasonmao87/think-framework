package com.think.mongo;

import com.think.core.annotations.bean.ThinkMongoIndex;
import com.think.core.annotations.bean.ThinkMongoIndexes;
import com.think.core.bean.SimpleMongoEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ThinkMongoIndexes(expireAble = true,expireAtDays = 12 ,indexes = {
        @ThinkMongoIndex(unique = true,keys = {"name","school"})
})
public class MongotestBean extends SimpleMongoEntity {

    private String name ;

    private String school ;


    private String hello ;
}
