package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.util.FastJsonUtil;
import com.think.common.util.L;

import java.io.Serializable;

/**
 * test for array ... and array []  can free change
 */
public class ThinkSqlFilterTest {





    public static void pr(Serializable... v){
        L.print(v.length);
        L.print(v[0]);
        hh(v);
    }

    public static void hh(Serializable[] v){
        for(Serializable x : v){
            L.print( "\t--> "+ x  + "  ["+x.getClass()+"]");
        }
    }


    public  void main() {
        ThinkSqlFilter sqlFilter = ThinkSqlFilter.build(TestTableBean.class)
                .eq("name","沉香")
                .in("address" ,"a","b","vadada",134)
                .or("address" ,"dqeqeqe","b" )
                .isNull("lv")
                .notEq("enable",true);

        sqlFilter.getBeans().forEach(t->{
            L.print(t.toString());
            L.print(FastJsonUtil.parseToJSON(t));
        });

        pr(123);
        pr(13131,3131);
    }

}