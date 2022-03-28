package com.think.data.provider;

import com.mysql.cj.api.xdevapi.CreateTableStatement;
import com.sun.org.apache.regexp.internal.RE;
import com.think.common.result.ThinkResult;
import com.think.common.result.state.ResultCode;
import org.springframework.dao.DataAccessException;

import java.sql.SQLException;

public class DaoExceptionTranslater {



    public static ThinkResult<Integer> updateFilterEmpty(){
        return ThinkResult.fail("执行修改对象条件非法，不允许使用KEY IN EMPTY 的条件执行修改",ResultCode.REQUEST_PARAM_ERROR);
    }

    public static ThinkResult translate(Exception exception){
        String message = exception.getMessage();

        ThinkResult result = null ;
        if(message.contains("Duplicate")){
            result= ThinkResult.fail("主键或唯一值约束冲突", ResultCode.REQUEST_PARAM_ERROR);
        }
        else if(message.contains("Data too long for column")){
            result= ThinkResult.fail(message.split("Data too long for column")[1].split("at")[0] + "参数字段长度超过了限制！", ResultCode.REQUEST_PARAM_ERROR);
        }
        else if(message.contains("doesn't have a default value")){
            result= ThinkResult.fail(message.split("Field")[1].replaceAll("doesn't have a default value.*" ,"不能为NULL"), ResultCode.REQUEST_PARAM_ERROR);
        }
        else if(message.contains("cannot be null")){
            result= ThinkResult.fail(message.split("java.sql.SQLIntegrityConstraintViolationException:")[1].replaceAll("Column","列名").replaceAll("cannot be null","不能为NULL"), ResultCode.REQUEST_PARAM_ERROR);
        }
        else if(message.contains("bad SQL grammar")){
            result= ThinkResult.fail("BAD SQL ", ResultCode.SERVER_ERROR);
        }else {
            String tableNotExist = ".*Table '(.*)' doesn't exist.*";
            if (message.matches(tableNotExist)) {
                result = ThinkResult.fail("数据库对应表不存在！", ResultCode.SERVER_ERROR);
            }
        }
        if (null != result) {
            result.setThrowable(exception);
        }else {
            result = ThinkResult.error(exception);
        }


        return  result;
    }


}
