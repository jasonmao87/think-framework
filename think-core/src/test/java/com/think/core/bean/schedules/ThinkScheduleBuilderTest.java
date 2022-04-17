package com.think.core.bean.schedules;

import com.think.common.util.DateUtil;
import com.think.common.util.StringUtil;
import com.think.common.util.TimeUtil;
import com.think.core.executor.ThinkThreadExecutor;
import com.think.exception.ThinkNotSupportException;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ThinkScheduleBuilderTest {

    public static final void printDate(){
        System.out.printf( StringUtil.fmtAsDatetime(new Date()));
    }

//    @Test
//    public void buildDelayConfig() throws ThinkNotSupportException {
//        final ThinkScheduleCronConfig thinkScheduleCronConfig = ThinkScheduleBuilder.buildDelayConfig(1, TimeUnit.DAYS);
//        System.out.println(thinkScheduleCronConfig.displayString());
//
//    }


    public static final   ThinkScheduleCronConfig config (){
      return  ThinkScheduleBuilder.builderWithMaxTrigger(10).configMinute("*").getConfig();
    }

    public static void main(String[] args) {


//        printDate();
        try {
//            ThinkThreadExecutor.runDelay(() -> {
//
//                System.out.println();
//                System.out.println("我运行啦");
//                printDate();
//            }, 3);




            ThinkThreadExecutor.startScheduledTask(()->{
                printDate();
                System.out.println(" A 运行了 ");
            },config());

            TimeUtil.sleep(1,TimeUnit.SECONDS);

            ThinkThreadExecutor.startScheduledTask(()->{
                printDate();
                System.out.println(" B 运行了 ");
            },config());
            TimeUtil.sleep(1,TimeUnit.SECONDS);

            ThinkThreadExecutor.startScheduledTask(()->{
                printDate();
                System.out.println(" C 运行了 ");
            },config());



        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testRunDely(){



    }
}