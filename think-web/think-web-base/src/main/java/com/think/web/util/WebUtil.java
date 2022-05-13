package com.think.web.util;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.common.util.security.Base64Util;
import com.think.common.util.security.SHAUtil;
import com.think.core.security.AccessKey;
import com.think.core.security.WebSecurityUtil;
import com.think.core.security.token.ThinkSecurityToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * web 工具类
 */
@Slf4j
public class WebUtil {

    private static Set<String> excludeAuthUriPrefixSet = new HashSet<>();


    /**
     * 是否是一个和HTTP请求
     * @return
     */
    public static final boolean isHttpRequest(){
        return getRequest() !=null;
    }

    /**
     * 添加排除授权的 uri 前缀
     * @param noAuthPrefix
     */
    public static final void addExcludedAuthUriPrefix(String noAuthPrefix){
        try{
            if(log.isDebugEnabled()){
                log.debug("{} 添加到无需授权前缀清单" ,noAuthPrefix);
            }
            if(noAuthPrefix.endsWith("/")){
                throw new RuntimeException("请不要以‘/’结尾");
            }
            if(!noAuthPrefix.startsWith("/")){
                noAuthPrefix = "/" + noAuthPrefix;
            }
            excludeAuthUriPrefixSet.add(noAuthPrefix);
        }catch (Exception e){
        }
    }

    /**
     * 检查是否需要进行授权访问检查
     * @param uri
     * @return
     */
    public static final boolean isAuthorizationRequired(String uri){
        if(!uri.startsWith("/")){
            uri = "/" + uri;
        }
        for(String prefix : excludeAuthUriPrefixSet){
            if(uri.startsWith(prefix)){
                return false;
            }
        }
        return true;
    }


    /**
     * 获取到 request
     * @return
     */
    public static HttpServletRequest getRequest() {
        try{
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }catch (Exception e){
            return null;
        }
    }

    public static HttpServletResponse getResponse(){
        try{
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        }catch (Exception e){
            return null;
        }

    }
    /**
     * 读取Header中的值
     * @param k
     * @return
     */
    public static String headerValue(String k) {
        try{
            String value = getRequest().getHeader(k);
            if(StringUtil.isEmpty(value)){
                value = getRequest().getHeader(k.toLowerCase());
            }
            return value;
        }catch (Exception e){}
        return "";
    }





    /**
     * 读取访问的URI 资源
     * @return
     */
    public static String uri(){
        return getRequest().getRequestURI().replaceAll("//","/");
    }

    /**
     * 读取游览器 UA 字符串
     * @return
     */
    public static String userAgent(){
        return headerValue("User-Agent");
//        return getRequest().getHeader("User-Agent");
    }

    /**
     * 客户端得ip地址
     * @return
     */
    public static String ip(){
        HttpServletRequest request = getRequest();
        /**
         * 使用了  RemoteIpFilter 直接使用即可
         */
        String ip = request.getRemoteAddr();
        if(ip.contains("localhost") || ip.contains("0:0:0:0:0:0")){
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static String clientId(){
        String ua = userAgent();
        String ip = ip();
        return SHAUtil.sha1(ua + ip);
    }



    /**
     * 客户端得ip地址： 转换为 long 类型存储！更加节省空间
     * @return
     */
    public static Long longIp(){
        String[] array = ip().split("\\.");
        Long ip = 0L ;
        for(int i = 3 ;i>0; --i){
            ip  |= Long.parseLong(array[3 - i])<<(i*8);
        }
        return ip;
    }
    /**
     * 客户端得ip地址，16进制字符串
     * @return
     */
    public static String hexIp(){
        return Long.toHexString(longIp());
    }
    /**
     * long 类型得ip地址转成 看得懂得ip地址字符串
     * @param ip
     * @return
     */
    public static String ipFromLong(Long ip){
        Long a = ip>>24;
        ip = ip - (a<<24) ;
        Long b = ip>>16;
        ip = ip - (b<<16);
        Long c = ip>>8;
        ip = ip -(c<<8);
        Long d = ip;
        return a+"."+b+"."+c+"."+d;
    }

    /**
     *     16进制得IP 地址 转成 肉银看得懂得ip字符串
     * @param hex
     * @return
     */
    public static String ipFromHex(String hex){
        return ipFromLong(Long.parseLong(hex,16));
    }

    /**
     * 是否ajax 请求
     * @return
     */
    public static boolean isAjax(){
        String xRequestedWith = getRequest().getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1)
        {
            return true;
        }
        return false;
    }




    /**
     * 获取HTTP METHOD
     * @return
     */
    public static final String httpMethod(){
        return httpMethod(getRequest());
    }

    public static final String httpMethod(HttpServletRequest request){
        return request.getMethod();
    }
    public static final Map requestParams(HttpServletRequest request){
        if(request.getMethod().equalsIgnoreCase("get")) {
            return request.getParameterMap();
        }else{
            Map<String,String> map = new HashMap();
            Enumeration<String> parameterNames = request.getParameterNames();
            if(parameterNames !=null) {
                while (parameterNames.hasMoreElements()) {
                    String k = parameterNames.nextElement();
                    map.put(k, request.getParameter(k));
                }
            }
            return map;
        }
    }

    public static final Map requestParams(){
        return requestParams(WebUtil.getRequest());
    }


    /**
     * 判断是否是新的UV
     *  cookie tdate=如果是当日新建的 cookie ，那么认定为不是新的UV
     *  如果不是当日新建的Cookie，那么依然认定为新的UV
     *  即，判断 tdate 是否等于 当日的 yyyyMMdd
     * @return
     */
    public static final boolean isNewUV(){
        final String key = "tdate";
        String targetDateString = DateUtil.toFmtString(DateUtil.now(),"yyyyMMdd");
        String v = getCookieValue(key) ;
        if(v == null){
            //如果没有读取到 cookie ，那么 设置一个 ！那么这个 请求，可以被认定为 新的UV
            setCookieValue(key , targetDateString,24);
            return false;
        }else{
            return v.equals(targetDateString);
        }
    }

    public static final String getCookieValue(String key){
        HttpServletRequest request = getRequest();
        Cookie[] cks = request.getCookies();
        if(cks == null || cks.length == 0){
            return null;
        }
        for (Cookie ck: request.getCookies() ){
            if(ck.getName().equalsIgnoreCase(key) ){
                return ck.getValue();
            }
        }
        return null;
    }


    /**
     * 设置cookie
     * @param k
     * @param v
     * @param useAge  生命周期 （单位小时）
     */
    public static final void setCookieValue(String k  , String v , int useAge){
        Cookie cookie = new Cookie(k,v);
        cookie.setMaxAge(60*60* useAge);
        cookie.setPath("/");
        getResponse().addCookie(cookie);
    }


    public static final AccessKey getUserAccessKey(){
        try {
            if(getToken()!=null){
                return WebSecurityUtil.getInstance().getAccessKeyValueOfAkString(getRequest().getHeader("accessKey"),WebUtil.userAgent());
            }else{
                return null;
            }
        }catch (Exception e){}
        return null;
    }

    public static Optional<ThinkSecurityToken> getToken(){
        ThinkSecurityToken token ;
        String tokenString = WebUtil.headerValue("token");
        if(StringUtil.isEmpty(tokenString)){
            return Optional.ofNullable(null);
        }

        try {
            tokenString = Base64Util.decodeToString(tokenString);
            token = ThinkSecurityToken.valueOfJsonString(tokenString);

        }catch (Exception e){
            token = null;
            if(log.isTraceEnabled()){
                log.debug("source token string is : {} ",tokenString);
                log.debug("exception while try to catch header token :" ,e);
            }
        }
        return Optional.ofNullable(token);
    }







//    public static final Map<String,String[]> requestParamsMap(HttpServletRequest request){
//        Map<String,String[]> paramsMap= new HashMap<>();
//        这方法明显不靠谱 ，仔细取研究 request.getContentType() post的几种方式
//        Enumeration<String> parameterNames = request.getParameterNames();
//        while (parameterNames.hasMoreElements()){
//            String paramName = parameterNames.nextElement();
//            paramsMap.put(paramName,request.getParameterValues(paramName));
//        }
//        return paramsMap;
//    }
//
//    public static final Map<String,String> requestHeaderMap(HttpServletRequest request){
//        request.getContentType()
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()){
//
//        }
//
//    }


}
