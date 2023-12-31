package com.think.common.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.think.core.annotations.Remark;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class StringUtil {

    public static final String UTF8 =  new String("UTF-8").intern();

    public static final String EMPTY_JSON = new String("{}").intern();
    public static final String EMPTY_STRING = new String("").intern();


    private static char[] allCharDic = {
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O',
            'P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3',
            '4','5','6','7','8','9','a','b','c','d','e','f','g','h','i',
            'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x',
            'y','z'
    };

    public static boolean isEmpty(String s){
        return s== null || s.trim().length() == 0 ;
    }

    /**
     * 判断字符串是否非空
     * @param str 如果不为空，则返回true
     * @return
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    public static final boolean hasLength(String str){
        return str != null && !str.isEmpty();
    }


    /**
     * 检查是否有空格
     * @param str
     * @return
     */
    public static boolean containsWhitespace(String str) {
        if (!hasLength(str)) {
            return false;
        } else {
            int strLen = str.length();

            for(int i = 0; i < strLen; ++i) {
                if (Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }


    /**
     * 判断是否全都为数字
     */
    public static boolean isAllNumber(String input) {
        boolean isNum = input.matches("[0-9]+");
        return isNum;
    }


    /**
     * 是否仅仅包含 数字 和字母
     * @param input
     * @return
     */
    public static boolean isOnlyNumberAndLetter(String input){
        String format = "^[0-9a-zA-Z]+[0-9a-zA-Z-_]$";
        return input.matches(format);
    }


    /**
     * 是否为合法的Email格式
     * @param email
     * @return
     */
    public static boolean isEmail(String email)
    {// 验证邮箱的正则表达式
        String format = "[0-9a-zA-Z]+[0-9a-zA-Z-_]{0,32}[@][0-9a-zA-Z-_]{2,}[.]\\p{Lower}{2,}";
        //p{Alpha}:内容是必选的，和字母字符[\p{Lower}\p{Upper}]等价。如：200896@163.com不是合法的。
        //w{2,15}: 2~15个[a-zA-Z_0-9]字符；w{}内容是必选的。 如：dyh@152.com是合法的。
        //[a-z0-9]{3,}：至少三个[a-z0-9]字符,[]内的是必选的；如：dyh200896@16.com是不合法的。
        //[.]:'.'号时必选的； 如：dyh200896@163com是不合法的。
        //p{Lower}{2,}小写字母，两个以上。如：dyh200896@163.c是不合法的。
        if (email.matches(format.trim()))
        {
            return true;// 邮箱名合法，返回true
        }
        else
        {
            return false;// 邮箱名不合法，返回false
        }
    }

    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("[-_]", "");
    }



    public static String randomUppercaseStr(int len){
        StringBuilder sb = new StringBuilder();
        int max = 26 ;
        for(int i = 0 ; i < len ;i ++){
            sb.append( allCharDic[RandomUtil.nextInt()%max]);
        }
        return sb.toString();
    }

    public static String randomStr(int len)  {
        StringBuilder sb = new StringBuilder();
        int max = allCharDic.length ;
        for(int i=0; i < len ;i++){
            sb.append( allCharDic[RandomUtil.nextInt()%max]);
        }
        return sb.toString();
    }


    public static String randomNumber(int len) throws RuntimeException{

        if(len>10){
            throw new RuntimeException("随机数值最大长度不可超过10");
        }else if(len<2){
            throw new RuntimeException("随机数值最小长度不可小于2");
        }
        StringBuilder randomNumberString = new StringBuilder("");
        for (int i = 0; i <len; i++) {
            int x = RandomUtil.nextInt()%9;
            if(x<0){
                x = -x ;
            }
            if(i == 0  && x == 0){
                return randomNumber(len);
            }
            randomNumberString.append(x);
        }
//        int seek = 9 ;
//        int fix = 1;
//        for(int i=0 ;i< len -1 ;i++){
//            seek *= 10;
//            fix *= 10;
//        }
//        return  (new Random().nextInt(seek) + fix) +"";
        return randomNumberString.toString();

    }



    /**
     * 输出 yyyy-MM-dd的日期格式
     * @param date
     * @return
     */
    public static String fmtAsDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }


    /**
     * 输出 yyyy-MM-dd HH:mm:ss 格式的日期时间字符串
     * @param date
     * @return
     */
    public static String fmtAsDatetime(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" ).format(date);
    }

    public static final boolean isUpcase(char c){
        if('A' <= c  && 'Z'>= c){
            return true;
        }
        return false;
    }

    public static final String base64Encode(String source){
        try{
            return new String(Base64.getEncoder().encode(source.getBytes(StringUtil.UTF8)),StringUtil.UTF8) ;
//            return new BASE64Encoder().encode(source.getBytes(StringUtil.UTF8));
        }catch (Exception e){}
        return null;
    }

    public static final String base64Decode(String base64Str){
        try{

            return new String( Base64.getDecoder().decode(base64Str),StringUtil.UTF8);
        }catch (Exception e){}
        return null;
    }

    public static final String arrayToString(Object[] arrs){
        StringBuilder str = new StringBuilder("");
        int index = 0 ;
        for(Object t : arrs){
//            if(index > 0){
//            }
            str.append("\n[").append(index).append("\t\t:  （").append(t.getClass()).append(")").append(t).append("\t]");
            index ++ ;
        }
        return str.toString();
    }


    /**
     * 替换中文符号未英文符号
     * @param symbol
     * @return
     */
    private static final char replaceChineseSymbol(char symbol){
        switch ( symbol){
            case '，' :  return ',';
            case '。' :  return '.';
            case '？' :  return '?';
            case '【' :  return '[';
            case '】' :  return ']';
            case '；' :  return ';';
            case '’' :  return '\'';
            case '“' :  return '"';
            case '！' :  return '!';
            case '：' :  return ':';
            case '、' :  return '\\';
            case '|' :  return '|';
            case '（' :  return '(';
            case '）' :  return ')';
            case '—' : return '-';
            case '》' : return '>';
            case '《' : return '<';

        }
        return symbol;
    }

    /**
     *  仅保留字母和数字
     * @param str
     * @return
     */
    public static final String replaceOtherButKeepLetterAndNumber(String str,String replaceValue){
        StringBuilder stringBuilder = new StringBuilder();
        for(char c : str.toCharArray()){
            if( '0'<= c && '9'>= c){
                stringBuilder.append(c);
            }else if('a'<=c && 'z'>= c){
                stringBuilder.append(c);
            }else if('A'<=c && 'Z'>= c){
                stringBuilder.append(c);
            }else{
                stringBuilder.append(replaceValue);
            }
        }
        return stringBuilder.toString();

    }


    /**
     * 替换中文符号 为 英文符号
     * @param str
     * @return
     */
    public static final String replaceZhSymbolToEnSymbol(String str){
        StringBuilder stringBuilder = new StringBuilder();
        for(char x : str.toCharArray()){
            stringBuilder.append(replaceChineseSymbol(x));
        }
        return stringBuilder.toString();
    }


    /**
     * 读取拼音简写
     * @param zh
     * @return
     */
    public static final String getShortPinyin(String zh){
        if(isNotEmpty(zh)){
            return PinyinHelper.getShortPinyin(zh).trim();
        }
        return "";
    }



    /**
     * 获取拼音简写并移除所有符号 和空格
     * @param zh
     * @return
     */
    public static final String getShortPinyinWithoutSymbol(String zh){
        return getShortPinyinReplaceSymbolWithSpecialCode(zh,"");
    }

    /**
     * 获取拼音简写并 用特殊字符替换 所有符号 和空格
     * @param zh
     * @param specialCode
     * @return
     */
    public static final String getShortPinyinReplaceSymbolWithSpecialCode(String zh ,String specialCode){
        String str = getShortPinyin(zh);
        str = replaceOtherButKeepLetterAndNumber(str,specialCode);
        return str.replaceAll(specialCode+specialCode,"");
    }

    public static final String getFullPinyin(String zh){
        return getFullPinyinReplaceSymbolWithSpecialCode(zh,"");
    }

    /**
     * 获取完整拼音并 用特殊字符替换 所有符号 和空格
     * @param zh
     * @param specialCode
     * @return
     */
    public static final String  getFullPinyinReplaceSymbolWithSpecialCode(String zh, String specialCode){
        if(isNotEmpty(zh)) {
            String str = PinyinHelper.convertToPinyinString(zh, "", PinyinFormat.WITHOUT_TONE);
            str = replaceOtherButKeepLetterAndNumber(str,specialCode);
            return str.replaceAll(specialCode+specialCode,"");
        }
        return "";
    }

    private static String x(long... x){
        String s ;
        if(x.length == 1 ){
            s = x[0] +"";
            while (s.length() < 4){
                s = "0"+s;
            }
        }else{
            double dv = 1.0 * x[1]/ x[0];
            s =dv + "";
            if(s.length() > 4){
                s = s.substring(0,4);
            }


        }
        return s ;

    }






    /**
     * 移除所有空白
     * @param str
     * @return
     */
    public static String removeAllBlank(String str){
        return str.replaceAll(" " ,"").replaceAll(" ","");
    }

    /**
     * 提取数字和其他信息 得到 数组
     * @param source
     * @return
     */
    public static final String[] extractNumbersAndOtherAsArray(String source){
        if(source.length()  == 0 ){
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        boolean number  = false;
        char first = source.charAt(0);
        if( '0'<=first  &&  first<='9' ){
            number = true;
        }
        String t =  first+ "";
        for(int i =1 ; i < source.length() ; i++){
            char c = source.charAt(i);
            if(number){
                if( '0'<=c && c<='9' ){
                    t += c ;
                }else{
                    list.add(t + "");
                    t =  c + "";
                    number = false;

                }
            }else{
                if( '0'<=c && c<='9'  ){
                    list.add(t + "");
                    t =  c + "";
                    number =true;
                }else{
                    t += c ;
                }
            }
        }
        if(t.length()>0) {
            list.add(t);
        }
        return list.toArray(new String[list.size()]);
    }


    /**
     * 拼接字符串
     * @param appends
     * @return
     */
    public static final String buildStringAppend( Object... appends){
        TVerification.valueOf(appends).throwIfNull("传入参数不能为NULL");
        if(appends.length == 1 && appends[0] !=null){
            if(appends[0] instanceof Appendable){
                throw new RuntimeException("不能使用Append作为参数中的内容");
            }
            return appends[0].toString();
        }
        StringBuilder sb = new StringBuilder();
        for (Object t : appends){
            if (t!=null) {
                if(t instanceof Appendable){
                    throw new RuntimeException("不能使用Append作为参数中的内容");
                }
                sb.append(t);
            }
        }
        return sb.toString();

    }

    public static final String getEmptyJsonStr(){
        return EMPTY_JSON;
    }
    public static final String getEmptyStr(){
        return EMPTY_STRING;
    }


    public static final String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    public static final String toHexString(Number v ){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("0x");
        String hex = Long.toHexString(v.longValue());

        int appendLength = 0 ;


        if(hex.length() <2){
            appendLength = 2 - hex.length();
        }else if(hex.length() < 4){
           appendLength = 4- hex.length();
        }else if(hex.length()<8){
            appendLength = 8 - hex.length();
        }
        while (appendLength  > 0){
            appendLength --;
            stringBuilder.append("0");
        }

        stringBuilder.append(hex);
        return stringBuilder.toString();
    }


    public String format(String template,Object... params){
        final String s = template.replaceAll("\\{}", "%s");

        return String.format(s, params);

    }


    public static String fixStringIfNullAsEmpty(String str){
        return str!=null?str:getEmptyStr();
    }


    /**
     * 进制 转换
     * @param source
     * @param sourceRadix
     * @param targetRadix
     * @return
     */
    public static final String radixChange(String source ,int sourceRadix ,int targetRadix) throws RuntimeException{
        BigInteger bg = new BigInteger(source,sourceRadix);
        return bg.toString(targetRadix);
    }


    @Remark("约束String 的长度")
    public final static String fixedStrLenForPrintAndLogger(String source , int len){
        if(len ==0){
            return "";
        }
        StringBuilder sb ;
        if(source==null){
            sb =new StringBuilder("");
            while (sb.length()<len){
                sb.append(" ");
            }
            return sb.toString();
        }


        byte[] bytes = source.getBytes();
        byte[] target =new byte[len];
        int maxIndex = (target.length>bytes.length?bytes.length:target.length);
        for (int i = 0; i < maxIndex; i++) {
            target[i] = bytes[i];
        }
        String fixedString = new String(target);
        return fixedString;




        /**

        if(len == source.length() ){
            return source;
        }else if(len > source.length()){
            sb= new StringBuilder("");
            while ( (sb.length() + source.length() )< len){
                sb.append(" ");
            }
            return sb.append(source).toString();
        } else{
            byte[] bytes = source.getBytes();
            byte[] target =new byte[len*2];
            int maxIndex = (target.length>bytes.length?bytes.length:target.length);
            for (int i = 0; i < maxIndex; i++) {
                target[i] = bytes[i];
            }
            if(maxIndex<target.length){
                sb.append(new String())
            }





            sb = new StringBuilder();
            sb.append(source.substring(0,len));
            if(sb.length()>5){
                sb.replace(sb.length()-4,sb.length()-1,"...");
            }
            return sb.toString();
        }
         **/
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        String s = "我爱茜茜";


        byte[] bytes = s.getBytes("GB2312");

        System.out.println(s);


        String s2 = new String(bytes, "ISO-8859-1");


        System.out.println(s2);

    }
}
