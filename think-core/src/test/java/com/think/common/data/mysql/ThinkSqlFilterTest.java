package com.think.common.data.mysql;

import com.think.common.data.TbA;
import com.think.core.bean.SimplePrimaryEntity;

import static org.junit.Assert.*;

public class ThinkSqlFilterTest {

    public static void main(String[] args) {

        String sqlFilter = "{ \"limit\": 10, \"sortQuery\": {\"key\": \"id\" ,\"sort\": \"desc\"}, \"keyOrType\": \"LIKE\", \"keyOrBody\": {\"name\": \"廖志鹏\", \"workNo\": \"廖志鹏\"}, \"filterBody\": { \"id\": { \"op\": \"LG\", \"type\": \"number\", \"v\": 0 } } }";
        System.out.println(sqlFilter);

        System.out.println("xx");
        ThinkSqlFilter<TbA> simplePrimaryEntityThinkSqlFilter = ThinkSqlFilter.parseFromJSON(sqlFilter, TbA.class);

        System.out.println(simplePrimaryEntityThinkSqlFilter.isKeyOrTypeUsingLike());
        System.out.println(simplePrimaryEntityThinkSqlFilter.getKeyOrMap());
        System.out.println("Xx");
    }


}