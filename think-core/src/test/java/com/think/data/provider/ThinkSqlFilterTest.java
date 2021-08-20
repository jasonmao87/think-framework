package com.think.data.provider;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.util.FastJsonUtil;

import java.io.Serializable;

/**
 * test for array ... and array []  can free change
 */
public class ThinkSqlFilterTest {





    public static void pr(Serializable... v){
        System.out.println(v.length);
        System.out.println(v[0]);
        hh(v);
    }

    public static void hh(Serializable[] v){
        for(Serializable x : v){
            System.out.println( "\t--> "+ x  + "  ["+x.getClass()+"]");
        }
    }


    public static void main(String[] args) {
        ThinkSqlFilter sqlFilter = ThinkSqlFilter.build(TestTableBean.class)
                .eq("name","沉香")
                .in("address" ,"a","b","vadada",134)
                .or("address" ,"dqeqeqe","b" )
                .isNull("lv")
                .notEq("enable",true);

        sqlFilter.getBeans().forEach(t->{
            System.out.println(t.toString());
            System.out.println(FastJsonUtil.parseToJSON(t));
        });

        pr(123);
        pr(13131,3131);
    }

}