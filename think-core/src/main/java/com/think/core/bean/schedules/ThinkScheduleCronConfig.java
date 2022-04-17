package com.think.core.bean.schedules;

import com.think.common.util.BinaryTool;
import com.think.common.util.DateUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.TScheduleCron;
import com.think.exception.ThinkNotSupportException;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 *  long 8字节 ， 64位
 *  月12 日 31 时 24 分 60
 *
 */
@Slf4j
public class ThinkScheduleCronConfig implements Serializable {
    private static final long serialVersionUID = 7994897748140413147L;


//    @Remark(value = "年份配置",description = "* 代表任意年份，可以使用 A-B ，也可以使用逗号分割 ")
//    private String yearCron;
    @Remark("生效状态")
    private boolean enable  =false;

    @Remark(value = "月份配置", description = "* 代表任意月份=【1-12】，也等于 1,2,3,4,5,6,7,8,9,10,11,12")
    private String monthCron;

    @Remark(value = "日配置", description = "* 代表任意日，参考月，当日大于当月最大日时会，被调整至当月最后一天")
    private String dateCron;

    @Remark(value = "小时配置", description = "* 代表任意，参考日，月配置")
    private String hourCron;

    @Remark(value = "分钟配置", description = "* 代表任意，参考日，月配置")
    private String minuteCron;

    @Remark(value = "秒配置", description = "必须指定精确的秒")
    private int second = -1;

    protected void setMonthCron(String monthCron) {
        this.monthCron = monthCron;
    }

    protected void setDateCron(String dateCron) {
        this.dateCron = dateCron;
    }

    protected void setHourCron(String hourCron) {
        this.hourCron = hourCron;
    }

    protected void setMinuteCron(String minuteCron) {
        this.minuteCron = minuteCron;
    }


    //    private int beginYear;
//
//    private long yearConfig;

    private short monthConfig = 0;  // max 12 - int

    private int dateConfig = 0; //  max 31 - int

    private int hourConfig = 0; // max 24 - int

    private long minuteConfig = 0L; // max 60 - long


//    private long expireTime = 0 ;

    private int maxTriggerCount = 1;

    private int triggerCount =0;

    private long lastTrigger =0L ;

    private void setTriggerCount(int triggerCount) {
        this.triggerCount = triggerCount;
    }

    public void setMaxTriggerCount(int maxTriggerCount) {
        this.maxTriggerCount = maxTriggerCount;
    }

    private void setMonthConfig(short monthConfig) {
        this.monthConfig = monthConfig;
    }

    private void setDateConfig(int dateConfig) {
        this.dateConfig = dateConfig;
    }

    private void setHourConfig(int hourConfig) {
        this.hourConfig = hourConfig;
    }

    private void setMinuteConfig(long minuteConfig) {
        this.minuteConfig = minuteConfig;
    }

    protected void setSecond(int second) throws ThinkNotSupportException {
        if (second > 59 || second < 0) {
            throw new ThinkNotSupportException("触发秒配置不允许" + second + "，允许区间[0-59]");
        }
        this.second = second;
    }

    /**
     * @param s
     * @param maxAllowValue
     * @return
     */
    private int[] fromCronString(String s, int maxAllowValue) throws ThinkNotSupportException{
        int[] v = null;
        String[] sArray = null;
        if (s.matches("^\\d{1,2}$")) {
            return new int[]{Integer.valueOf(s)};
        } else if (s.matches("^\\d{1,2}-\\d{1,2}$")) {
            sArray = s.split("-");
            if(sArray.length !=2){
                throw new ThinkNotSupportException("非法的配置表达式，准确的配置是\"N-M\",不允许使用" + s);
            }else{
                int start = Integer.valueOf(sArray[0]);
                int end = Integer.valueOf(sArray[1]);
                int len = end - start + 1;
                v = new int[len];
                int index = 0;
                for (int i =start; i <=end; i++) {
                    v[index] = i ;
                    if (i>maxAllowValue) {
                        throw new ThinkNotSupportException(
                                "非法的配置表达式，准确的配置是\"N-M\",规则匹配最大值不能超过" + maxAllowValue+ "但当前出现" + i);
                    }
                    index ++ ;
                }
                return v;
            }
        } else if (s.matches("^\\d{1,2}[,\\d{1,2}]+$")) {
            sArray = s.split(",");
        } else {
            throw new ThinkRuntimeException("不合理的配置 : " + s);
        }
        if (sArray != null) {
            v =  new int[sArray.length];
            for (int i = 0; i < sArray.length; i++) {
                int x = Integer.valueOf(sArray[i]);
                if(x>maxAllowValue){
                    throw new ThinkNotSupportException("非法的配置表达式:"+s+",规则匹配最大值不能超过\" + maxAllowValue+ \"但当前出现\" + i");
                }
                v[i] = Integer.valueOf(sArray[i]);
            }
            Arrays.sort(v);
        }

        return v;
    }

    public String toSerializedString() {
        StringBuilder serializedStringBuilder = new StringBuilder();
        serializedStringBuilder.append(this.monthConfig)
                .append(";").append(this.dateConfig)
                .append(";").append(this.hourConfig)
                .append(";").append(this.minuteConfig)
                .append(";").append(this.second)
//                .append(";").append(this.expireTime)
                .append(";").append(this.maxTriggerCount)
                .append(";").append(this.triggerCount);
        return serializedStringBuilder.toString();
    }

    public static final ThinkScheduleCronConfig ofSerializedString(String serializedString) throws ThinkNotSupportException {
        ThinkScheduleCronConfig config = new ThinkScheduleCronConfig();
        String[] array = serializedString.split(";");
        config.setMonthConfig(Short.valueOf(array[0]));
        config.setDateConfig(Integer.valueOf(array[1]));
        config.setHourConfig(Integer.valueOf(array[2]));
        config.setMinuteConfig(Long.valueOf(array[3]));
        config.setSecond(Integer.valueOf(array[4]));
//        config.setExpireTime(Long.valueOf(array[5]));
        config.enable(Integer.valueOf(array[5]));
//        config.setMaxTriggerCount(Integer.valueOf(array[5]));
        config.setTriggerCount(Integer.valueOf(array[6]));
       return config;
    }

    /**
     * 使生效,
     * @param maxTriggerCount
     * @return 返回生效状态
     */
    public boolean enable(int maxTriggerCount ) throws ThinkNotSupportException{
        if(isSafe() && this.enable == false) {
            this.init();
            this.setMaxTriggerCount(maxTriggerCount);
            this.enable = true;
            if (log.isDebugEnabled()) {
                log.debug("配置生效,将持续执行{}次",maxTriggerCount);
            }
        }

        return enable;
    }


    public boolean isEnable() {
        return enable;
    }

    private void init() throws ThinkNotSupportException{
        int[] monthArray = this.fromCronString(this.monthCron, 12);
        for (int m : monthArray) {
            this.monthConfig |= 1L << m;
        }
        int[] dayArray = this.fromCronString(this.dateCron, 31);
        for (int d : dayArray) {
            this.dateConfig |= 1L << d;
        }
        int[] hourArray = this.fromCronString(this.hourCron, 59);
        for (int h : hourArray) {
            this.hourConfig |= 1L << h;
        }
        int[] minuteArray = this.fromCronString(this.minuteCron, 59);
        for (int min : minuteArray) {
            this.minuteConfig |= 1L << min;
        }
    }


    public ThinkScheduleCronConfig() {
    }

    public ThinkScheduleCronConfig(
            //String yearCron, 可能我们并不需要年份的配置 ，每年执行又如何
            String monthCron, String dateCron,
            String hourCron, String minuteCron, int second) throws ThinkNotSupportException {
//        this.yearCron = yearCron.trim();
        this.monthCron = monthCron.trim();
        if(this.monthCron.equals("*")){
            this.monthCron = "1-12";
        }
        this.dateCron = dateCron.trim();
        if(this.dateCron.equals("*")){
            this.dateCron = "1-31";
        }
        this.hourCron = hourCron.trim();
        if(this.hourCron.equals("*")){
            this.hourCron = "0-23";
        }
        this.minuteCron = minuteCron.trim();
        if(this.minuteCron.equals("*")){
            this.minuteCron = "0-59";
        }
        this.second = second;
        this.init();

    }



    public static ThinkScheduleCronConfig ofAnnotation(TScheduleCron annotation) {
        try{
            ThinkScheduleCronConfig config = new ThinkScheduleCronConfig(annotation.month(),
                    annotation.date(),
                    annotation.hour(),
                    annotation.minute(),
                    annotation.second());
            config.enable(annotation.maxTriggerCount());
            return config;
        }catch (Exception e){

        }
        return null;

    }

    private boolean isSafe() {
        return second > 0;
    }

    /**
     * 尝试触发
     * @return
     */
    public synchronized boolean tryTrigger(){
        if(maxTriggerCount >0
                && triggerCount >= maxTriggerCount ){
            return false;
        }
        long now = ThinkMilliSecond.currentTimeMillis();
        if(now - lastTrigger < 59000) {
            return false;
        }
        int month =DateUtil.month();
        int date = DateUtil.date();
        int hour = DateUtil.currentHourOfTime();
        int minute = DateUtil.currentMinuteOfTime();
        int second =DateUtil.currentSecondOfTime();
        boolean result = this.checkTrigger(DateUtil.year(),month,date,hour,minute,second);
        if(result){
            this.triggerCount ++ ;
        }
        return result;
    }

    /**
     * 下次触发时间
     * @return
     */
    public Date nextTriggerTime(){
        int year =DateUtil.year();
        int month =DateUtil.month();
        int date = DateUtil.date();
        int hour = DateUtil.currentHourOfTime();
        int minute = DateUtil.currentMinuteOfTime();
        int second = this.second;
        boolean match = this.checkTrigger(year,month,date,hour,minute,second);
        while (!match){
            minute ++ ;
            if(minute == 60){
                minute = 0;
                hour++ ;
            }
            if(hour == 24){
                hour = 0;
                date ++ ;
            }
            Date newDate = DateUtil.from(year, month, date);
            year = DateUtil.year(newDate);
            month = DateUtil.month(newDate);
            date = DateUtil.date(newDate);
            match = this.checkTrigger(year,month,date,hour,minute,second);
//            log.debug("检查 {}-{}-{} {}:{}:{} --->>>> {}" , year,month,date,hour,minute,second,match);

        }
//        System.out.println( year+" " + month + " "+date);
        return DateUtil.buildNewDate(year,month,date,hour,minute,second);
    }



    private boolean checkTrigger(int year ,int month ,int date ,int hour,int minute,int second){
        if(!BinaryTool.checkPositionIsTrue(this.monthConfig,month)){
            return false;
        }
        if(date > 27) {
            Date newDate = DateUtil.buildNewDate(year, month, date, 1, 1, 1);
            Date endOfMonth = DateUtil.endOfMonth(newDate);
            int endOfMonthDate = DateUtil.date(endOfMonth);
            boolean dateMatch = false;

            if(endOfMonthDate < date){
                dateMatch = this.checkDateIsMath(endOfMonthDate);
            }else if (endOfMonthDate == date) {
                for (int i = date; i <= 31; i++) {
                    if (this.checkDateIsMath(i)) {
                        dateMatch = true;
                    }
                }
            }else{
                dateMatch = this.checkDateIsMath(date);
            }
            if(dateMatch == false){
                return false;
            }

        }else{
            if(!checkDateIsMath(date)){
                return false;
            }
        }

        if (!BinaryTool.checkPositionIsTrue(this.hourConfig,hour)){
            return false;
        }
        if(!BinaryTool.checkPositionIsTrue(this.minuteConfig,minute)){
            return false;
        }
        if(second >= this.second){
            this.triggerCount ++ ;
            return true;
        }

        return false;
    }





    private boolean checkDateIsMath(int date){
        return BinaryTool.checkPositionIsTrue(this.dateConfig,date);

    }

    /**
     * 是否可以 销毁
     * @return
     */
    @Remark(value = "是否可以销毁",description = "当触发次数达到maxTriggerCount时候返回true")
    public boolean canDestroy(){
        if(this.maxTriggerCount>0) {
            return (this.triggerCount >= this.maxTriggerCount);
        }else{
            return false;
        }
    }


    /**
     * 输出配置信息
     *
     * @return
     * @throws ThinkNotSupportException
     */
    public String displayString() throws ThinkNotSupportException {
        StringBuilder sb = new StringBuilder();
        sb.append("配置解析：本配置将允许触发").append(this.maxTriggerCount).append("次，已经触发").append(this.triggerCount).append("次");
        if(this.triggerCount>0){
            sb.append(".\n上次触发时间：")
                    .append( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(DateUtil.ofMilliseconds(this.lastTrigger)));
        }
        sb.append("\n触发月份配置:\n")
                .append(BinaryTool.toHorizontalString(this.monthConfig));
        sb.append("\n触发日配置：\n")
                .append(BinaryTool.toHorizontalString(this.dateConfig));

        sb.append("\n触发时配置：\n")
                .append(BinaryTool.toHorizontalString(this.hourConfig));
        sb.append("\n触发分配置：\n")
                .append(BinaryTool.toHorizontalString(this.minuteConfig));
        sb.append("\n触发秒： ").append(this.second);
        return sb.toString();
    }

    public static void main(String[] args) throws ThinkNotSupportException {
        ThinkScheduleCronConfig config =
                new ThinkScheduleCronConfig(
                        "*",
                        "30-31",
                        "3,5",
                        "14",
                        12);
        config.enable(Integer.MAX_VALUE);
        System.out.println(config.displayString());

        String ss = config.toSerializedString();

        System.out.println("序列化字符串： ");
        System.out.println(ss);
        ThinkScheduleCronConfig c = ThinkScheduleCronConfig.ofSerializedString(ss);
        System.out.println(c.displayString());
        System.out.println(DateUtil.toFmtString(c.nextTriggerTime(),"yyyy-MM-dd HH:mm:ss"));



        int year =DateUtil.year();
        int month =DateUtil.month();
        int date = DateUtil.date();
        int hour = DateUtil.currentHourOfTime();
        int minute = DateUtil.currentMinuteOfTime();
        int second = 12;
        boolean match = c.checkTrigger(year,month,date,hour,minute,second);
        int i = 0;
        while (true){
            minute ++ ;
            if(minute == 60){
                minute = 0;
                hour++ ;
            }
            if(hour == 24){
                hour = 0;
                date ++ ;
            }
            Date newDate = DateUtil.from(year, month, date);
            year = DateUtil.year(newDate);
            month = DateUtil.month(newDate);
            date = DateUtil.date(newDate);
            match = c.checkTrigger(year,month,date,hour,minute,second);
            if(match){
                i++;
                log.debug("{} >> 将是第{}次触发" ,DateUtil.toFmtString(DateUtil.buildNewDate(year,month,date,hour,minute,second),"yy-MM-dd HH:mm:ss"),i);
//                log.debug("触发时间：{}-{}-{} {}:{}:{} --->>>>第{}次触发" , year,month,date,hour,minute,second,i);
            }

            if(year > 2025){
                break;
            }
        }
//        System.out.println( year+" " + month + " "+date);

    }

}
