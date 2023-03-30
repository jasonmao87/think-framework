package com.think.common.util;

import com.think.core.annotations.Remark;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 日期工具类
 */
@Slf4j
public class DateUtil extends TimeUtil{

    public static final String FMT_YMD = "yyyy-MM-dd";

    private static long lastCallNowTime = 0L;
    private static Date lastCallNowDate = new Date();

    private static final Date zero =new Date(0);


    public static final String toFmtYMd(Date date){
        return new SimpleDateFormat(FMT_YMD).format(date);
    }

    public static final String toFmtYMdHms(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
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
        return LocalDateTimeUtil.valueOfDate(date).getYear();
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
       return LocalDateTimeUtil.valueOfDate(date).getMonthValue();
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
        return day(null);
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
        return LocalDateTimeUtil.valueOfDate(date).getDayOfMonth();
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
        if(zero.getTime() == 0L){
            return zero;
        }else{
            zero.setTime(0L);
            return zero;
        }
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
    /**
     * 通过年月日 解析一个时间
     * @param year
     * @param month
     * @param date
     * @return
     */
    public static final Date from(int year ,int month ,int date){
        return buildNewDate(year,month,date,0,0,0);

    }

    public static final Date buildNewDate(int year,int month,int date,int hour,int minute,int second){
        LocalDateTime localDateTime = LocalDateTime.of(year, month, date,hour,minute,second);
        return LocalDateTimeUtil.toDate(localDateTime);

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
        LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(date);
        LocalDateTime of = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(),
                0, 0, 0, 0);
        return LocalDateTimeUtil.toDate(of);

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
        LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(date);
        LocalDateTime of = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonthValue(), 1,
                0, 0, 0, 0);
        return LocalDateTimeUtil.toDate(of);
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
        //next day
        LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(date).plusDays(1);
        LocalDateTime of = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(),
                0, 0, 0, 0).minusNanos(1);
        return LocalDateTimeUtil.toDate(of);
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
        final LocalDateTime endOfMonth = LocalDateTimeUtil.endOfMonth(LocalDateTimeUtil.valueOfDate(date));
        return LocalDateTimeUtil.toDate(endOfMonth);
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

        final LocalDateTime d1 = LocalDateTimeUtil.valueOfDate(date1);
        final LocalDateTime d2 = LocalDateTimeUtil.valueOfDate(date2);
        System.out.println(d1);
        System.out.println(d2);
        long l = Duration.between(d1, d2).getSeconds();
        return Long.valueOf(TimeUnit.SECONDS.toDays(l)).intValue();

    }




    private static Date localdatetime2Date(LocalDateTime localDateTime){
        return LocalDateTimeUtil.toDate(localDateTime);
    }
    /**
     * 时间计算 -- 加指定年数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddYears(Date source ,int number){
        final LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(source).plusYears(number);
        return localdatetime2Date(localDateTime);
    }
    /**
     * 时间计算 -- 加指定月数 (负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddMonths(Date source ,int number){
        final LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(source).plusMonths(number);
        return localdatetime2Date(localDateTime);
    }



    /**
     * 时间计算 -- 加指定日数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddDays(Date source ,int number){
        final LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(source).plusDays(number);
        return localdatetime2Date(localDateTime);    }

    /**
     * 时间计算 -- 加指定小时数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddHours(Date source ,int number){
        final LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(source).plusHours(number);
        return localdatetime2Date(localDateTime);    }
    /**
     * 时间计算 -- 加指定分数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddMinutes(Date source ,int number){
        final LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(source).plusMinutes(number);
        return localdatetime2Date(localDateTime);
    }
    /**
     * 时间计算 -- 加指定秒数(负数为减)
     * @param source
     * @param number
     * @return
     */
    public static final Date computeAddSeconds(Date source ,int number){
        final LocalDateTime localDateTime = LocalDateTimeUtil.valueOfDate(source).plusSeconds(number);
        return localdatetime2Date(localDateTime);

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
        String[] weeks = { "星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
         return weeks[getWeek(date)-1];
    }




    public static String getWeekZhCN(){
        return getWeekZhCN(DateUtil.now());
    }


    public static int getWeek(Date date){
        int index = LocalDateTimeUtil.valueOfDate(date).getDayOfWeek().getValue();
        return index;
    }

    public static int getWeek(){
        return getWeek(DateUtil.now());
    }


    public static final int dayOfYear(Date date){
        return LocalDateTimeUtil.valueOfDate(date).getDayOfYear();
    }

    public static final int dayOfYear(){
        return dayOfYear(DateUtil.now());
    }



    public static final Date beginOfYear(Date date){
        final LocalDateTime localDateTime = LocalDateTimeUtil.beginOfYear(LocalDateTimeUtil.valueOfDate(date));
        return localdatetime2Date(localDateTime);
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
//        System.out.println(toFmtString(date,FMT_YMD));
        while (getWeek(date) !=2){
//            System.out.println(getWeek(date) + " " + getWeekZhCN(date)  + " " + toFmtString(date,FMT_YMD) );
            date= nextDay(date);
        }
        return date;
    }


    public static Date nextMonday(){
        return nextMonday(DateUtil.now());
    }


}

