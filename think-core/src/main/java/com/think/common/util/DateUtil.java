package com.think.common.util;

import com.think.core.annotations.Remark;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
@Slf4j
public class DateUtil extends TimeUtil{

    public static final String FMT_YMD = "yyyy-MM-dd";

    private static long lastCallNowTime = 0L;
    private static Date lastCallNowDate = new Date();

    public static final Calendar getCalendar(){
        return Calendar.getInstance();
    }
    public static final Calendar getCalendar(Long time){
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(time);
        return calendar;
    }
    public static Calendar getCalendar(Date date){
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        return calendar;
    }



    public static final String toFmtString(Date date,String fmt){
        return new SimpleDateFormat(fmt).format(date);
    }

    /**
     * 获取日期年份
     * @param date
     * @return
     */
    public static final int year(Date date){
        Calendar cal = getCalendar();
        if(date!=null){
            cal.setTime(date);
        }
        return cal.get(Calendar.YEAR);
    }
    /**
     * 当前年份
     */
    public static final int year(){
        return year(null);
    }

    public static final String yearStr(int strLen){
        String dateStr =""+ year();
        while (dateStr.length() <strLen){
            dateStr = "0" + dateStr;
        }
        return dateStr;

    }


    /**
     * 指定时间季度
     * @param date
     * @return
     */
    public static final int quarter(Date date){
        int m = month(date);
        if(m > 0 && m < 4 ){
            // 1 2 3
            return 1;
        }else if(m>3 && m < 7){
            // 4 5 6
            return 2;
        }else if(m>6 && m < 10){
            // 7 8 9
            return 3;
        }else if(m > 9 && m < 13){
            // 10 11 12
            return 4;
        }
        return -1 ;
    }

    /**
     * 当前季度
     * @return
     */
    public static final int quarter(){
        return quarter(DateUtil.now());
    }





    /**
     * 获取日期月份
     * @param date
     * @return
     */
    public static final int month(Date date){
        Calendar cal = getCalendar();
        if(date!=null){
            cal.setTime(date);
        }
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 当前月份
     * @return
     */
    public static final int month(){
        return month(null);
    }

    public static final String monthStr(int strLen){
        String dateStr =""+ month();
        while (dateStr.length() <strLen){
            dateStr = "0" + dateStr;
        }
        return dateStr;
    }

    /**
     * 获得当前日
     * @return
     */
    public static final int day(){
        return getCalendar().get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 返回 当前日的 string 格式， len 等于长度 ，前缀补充0
     * @param strLen
     * @return
     */
    public static final String dayStr(int strLen){
        String dateStr =""+ day();
        while (dateStr.length() <strLen){
            dateStr = "0" + dateStr;
        }
        return dateStr;


    }

    /**
     * 获取指定时间的 日
     * @param date
     * @return
     */
    public static final int date(Date date){
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


    public static final int day(Date date){
        return date(date);
    }

    /**
     * 当前日
     * @return
     */
    public static final int date(){
         return date(  now() );
    }

    /**
     * 相当于 1970-1-1 ,我们可以理解为 0.或者替代NULL
     * @return
     */
    public static final Date zeroDate(){
        return new Date(0L);
    }

    /**
     * 当前时间
     * @return
     */
    public static final Date now(){

        long now =ThinkMilliSecond.currentTimeMillis();
        if(now == lastCallNowTime){
            return lastCallNowDate;
        }else{
            lastCallNowTime = now;
            lastCallNowDate = new Date();
            return lastCallNowDate;
        }
    }


    public static final Date nowByNew(){
        return new Date();
    }


//    public static void main(String[] args) {
//        int max = 99999999;
//        t1(max);
//        t2(max);
//
//    result:
//            99999999 of now ----73
//            99999999 of nowtime  ----274

//        System.exit(-1);
//    }
//
//    static void t1(int max ){
//        long start = System.currentTimeMillis();
//        int x.log = 0;
//        for(int i =0 ; i< max; i++){
//            Date now = DateUtil.now();
//            if (now.getTime() >10000) {
//                x.log ++ ;
//            }
//        }
//        long end = System.currentTimeMillis();
//        System.out.println( x.log +" of now ----" + (end -start));
//    }
//
//
//    static void t2(int max ){
//        long start = System.currentTimeMillis();
//        int x.log = 0;
//        for(int i =0 ; i< max; i++){
//            Date now = DateUtil.nowTime();
//            if (now.getTime() >10000) {
//                x.log ++ ;
//            }
//        }
//        long end = System.currentTimeMillis();
//        System.out.println( x.log +" of nowtime  ----" + (end -start));
//
//    }
    /**
     * 通过年月日 解析一个时间
     * @param year
     * @param month
     * @param date
     * @return
     */
    public static final Date from(int year ,int month ,int date){
        Calendar calendar = getCalendar();
        calendar.set(year,month -1 ,date);
        Date d =  calendar.getTime();
        return d;
    }

    public static final Date buildNewDate(int year,int month,int date,int hour,int minute,int second){
        Calendar calendar = getCalendar();
        calendar.set(year,month-1,date,hour,minute,second);
        return calendar.getTime();
    }

    /**
     * 是否当年
     * @param date
     * @return
     */
    public static final boolean isCurrentYear(Date date){
        return year(date) == year();
    }

    /**
     * 是否当月
     * @param date
     * @return
     */
    public static final boolean isCurrentMonth(Date date){
        if(isCurrentYear(date)){
            return month(date) == month();
        }
        return false;
    }

    /**
     * 是否今天
     * @param date
     * @return
     */
    public static final boolean isToday(Date date){
        if(isCurrentMonth(date)){
            return date(date) == date();
        }
        return false;
    }


    /**
     * 比较2个日期是否是同一年
     * @param date1
     * @param date2
     * @return
     */
    public static final boolean isSameYear(Date date1 ,Date date2){
        return year(date1) == year(date2);
    }

    /**
     * 比较两个日期是否是在同一个月
     * @param date1
     * @param date2
     * @return
     */
    public static final boolean isSameMonth(Date date1 ,Date date2){
        if(isSameYear(date1,date2)){
            return month(date1) == month(date2);
        }
        return false;
    }

    /**
     * 比较两个日期是否是在同一天
     * @param date1
     * @param date2
     * @return
     */
    public static final boolean isSameDate(Date date1 ,Date date2){
        if(isSameMonth(date1,date2)){
            return date(date1) == date(date2);
        }
        return false;
    }

    /**
     * 是否在同一天的同一个小时
     * @param date1
     * @param date2
     * @return
     */

    public static final boolean isSameHour(Date date1 ,Date date2){
        if(isSameDate(date1,date2)){
            return  DateUtil.hourOfTime(date1) == DateUtil.hourOfTime(date2);
        }
        return false;
    }

    /**
     * 是否在同一天，统一小时的同一分钟
     * @param date1
     * @param date2
     * @return
     */
    public static final boolean isSameMinute(Date date1 ,Date date2){
        if(isSameHour(date1,date2)){
            return minuteOfTime(date1) == minuteOfTime(date2);
        }
        return false;
    }



    /**
     * 指定日期开始时间 即 0:0:0
     * @param date
     * @return
     */
    public static final Date beginOfDate(Date date){
        Calendar c = getCalendar(date);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();
    }

    /**
     * 获取今天 0点得日期类
     * @return
     */
    public static final Date beginOfToday(){
        return beginOfDate(now());
    }

    /**
     * 获取指定日期1日0点的时间
     * @param date
     * @return
     */
    public static final Date beginOfMonth(Date date){
        Calendar c = getCalendar(date);
        c.set(Calendar.DATE,1);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();

    }

    /**
     * 获取本月开始的时间
     * @return
     */
    public static final Date beginOfCurrentMonth(){
      return beginOfMonth(now());
    }


    /**
     * 指定日期最后1毫秒的时间
     * @param date
     * @return
     */
    public static final Date endOfDate(Date date){
        Calendar c = getCalendar(date);
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();
    }

    /**
     * 今日最后1毫秒的时间
     * @return
     */
    public static final Date endOfToday(){
        return endOfDate(now());
    }

    /**
     * 指定日期 该月最后1毫秒的时间
     * @param date
     * @return
     */
    public static final Date endOfMonth(Date date){
        Date t = beginOfMonth(date);
        Calendar c = getCalendar(t);
        c.add(Calendar.MONTH,1);
        c.add(Calendar.SECOND,-1);
        return c.getTime();
    }

    /**
     * 本月最后1毫秒的时间
     * @return
     */
    public static final Date endOfCurrentMonth(){
        return endOfMonth(now());
    }


    public static final int dayOfDay2SubtractDay1(Date day1 ,Date day2){
        day1 = beginOfDate(day1);
        day2 =beginOfDate(day2);
        long sub = day2.getTime() - day1.getTime();
        sub /=1000; // 秒
        sub /=3600 ; //小时 = 60 *60 分*秒
        sub /=24;
        return Long.valueOf(sub).intValue();
    }

    /**
     * date2比date1多的天数
     * @param date1
     * @param date2
     * @return
     */
    public static final int differentDays(Date date1,Date date2)
    {
        int value = 0 ;
        boolean needTran = false; //是否需要反转
        if(date1.getTime() -  date2.getTime()>0){
            needTran = true;
            Date t = null;
            t = date1 ;
            date1 = date2;
            date2 = t ;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)//不是同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            value =  timeDistance + (day2-day1) ;
        }
        else {//同一年
            value =  day2-day1;
        }
        return needTran?(-value):value;
    }


    /************/
    /**
     * 时间计算基础方法
     * @param source
     * @param type
     * @param number
     * @return
     */
    private static final Date computeAdd(Date source,final int type ,int number){
        Calendar c =getCalendar();
        c.setTime(source);
        c.add(type ,number);
        return c.getTime();
    }

    /**
     * 时间计算 -- 加指定年数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddYears(Date source ,int number){
        return computeAdd(source,Calendar.YEAR,number);
    }
    /**
     * 时间计算 -- 加指定月数 (负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddMonths(Date source ,int number){
        return computeAdd(source,Calendar.MONTH,number);
    }



    /**
     * 时间计算 -- 加指定日数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddDays(Date source ,int number){
        return computeAdd(source,Calendar.DATE,number);
    }

    /**
     * 时间计算 -- 加指定小时数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddHours(Date source ,int number){
        return computeAdd(source,Calendar.HOUR_OF_DAY,number);
    }
    /**
     * 时间计算 -- 加指定分数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddMinutes(Date source ,int number){
        return computeAdd(source,Calendar.MINUTE,number);
    }
    /**
     * 时间计算 -- 加指定秒数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddSeconds(Date source ,int number){
        return computeAdd(source,Calendar.SECOND,number);
    }


    public static final Date valueOfString(String datetime,Date defaultDateValue){
        if (StringUtil.isEmpty(datetime)) {
            return defaultDateValue;
        }
        try{
            return new Date(datetime);
        }catch (Exception e){}

        String fmtStr =  datetime.replaceAll("/","-");
        try{
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fmtStr);
        }catch (Exception e){
        }
        try{
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fmtStr);
        }catch (Exception e){
        }
        try{
            return new SimpleDateFormat("yyyy-MM-dd").parse(fmtStr);
        }catch (Exception e){
        }
        return defaultDateValue;

    }

    public static final Date valueOfString(String datetime){
       return valueOfString(datetime,DateUtil.zeroDate());
    }


    /**
     * 获得中文得星期几
     * @param date
     * @return
     */
    public static String getWeekZhCN(Date date){
        String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        Calendar cal =getCalendar(date);

        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return weeks[week_index];
    }

    public static String getWeekZhCN(){
        return getWeekZhCN(DateUtil.now());
    }


    public static int getWeek(Date date){
        return getCalendar(date).get(Calendar.DAY_OF_WEEK);
    }

    public static int getWeek(){
        return getWeek(DateUtil.now());
    }


    public static final int dayOfYear(Date date){
        Calendar calendar = getCalendar(date);
        int i = calendar.get(Calendar.DAY_OF_YEAR);
        return i;
    }

    public static final int dayOfYear(){
        return dayOfYear(DateUtil.now());
    }



    public static final Date beginOfYear(Date date){
        if(date == null){
            date = DateUtil.now();
        }
        Calendar c = getCalendar(date);
        c.set(Calendar.DAY_OF_YEAR,1);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();
    }

    public static final Date beginOfCurrentYear(){
        return beginOfYear(DateUtil.now());
    }

    @Deprecated
    public static final Date endOfYear(Date date){
        if(date == null){
            date = DateUtil.now();
        }        Date date1 = beginOfYear(date);
        Date date2 = DateUtil.computeAddYears(date1, 1);
        Date date3 = DateUtil.computeAddMinutes(date2, -1);
        return endOfDate(date3);
    }

    public static final Date endOfCurrentYear(){
        return endOfYear(DateUtil.now());
    }


    @Remark("是否普通工作日，即 周一至周五 ，大部分场景适用")
    public static final boolean isSimpleWeekDay(Date date){
        int week = DateUtil.getWeek();
        if(week == 1 || week == 7){
            return false;
        }
        return true;
    }
    @Remark("是否普通工作日，即 周一至周五 ，大部分场景适用")
    public static final boolean isSimpleWeekDay(){
        return isSimpleWeekDay(DateUtil.now());
    }

        @Remark("是否普通中国适用的休息日，大部分场景适用（周六，周日,10.1 -10，7 ，5.1 都为休息日）")
    public static final boolean isSimpleChinesRestDay(Date date){
        if(month(date) == 10 ){
            //10.1 -7 标记未 休息日
            return day(date) < 7;
        }
        if(month(date) == 5 ){
            return  day(date) ==1;
        }
        return !isSimpleWeekDay(date);

    }
    @Remark("是否普通中国适用的休息日，大部分场景适用（周六，周日,10.1 -10，7 ，5.1 都为休息日）")
    public static final boolean isSimpleChinesRestDay(){
        return isSimpleChinesRestDay(DateUtil.now());
    }


    public static Date nextDay(){
        return DateUtil.nextDay(DateUtil.now());
    }
    public static Date nextDay(Date date){
        return DateUtil.computeAddDays(date,1);
    }


    public static Date nextMonday(Date date){
        date = nextDay(date);
        while (getWeek(date) !=2){
            date= nextDay();
        }
        return date;

    }
    public static Date nextMonday(){
        return nextMonday(DateUtil.now());
    }
}

