package com.think.core.security;

import com.think.common.util.security.Base64Util;
import com.think.exception.ThinkException;

import java.util.Random;

/**
 *
 */
public class ThinkSecurityKey {
    private static final String dic  = "QWERTYUIOPLKJHGFDSDAZXCVBNM0123456789" ;
    /**
     * 默认 SecurityKey
     */
    private static String defaultKey = "NUXOV2LDFDTVNCPWVFBXLQVVZ5FQ6Q0X|MjUjMDYjMjEjMDgjMjMjMjkjMTAjMTgjMTUjMTYjMDQjMjMjMjUjMjIjMDkjMDEjMjMjMTUjMjQjMjEjMTAjMDAjMjMjMjMjMjAjMzIjMTUjMDAjMzMjMDAjMjcjMjE=|";


    protected static final void setSecurityKey(String key) throws ThinkException{
        if(verifyKey(key)) {
            defaultKey = key;
        }else{
            throw new ThinkException("非法的SecurityKey ，建议调用 generateKey 创建。 ");
        }
    }

    protected static String getSecurityKey(){
        return defaultKey;
    }

    protected static boolean verifyKey(){
        return verifyKey(defaultKey);
    }

    /**
     * 检查Key的合法性
     * @return
     */
    protected static boolean verifyKey(String key){
        String base64 = key.split("\\|")[1];
        base64  = Base64Util.decodeToString(base64);
        String[] offsets = base64.split("#");
        for(int i = 0 ; i < 32; i++){
            int offset = Integer.parseInt(offsets[i]) ;
            if(dic.charAt(offset) !=key.charAt(i)){
                return false ;
            }
        }
        return true;
    }

    /**
     * 危险方法，很可能导致整个系统 授权等方面的错误 。
     * @return
     */
    protected static String generateKey(){
        StringBuilder offsetString = new StringBuilder("");
        StringBuilder key =new StringBuilder();
        int[] offsets = new int[32];
        for(int i = 0 ; i<offsets.length ;i++ ){
            int offset =new Random().nextInt(dic.length());
            offsets[i] =offset;
            if(i > 0){
                offsetString.append("#");
            }
            if(offset<10){
                offsetString.append("0");
            }
            offsetString.append(offset);
            key.append(dic.charAt(offset));
        }
        key.append("|");
        String base64 = Base64Util.encodeToString(offsetString.toString());
        key.append(base64).append("|");
        return key.toString();
    }

}
