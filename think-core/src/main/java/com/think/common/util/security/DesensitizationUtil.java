package com.think.common.util.security;

/**
 * 数据脱敏 / 还原工具类
 */
public class DesensitizationUtil {
    private static final int offSet = (36 + 480) ;

    /**
     * 数据脱敏
     * @param source
     * @return
     */
    public static final String encode(String source){
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            c += offSet;
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
                c += offSet;
                sb.append(c);
            }else{
                sb.append(c);
            }
            // UW\YYX[XXXU
            "".toString();

        }
        return sb.toString();
    }

    public static final String encodeForSelect(String source){
        if(true){
            return encode(source);
        }
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            c += offSet;
            if(c == '\\'){
                sb.append("\\\\");
            }else{
                sb.append(c);
            }
//            sb.append(c);
        }
        return sb.toString();
    }

    public static final String encodeForSelectWithIgnore(String source ,char ignore){
        if(true){
            return encodeWithIgnore(source,ignore);
        }
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            if(c != ignore){
                c += offSet;
                if(c == '\\'){
                    sb.append("\\\\\\\\");
                }else{

                    sb.append(c);
                }
            }else{
                sb.append(c);
            }
            // UW\YYX[XXXU
            "".toString();

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

            c -= offSet;
            sb.append(c);

        }
        return sb.toString();

    }

    public static void main(String[] args) {

        System.out.println(encode("8"));
        System.out.println((int)'\\');
        int plus  =offSet;
        System.out.println((char)('z'+plus));
        for (int i = '0'; i < 'z'; i++) {
            System.out.print( i +"("+(char)i+")" + ">" + (char) (i+plus) + "  ["+(i + plus)+"]|| ") ;
            if(i%10==0){
                System.out.println();
            }
        }
        System.out.println("");
        System.out.println("---------------------");


        System.out.println(Integer.toBinaryString('Ù'));
        System.out.println((int)'(');
        System.out.println((int)')');

//        System.out.println(encodeForSelect("888"));
//
//        int start  =90000;
//        for (int i = start; i < start +100 ; i++) {
//            System.out.println(((char )i) );
//        }
//
//
//
//        char c = '♡';
//
//        System.out.println((int )c );
//
//        System.out.println(encode("dajiba@tom.com"));
    }



}
