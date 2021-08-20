package com.think.common.util;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
public class TimeUtil {

    /**
     * @param date
     * @return
     */
    public static final int hourOfTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    /**
     * 当前小时数
     * @return
     */
    public static final int currentHourOfTime(){
        return hourOfTime(DateUtil.now());
    }



    /**
     * 指定时间是 几分
     * @param date
     * @return
     */
    public static final int minuteOfTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static final int currentMinuteOfTime(){
        return minuteOfTime(DateUtil.now());
    }

    public static final int secondOfTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    public static final int currentSecondOfTime(){
        return secondOfTime(DateUtil.now());
    }

    /**
     * 获取机器当前秒数
     * @return
     */
    public static final long seconds(){
        return Instant.now().getEpochSecond();
    }

    public static final long milliseconds(){
        //更快
        return ThinkMilliSecond.currentTimeMillis();
        //更准
//        return Instant.now().toEpochMilli();

    }

    /**
     * 通过 当前秒 获取到 日期类
     * @param seconds
     * @return
     */
    public static Date ofSeconds(long seconds){
        return Date.from( Instant.ofEpochSecond(seconds));
    }

    /**
     * 通过当前毫秒 获取到日期类
     * @param ms
     * @return
     */
    public static Date ofMilliseconds(long ms){
        return new Date(ms);
    }


    /**
     * 是否在这个小时内
     * @param date
     * @return
     */
    public static boolean isThisHour(Date date ){
        if(DateUtil.isToday(date)) {
            return hourOfTime(date) == currentHourOfTime();
        }
        return false;
    }

    /**
     * 是否在 这一分钟内
     * @param date
     * @return
     */
    public static boolean isThisMinutes(Date date){
        if(isThisHour(date)) {
            return minuteOfTime(date) == currentHourOfTime();
        }
        return false;
    }


    /**
     * target是否跟现在比较在   minuteValue 分钟内
     * @param target
     * @param minuteValue
     * @return
     */
    public static boolean isInLastFewMinutes(Date target,int minuteValue){
        long value = target.getTime() - milliseconds();
        if(value<0){
            value = -value;
        }
        return  (value/60000) < minuteValue;
    }



    public static boolean isAM(Date datetime){
        return hourOfTime(datetime) < 12;
    }

    public static boolean isPM(Date datetime){
        return !isAM(datetime);
    }





}
