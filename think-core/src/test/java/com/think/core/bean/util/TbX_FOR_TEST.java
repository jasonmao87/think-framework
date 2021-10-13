package com.think.core.bean.util;

import com.think.common.util.StringUtil;
import com.think.core.bean.SimplePrimaryEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @Date :2021/4/23
 * @Name :TbX
 * @Description : 请输入
 */
@Data
@Slf4j
@Accessors(chain = true)
public class TbX_FOR_TEST extends SimplePrimaryEntity {

    private String name;

    private Date date;

    private boolean b = System.currentTimeMillis()%2==0;


    private String man = StringUtil.randomNumber(10);

    static {
        if (log.isInfoEnabled()) {
            log.info("HELLO I AM HERE");
        }
    }


    public static void print(){
        if (log.isInfoEnabled()) {
            log.info(StringUtil.randomNumber(13));
        }
    }
}
