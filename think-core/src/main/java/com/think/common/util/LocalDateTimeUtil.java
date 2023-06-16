package com.think.common.util;


import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/11/8 22:22
 * @description :
 */
public class LocalDateTimeUtil {

    private static final ZoneId ZONE_ID = ZoneOffset.systemDefault();
    private static final ZoneOffset ZONE_OFFSET = ZONE_ID.getRules().getOffset(Instant.now());
    //** YYYY-MM-ddThh:mm:ss  ---by JasonMao @ 2022/12/29*/
    static String simpleDate = "^[1-9]\\d{3,3}-[0-1][0-9]-[0-3][0-9]";
    static String simpleTime = "[0-5][0-9]:[0-5][0-9]:[0-5][0-9]";
    static String simpleFull = simpleDate + "T"+simpleTime;
    static String xFull = simpleFull +"[\\.]\\d*$";
    static Pattern compileXFull = compile(xFull);
    static Pattern compileSimpleFull = compile(simpleFull);
    static Pattern compileSimpleTime = compile(simpleTime);
    static Pattern compileSimpleDate = compile(simpleDate);

    private static final List<Pattern> compileList =new ArrayList<>();


    public static final Duration between(Date from ,Date to){
        return Duration.ofMillis(to.getTime() - from.getTime());
    }

    public static final Duration between(LocalDateTime from ,LocalDateTime to){
        return Duration.between(from,to);
    }


    public static final String buildDateTimeString(int year, int month ,int day ,int hour,int minute,int second){
        return DateStrFixer.buildDateTimeString(year, month, day, hour, minute, second);
    }

    public static final String buildDateTimeString(int year, int month,int day,int hour,int minute){
        return buildDateTimeString(year,month,day,hour,minute,0);
    }

    public static final String buildDateTimeString(int year, int month,int day){
        return buildDateTimeString(year,month,day,0,0);
    }




    public static final LocalDateTime valueOfEpochMillis(long milli){
        Instant instant = Instant.ofEpochMilli(milli);
        return valueOfInstant(instant);
    }
    public static final LocalDateTime valueOfInstant(Instant instant){
        return LocalDateTime.ofInstant(instant,ZONE_ID);
    }






    public static final LocalDateTime valueOfString(String source){

        return LocalDateTime.parse(source);
    }

    public static final LocalDateTime valueOfString(String source ,String pattern){
        return LocalDateTime.parse(source,DateTimeFormatter.ofPattern(pattern));
    }


    public static final LocalDateTime valueOfDate(Date date){
        if (date == null) {
            date = new Date();
        }
        return LocalDateTime.ofInstant(date.toInstant(),ZONE_ID);
    }


    public static final Date toDate(LocalDateTime localDateTime){
        return new Date(toEpochMilli(localDateTime));
    }


    public static final Instant toInstant(LocalDateTime localDateTime){
        return localDateTime.toInstant(ZONE_OFFSET);
    }


    public static final long toEpochMilli(LocalDateTime localDateTime){
        return toInstant(localDateTime).toEpochMilli();
    }

    public static final long toEpochSecond(LocalDateTime localDateTime){
        return toInstant(localDateTime).getEpochSecond();
    }

    public static final LocalDateTime now(){
        return LocalDateTime.now(ZONE_ID);
    }

    public static final LocalDateTime beginOfYear(LocalDateTime localDateTime){
        return LocalDateTime.of(localDateTime.getYear(),1,1,0,0,0,0);
    }

    public static final LocalDateTime beginOfYear(){
        return beginOfYear(now());
    }

    public static final LocalDateTime endOfYear(LocalDateTime localDateTime){
        return beginOfYear().plusYears(1).minusSeconds(1);
    }

    public static final LocalDateTime endOfYear(){
        return endOfYear(now());
    }


    public static final LocalDateTime beginOfMonth(LocalDateTime time){
//        return beginOfYear(time).plusMonths(time.getMonthValue() -1);
        return LocalDateTime.of(time.getYear(),time.getMonthValue(),1,0,0,0,0);
    }

    public static final LocalDateTime beginOfMonth(){
        return beginOfMonth(now());
    }

    public static final LocalDateTime endOfMonth(LocalDateTime localDateTime){
        return beginOfMonth(localDateTime).plusMonths(1).minusSeconds(1);
    }
    public static final LocalDateTime endOfMonth(){
        return endOfMonth(now());
    }


    public static final LocalDateTime beginOfDay(LocalDateTime localDateTime){
        return LocalDateTime.of(localDateTime.getYear(),localDateTime.getMonthValue(),localDateTime.getDayOfMonth(),0,0,0,0);
    }

    public static final LocalDateTime beginOfDay(){
        return beginOfDay(now());
    }


    public static final LocalDateTime beginOfYear(int year){
        return LocalDateTime.of(year,1,1,0,0,0,0);
    }

    public static final LocalDateTime beginOfYearMonth(int year,int month){
        return beginOfYear(year).plusMonths(month-1);
    }

    public static final LocalDateTime beginOfYearMonthDay(int year ,int month,int day){
        return beginOfYearMonth(year,month).plusDays(day-1);
    }

    public static final LocalDateTime endOfYear(int year){
        return beginOfYear().plusYears(1).minusSeconds(1);
    }

    public static final LocalDateTime endOfYearMonth(int year,int month){
        return beginOfYearMonth(year,month).plusMonths(1).minusSeconds(1);
    }

    public static final LocalDateTime endOfYearMonthDay(int year, int month,int day){
        return beginOfYearMonthDay(year,month,day).plusDays(1).minusSeconds(1);
    }


//
//    public static final String toLocalDateTimeString(LocalDateTime localDateTime){
//        return localDateTime.toString();
//    }




    public static final String toFmtString(LocalDateTime localDateTime , String datetimeFmt){
        return FormatFactory.getFmt(datetimeFmt).format(localDateTime);
    }


    /**
     * 计算指定月份的最大天数
     * @param year
     * @param month
     * @return
     */
    public static int maxValueForDayOfMonth(int year, int month){
        LocalDateTime date = LocalDateTime.of(year, month, 1, 0, 0);
        return endOfMonth(date).getDayOfMonth();
    }

    /*>>>>>>>>>>>>>>>>>>>>>>-一日当中的最大秒数-<<<<<<<<<<<<<<<<<<<<<<<*/


    public LocalDateTime ofString(String text){
        return LocalDateTime.parse(text);
//        if(text.contains("T")){
//            final String[] textArray = text.split("T");
//            String dateStr = textArray[0];
//            String timeStr = textArray[1];
//        }
//        text = text.trim().replaceAll(" ","T");
//        return LocalDateTime.now();
    }


    public static final class Formatter{
        public static final String toSimpleFullStringCN(Date date){
            return FormatFactory.getSdf(FormatFactory.CN_FMT_yMdHms).format(date);
        }
        public static final String toSimpleFullStringCN(LocalDateTime date){
            return FormatFactory.getFmt(FormatFactory.CN_FMT_yMdHms).format(date);
        }

        public static final String toSimpleFullString(Date date){
            return FormatFactory.getSdf(FormatFactory.FMT_yMdHms).format(date);
        }
        public static final String toSimpleFullString(LocalDateTime date){
            return FormatFactory.getFmt(FormatFactory.FMT_yMdHms).format(date);
        }




        /**  exp :2023年12月23日  ---by JasonMao @ 2023/1/31 12:42 */
        public static final String toSimpleDateStringCN(Date date){
            return FormatFactory.getSdf(FormatFactory.CN_FMT_yMd).format(date);
        }
        public static final String toSimpleDateStringCN(LocalDateTime date){
            return FormatFactory.getFmt(FormatFactory.CN_FMT_yMd).format(date);
        }

        public static final String toSimpleDateString(Date date){
            return FormatFactory.getSdf(FormatFactory.FMT_yMd).format(date);
        }
        public static final String toSimpleDateString(LocalDateTime date){
            return FormatFactory.getFmt(FormatFactory.FMT_yMd).format(date);
        }


    }



    private static class FormatFactory {
        public static final String CN_FMT_yMdHms = "yyyy年MM月dd日 HH:mm:ss";
        public static final String CN_FMT_yMdHm = "yyyy年MM月dd日 HH:mm";
        public static final String CN_FMT_yMd = "yyyy年MM月dd日";

        public static final String FMT_yMdHms = "yyyy-MM-dd HH:mm:ss";
        public static final String FMT_yMdHm = "yyyy-MM-dd HH:mm";
        public static final String FMT_yMd = "yyyy-MM-dd";

//
//        public static final DateTimeFormatter FMT_yMdHm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        public static final DateTimeFormatter FMT_yMdHms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        public static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        private static Map<String,DateTimeFormatter> fmtHolder = null;

        private static Map<String, SimpleDateFormat> sdfHolder = null;

        private static final SimpleDateFormat getSdf(String fmt){
            if(sdfHolder == null){
                sdfHolder = new WeakHashMap<>();
            }
            if (sdfHolder.containsKey(fmt) == false) {
                try{
                    SimpleDateFormat pattern = new SimpleDateFormat(fmt);
                    sdfHolder.put(fmt,pattern);
                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException("不合适且无法被解析的日期时间格式化表达式:" + fmt ,e);
                }catch (Exception e){
                    throw e ;
                }
            }
            SimpleDateFormat simpleDateFormat = sdfHolder.get(fmt);
            return simpleDateFormat;
        }

        private static DateTimeFormatter getFmt(String fmt){
            if(fmtHolder == null){
                fmtHolder = new WeakHashMap<>();
            }
            if (fmtHolder.containsKey(fmt) == false) {
                try{
                    DateTimeFormatter pattern = DateTimeFormatter.ofPattern(fmt);
                    fmtHolder.put(fmt,pattern);
                }catch (IllegalArgumentException e){
                    throw new IllegalArgumentException("不合适且无法被解析的日期时间格式化表达式:" + fmt ,e);
                }
            }
            DateTimeFormatter dateTimeFormatter = fmtHolder.get(fmt);
            return dateTimeFormatter;
        }


        public static final String format(LocalDateTime localDateTime ,DateTimeFormatter formatter){
            return localDateTime.format(formatter);
        }


    }


    private static final class DateStrFixer{
        private static final String fixNum(int v,int len,int maxValue ){
            if(maxValue>0){
                v = v>maxValue?maxValue:v;
            }
            StringBuilder sb =new StringBuilder(v);
            while (sb.length() < len) {
                sb.insert(0,"0");
            }
            return sb.toString();
        }

        protected static String fixYearNum(int v){
            return fixNum(v,4,9999);
        }
        protected static String fixMonthNum(int v){
            return fixNum(v,2,12);
        }
        protected static String fixDayNum(int v){
            return fixNum(v,2,31);
        }
        protected static String fixHourNum(int v){
            return fixNum(v,2,23);
        }
        protected static String fixMinuteNum(int v){
            return fixNum(v,2,59);
        }

        protected static String fixSecondNum(int v){
            return fixMinuteNum(v);
        }

        protected static String buildDateTimeString(int year, int month,int day, int hour,int minute, int second){
            StringBuilder dateSb = new StringBuilder()
                    .append(fixYearNum(year)).append("-")
                    .append(fixMonthNum(month)).append("-")
                    .append(fixDayNum(day)).append("T")
                    .append(fixHourNum(hour)).append(":")
                    .append(fixMinuteNum(minute)).append(":")
                    .append(fixSecondNum(second)).append("");
            return dateSb.toString();
        }

        protected static String buildDateTimeString(int year, int month, int day, int hour, int minute){
            return buildDateTimeString(year,month,day,hour,minute,0);
        }



    }


    public static void main(String[] args) {

        System.out.println(between(LocalDateTime.now(),LocalDateTime.now().plusDays(-1)).toHours());

    }


//    public static long secondOfYear(LocalDateTime localDateTime){
//    }
}
