package com.think.common.data;

import java.io.Serializable;

public enum ThinkFilterOp implements Serializable {
    EQ,         // = 等于
    NOT_EQ,     //   不等于
    EQ_KEY ,    //  = KEY 等于列
    NOT_EQ_KEY, // != KEY
    IN,         // IN (.... )
    OR,         // OR
    NOT_IN ,    // not in
    LG ,        // > 大于
    LGE,        // >= 大于等于
    LG_KEY,     //  大于KEY
    LGE_KEY,    //  大于等于KEY
    LE,         //   小于
    LEE,       //  小于等于
    LE_KEY,     //  小于 某个KEY
    LEE_KEY,    // 小于等于 KEY
    BETWEEN_AND ,    //介于 之间 ， MYSQL包含边界
    IS_NULL ,   // IS NULL ,是NULL
    IS_NOT_NULL, //
    LIKE;       // LIKE


    /**
     * 可能可以利用索引的 列， indexValue ---- 50
     * @param op
     * @return
     */
    public final static int indexValue(ThinkFilterOp op){
        switch (op){
            case EQ:{
                return  100;
            }
            case IN: {
                return 99;
            }
            case OR:{
                return 98 ;
            }
            case BETWEEN_AND:{
                return 82;
            }
            case LG:{
                return 81;
            }
            case LE:{
                return 80;
            }
            case LGE:{
                return 79;
            }
            case LEE:{
                return 78;
            }
            case LIKE:{
                return 51 ;
            }
            case NOT_IN:{
                return 51;
            }
            default:{
                return 0;
            }
        }
    }





}
