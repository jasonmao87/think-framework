package com.think.common.util;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ThinkConsoleLogger {
    private static boolean enable  = true;
    private static boolean includeTime = true;
    private static boolean fmtJson = false;
    private static boolean containsThreadInfo  = false;
    private static String dateFMT = "yyyy-MM-dd HH:mm:ss";


    private static final SimpleDateFormat getSdf(){
        return new SimpleDateFormat(dateFMT);
    }

    public static final void enableConsole(){
        enable = true;
    }

    public static final void disableConsole(){
        enable = false;
    }
    public static void logIncludeTime(boolean b){
        includeTime = b;
    }

    public static void logFormatJsonString(boolean b){
        fmtJson = b;
    }

    public static void logContainsThreadInfo(boolean b){
        containsThreadInfo = b;
    }

    private static void buildLogAndPrint(Object info){
        if(enable){
            StringBuilder logInfoSb = new StringBuilder("");
            if(includeTime){
                logInfoSb.append( getSdf().format(DateUtil.now())).append(" ");
            }
            if(containsThreadInfo){
                logInfoSb.append("ThreadInfo[ id : ").append(Thread.currentThread().getId()).append(" name :").append(Thread.currentThread().getName()).append("] ");
            }
            logInfoSb.append(" CONSOLE INFO >>");
            if(info instanceof Number || info instanceof String || info instanceof Date){
                logInfoSb.append( info);
            }
//            if(info instanceof Map || info instanceof JSONObject){
//                if() 下次实现
//            }

            logInfoSb.append(" ").append(info);
            try {
                String json = FastJsonUtil.toPrettyString(info);
                logInfoSb.append(json);
            } catch (Exception e) {
                logInfoSb.append(info);
            }
            System.out.println(logInfoSb.toString());
        }
    }

    public static final void print(String info){
        buildLogAndPrint(info);
    }
}
