package com.think.common.util.security;

/**
 * 数据脱敏 / 还原工具类
 */
public class DesensitizationUtil {


    /**
     * 数据脱敏
     * @param source
     * @return
     */
    public static final String encode(String source){
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            c += 36;
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 数据脱敏  忽略 字符  ，仅用于 like 查询时候 ！
     * @param source
     * @param ignore
     * @return
     */
    public static final String encodeWithIgnore(String source ,char ignore){
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            if(c != ignore){
                c += 36;
                sb.append(c);
            }else{
                sb.append(c);
            }

        }
        return sb.toString();
    }


    /**
     * 脱敏数据还原
     * @param source
     * @return
     */
    public static final String decode(String source){
        StringBuilder sb = new StringBuilder("");

        for(char c : source.toCharArray()){

            c -= 36;
            sb.append(c);

        }
        return sb.toString();

    }

    public static void main(String[] args) {
        System.out.println(encode("dajiba@tom.com"));
    }


}
