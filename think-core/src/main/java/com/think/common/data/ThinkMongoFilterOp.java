package com.think.common.data;

public enum  ThinkMongoFilterOp {
    EQ,         // = 等于
    IN,         // IN (.... )
    OR,         // OR
    LG ,        // > 大于
    LGE,        // >= 大于等于

    LE,         //   小于
    LEE,       //  小于等于
    BETWEEN_AND ,    //介于 之间 ， MYSQL包含边界
    LIKE;       // LIKE
    public final static int indexValue(ThinkMongoFilterOp op){
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
            default:{
                return 0;
            }
        }
    }



}
