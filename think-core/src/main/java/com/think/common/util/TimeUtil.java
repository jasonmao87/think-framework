package com.think.common.util;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具类
 * @author JASONMAO
 */
public class TimeUtil {


    /** 30 秒的 毫秒值 */
    public static final long MILLIS_OF_30_SECONDS= 30000L;

    public static final long MILLIS_OF_15_SECONDS = 15000L;

    public static final long MILLIS_OF_5_MINUTES = 300000L;


    public static final long MILLIS_OF_SECONDS(int seconds){
        return 1000L * seconds;
    }

    public static final long MILLIS_OF_MINUTES(int minutes){
        return 60000L* minutes  ;
    }

    public static final long MILLIS_OF_HOURS(int hours){
        return 3600000L* hours;
    }




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

    public static final ThinkTimer timer(){
        return new ThinkTimer();
    }

    public static void sleepForSeconds(int durationSeconds){
        sleep(durationSeconds,TimeUnit.SECONDS);
    }

    public static void sleep(int duration,TimeUnit unit){
        try{
            switch (unit){
                case MILLISECONDS:{
                    TimeUnit.MILLISECONDS.sleep(duration);
                    break;
                }
                case SECONDS:{
                    TimeUnit.SECONDS.sleep(duration);
                    break;
                }
                case MINUTES:{
                    TimeUnit.MINUTES.sleep(duration);
                    break;
                }
                case HOURS:{
                    TimeUnit.HOURS.sleep(duration);
                    break;
                }
                case DAYS:{
                    TimeUnit.DAYS.sleep(duration);
                    break;
                }
                case NANOSECONDS:{
                    TimeUnit.NANOSECONDS.sleep(duration);
                    break;
                }
                case MICROSECONDS:{
                    TimeUnit.MICROSECONDS.sleep(duration);
                    break;
                }
            }
        }catch (Exception e){}

    }





}

class ThinkTimer{

    private long begin ;

    protected ThinkTimer(){
        begin = ThinkMilliSecond.currentTimeMillis();
    }

    public long duration(TimeUnit unit){

        long duration = ThinkMilliSecond.currentTimeMillis() - begin;
        switch (unit){
            case MILLISECONDS:{
                return duration;
            }
            case NANOSECONDS:{
                return TimeUnit.MILLISECONDS.toNanos(duration);
            }
            case MICROSECONDS:{
                return TimeUnit.MILLISECONDS.toMicros(duration);
            }
            case SECONDS:{
                return TimeUnit.MILLISECONDS.toSeconds(duration);
            }
            case MINUTES:{
                return TimeUnit.MILLISECONDS.toMinutes(duration);
            }
            case HOURS:{
                return TimeUnit.MILLISECONDS.toHours(duration);
            }
            case DAYS:{
                return TimeUnit.MILLISECONDS.toDays(duration);
            }
        }
        return duration;
    }

}



