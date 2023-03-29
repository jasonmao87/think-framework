package com.think.core.bean.schedules;

import com.think.common.util.DateUtil;
import com.think.common.util.FastJsonUtil;
import com.think.common.util.RandomUtil;
import com.think.common.util.TVerification;
import com.think.core.annotations.Remark;
import com.think.core.executor.ThinkThreadExecutor;
import com.think.exception.ThinkNotSupportException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author JasonMao
 */
@Slf4j
public class ThinkScheduleBuilder {

    private ThinkScheduleCronConfig config ;
    private int maxTrigger =0;


    private ThinkScheduleBuilder(int maxTrigger){
        config =new ThinkScheduleCronConfig();
        /*config.monthCron = "*";
        config.dateCron = "*";
        config.hourCron ="*";
        config.minuteCron ="*";
        config.second = RandomUtil.nextInt()%60;*/
        config.setMonthCron("*");
        config.setDateCron("*");
        config.setHourCron("*");
        config.setMinuteCron("*");
        try{
            config.setSecond(RandomUtil.nextInt()%60);
        }catch (Exception e){


        }
        this.maxTrigger = maxTrigger;
    }
    public static final ThinkScheduleBuilder builderWithMaxTrigger(int maxTrigger){
        TVerification.valueOf(maxTrigger > 0).throwIfFalse("最大触发次数必须大于0");
        return new ThinkScheduleBuilder(maxTrigger);
    }

    public static final ThinkScheduleBuilder builderWithRunForever(){
        return new ThinkScheduleBuilder(-1);
    }

    /**
     * 快速构建一个 延迟执行的任务
     * @param delayTime  延时时常
     * @param timeUnit   单位
     * @return
     */
    public static ThinkScheduleCronConfig buildDelayConfig(int delayTime , TimeUnit timeUnit) throws ThinkNotSupportException {
        final long maxSecond = 72L * 60L * 60L;
        long delaySecond = timeUnit.toSeconds(delayTime);
        if(delaySecond > maxSecond){
            throw new ThinkNotSupportException("最大延迟不能超过23小时");
        }
        if(delaySecond < 1){
            throw new ThinkNotSupportException("最小延迟必须大于1秒");
        }
        final ThinkScheduleBuilder builder = builderWithMaxTrigger(1);
        delaySecond += 1L ;
        Date now = DateUtil.now();
        Date runDate = new Date(now.getTime() + delayTime * 1000);
                //DateUtil.computeAddSeconds(now, Long.valueOf(delaySecond).intValue());

        builder.configMonth( String.valueOf(DateUtil.month(runDate)))
                .configDate( String.valueOf(DateUtil.date(runDate)))
                .configHour(String.valueOf(DateUtil.hourOfTime(runDate)))
                .configMinute(String.valueOf(DateUtil.minuteOfTime(runDate)));
        builder.config.setSecond(DateUtil.secondOfTime(runDate));

        return builder.getConfig();
    }


    public ThinkScheduleBuilder configMonth(String monthCornString){
        config.setMonthCron(monthCornString);
        return this;
    }
    public ThinkScheduleBuilder configDate(String dateCornString){
        config.setDateCron(dateCornString);
        return this;
    }
    public ThinkScheduleBuilder configHour(String hourCornString){
        config.setHourCron(hourCornString);
        return this;
    }

    public ThinkScheduleBuilder configMinute(String minuteConString){
        config.setMinuteCron(minuteConString);
        return this;
    }




    @Remark("构建每月执行1次的 配置 ，指定日，时，分，秒数随机")
    public ThinkScheduleCronConfig buildEveryMonthConfig(int date,int hour, int minute ){
        this.config.setHourCron( String.valueOf(hour));
        this.config.setMinuteCron( String.valueOf(minute));
        return this.getConfig();
    }


    @Remark("构建每天执行1次的 配置 ，指定时，分，秒数随机")
    public ThinkScheduleCronConfig buildEveryDayConfig(int hour, int minute ){
        this.config.setHourCron( String.valueOf(hour));
        this.config.setMinuteCron( String.valueOf(minute));
        return this.getConfig();
    }

    @Remark("构建每小时执行1次的 配置 ，指定分，秒数随机")
    public ThinkScheduleCronConfig buildEveryHourConfig(int minute){
        this.config.setMinuteCron(String.valueOf(minute));
        return this.getConfig();
    }

    @Remark(value = "构建在指定多个分钟匹配时候的配置",description = "1,10,15 表示每小时的 1，10，15分钟执行，每小时执行三次")
    public ThinkScheduleCronConfig buildWhileMinuteIsIn(int... minuteArray){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < minuteArray.length; i++) {
            if(i > 0){
                sb.append(",");
            }
            sb.append(minuteArray[i]);
        }
        this.config.setMinuteCron(sb.toString());
        return this.getConfig();
    }



    public ThinkScheduleCronConfig getConfig() throws RuntimeException{
        try{
            boolean b = this.config.enable(maxTrigger);
            if(!b) {

                log.error("无法获取到合法的配置");
                throw new RuntimeException("无法获取到合法的配置");
            }
            log.error("{}", config.toSerializedString());
        }catch (Exception e){
            log.error("无法获取到合法的配置",e);
            throw new RuntimeException("无法获取到合法的配置",e);
        }
        return config;
    }


    public static void main(String[] args) {
        try {
            while (true) {
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);
                ThinkThreadExecutor.runDelay(() -> {
                }, 1);



                Thread.sleep(1000);
            }
        }catch (Exception e){}
    }

}
