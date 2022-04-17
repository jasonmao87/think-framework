package com.think.core.bean.schedules;

import com.think.common.util.RandomUtil;
import com.think.common.util.TimeUtil;
import com.think.core.annotations.Remark;
import com.think.core.bean.ThinkSchedule;
import com.think.exception.ThinkNotSupportException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

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
        config.setDateCron("*");
        config.setMonthCron("*");
        config.setHourCron("*");
        config.setMinuteCron("*");
        try{
            config.setSecond(RandomUtil.nextInt()%60);
        }catch (Exception e){

        }
        this.maxTrigger = maxTrigger;
    }
    public static ThinkScheduleBuilder builderWithMaxTrigger(int maxTrigger){
        return new ThinkScheduleBuilder(maxTrigger);
    }

    /**
     * 快速构建一个 延迟执行的任务
     * @param delayTime  延时时常
     * @param timeUnit   单位
     * @return
     */
    public static ThinkScheduleCronConfig buildDelayConfig(int delayTime , TimeUnit timeUnit) throws ThinkNotSupportException {

        if(timeUnit == TimeUnit.MICROSECONDS){
            throw new ThinkNotSupportException("最小时间单位必须为秒");
        }
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



    public ThinkScheduleCronConfig getConfig() throws RuntimeException{
        try{
            boolean b = this.config.enable(maxTrigger);
            if(!b) {
                log.error("无法获取到合法的配置");
                throw new RuntimeException("无法获取到合法的配置");
            }
        }catch (Exception e){
            log.error("无法获取到合法的配置");
            throw new RuntimeException("无法获取到合法的配置",e);
        }
        return config;
    }

}
