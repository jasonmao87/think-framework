package com.think.core.bean.util;

import com.think.common.util.StringUtil;
import com.think.core.bean.SimplePrimaryEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Date :2021/4/23
 * @Name :TbX
 * @Description : 请输入
 */
@Data
@Accessors(chain = true)
public class TbX extends SimplePrimaryEntity {

    private String name;

    private Date date;

    private boolean b = System.currentTimeMillis()%2==0;


    private String man = StringUtil.randomNumber(10);

    static {
        System.out.println("HELLO I AM HERE");
    }


    public static void print(){
        System.out.println(StringUtil.randomNumber(13));
    }
}
