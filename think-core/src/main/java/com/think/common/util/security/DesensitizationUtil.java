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


//
//    public static void test(String source){
//        String t = encode(source);
//        System.out.println(t + " - > "  + decode(t));
//    }
//    public static void main(String[] args) {
//
//        test("15957493009");
//        test("13819865889");
//        test("中华人民共和国");
//        test("南京市中观大道890-1号😄");
//        test("南京市中观大道890-2号😄");
//
//        test("#3P");
//        System.out.println(decode("3P"));
//
//        test(" k ");
////
////        for(int i = 15000 ; i <28000; i++){
////            System.out.print("   "+ i + ":" + (char)i );
////            if(i %50==0){
////                System.out.println();
////            }
////        }
//
//
//    }
}
