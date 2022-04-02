package com.think.core.bean;

import com.think.common.util.DateUtil;
import com.think.common.util.RandomUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.annotations.Remark;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/2 16:06
 * @description : Think 排期类
 */
@Slf4j
public class ThinkSchedule implements Serializable {

    private  int year ;

    private  int month ;

    private  int day ;

    private  int hour;

    private  int minute;

    private  int second;

    private long lastHitTime = 0L;

 

    private static int random(int max){
        int i = RandomUtil.nextInt() % max;
        if(i < 1){
            i = 1;
        }else if(i >= max){
            i -- ;
        }
        return i;
    }


    @Remark("指定秒，每分钟执行1次 ")
    public static final ThinkSchedule buildEverMinuteSchedule( int second){
        return new ThinkSchedule(
                -1,
                -1,
                -1,
                -1,
                -1,
                second
        );
    }



    @Remark("指定分秒，每小时执行1次 ")
    public static final ThinkSchedule buildEverHourSchedule(int minute,int second){
        return new ThinkSchedule(
                -1,
                -1,
                -1,
                -1,
               minute,second
        );
    }


    @Remark("指定时分秒，每日命中一次")
    public static final ThinkSchedule buildEveryDaySchedule(int hour ,int minute,int second){
        return new ThinkSchedule(-1, -1, -1, hour,minute,second
        );
    }



    @Remark("指定日 时分秒 ，每月执行1次，注意2月会被自动调整到27日  ")
    public static final ThinkSchedule buildEveryMonthSchedule(int day,int hour , int minute,int second) {
        return new ThinkSchedule(
                -1,
                -1,
              day,hour,minute,second
        );
    }


    public ThinkSchedule(int year, int month, int day, int hour, int minute, int second) {
        if(year < 2000){
            throw new RuntimeException("不被接受的指定年份【允许<0 或者 至少 "+DateUtil.year()+"】");
        }
        if(month > 12){
            throw new RuntimeException("非法月份数" + month);
        }
        if(day> 31){
            throw new RuntimeException("非法日期数" + month);
        }
        if(hour >23){
            throw new RuntimeException("小时取值范围为0-23，或者小于0，表示每小时");
        }
        if(minute > 59 || second > 59){
            throw new RuntimeException("分秒取值范围为0-59,分允许小于0，表示每分钟");
        }
        if(second < 0){
            throw new RuntimeException("不允许构建每秒执行的Schedule");
        }

        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public ThinkSchedule() {}

    private boolean hitYear(){
        if(year <=0 ){
            return true;
        }
        return DateUtil.year() == this.year;
    }

    private boolean hitMonth(){
        if(hitYear()){
            if(month <=0){
                return true;
            }
            return DateUtil.month() == this.month;
        }
        return false;
    }


    private boolean hideDay(){
        int maxDay = 31;
        if(hitMonth()){
            if(day<=0){
                return true;
            }
            if(this.month % 2 == 0){
                maxDay --;
            }
            if(this.month == 2){
                maxDay = 28;
            }
            int useDay = maxDay>this.day?this.day:maxDay;
            if (log.isWarnEnabled()) {
                if(useDay != this.day ){
                    log.warn("由于本月（{}月）不包含{}日，故符合日期校验被调整到{}日，下月自动恢复" , this.month,this.day,useDay);

                }
            }

            return DateUtil.day() == useDay;
        }
        return false;
    }

    private boolean hitHour(){
        if(hideDay()){
            if(this.hour <= 0 ){
                return true;
            }

            return DateUtil.currentHourOfTime() == this.hour;
        }
        return false;
    }


    private boolean hitMinute(){
        if(hitHour()){
            if(minute <=0){
                return true;
            }
            return DateUtil.currentMinuteOfTime() == this.minute;

        }
        return false;
    }






    public boolean tryHit(){
       if(hitMinute()){
           if (DateUtil.currentSecondOfTime() == this.second) {
               long now =  ThinkMilliSecond.currentTimeMillis();
               long minInterval = 1200;
               if(now - this.lastHitTime > minInterval){
                   this.lastHitTime = now;
                   return true;
               }else{

               }
           }
       }
       return false;
    }


//    public int getYear() {
//        return year;
//    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getYear() {
        return year;
    }


    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void setLastHitTime(long lastHitTime) {
        this.lastHitTime = lastHitTime;
    }

    public long getLastHitTime() {
        return lastHitTime;
    }
}
