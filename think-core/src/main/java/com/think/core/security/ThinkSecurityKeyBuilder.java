package com.think.core.security;

import com.think.common.util.DateUtil;
import com.think.common.util.RandomUtil;
import com.think.common.util.ThinkMilliSecond;

public class ThinkSecurityKeyBuilder {

    private static final char[] dic = {'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v' };


    public ThinkKey buildKey(int durationHour){
        long expireTime = DateUtil.computeAddHours(DateUtil.now(),durationHour).getTime();
        StringBuilder sb = new StringBuilder();
        String timeString = Long.toHexString(expireTime);

        StringBuilder keyString = new StringBuilder(timeString)
                .append("X");
        int i = RandomUtil.nextInt();
        int len = dic.length;
        int begin = i % len;
        byte[] initArray = new byte[16];
        for(int start =0; start<16; start ++){

        }


        return null;
    }


    public static void main(String[] args) {
        String x ="";
        System.out.println(DateUtil.toFmtString(DateUtil.ofMilliseconds(Long.MAX_VALUE),"yyyy-MM-dd"));
        System.out.println(Long.MAX_VALUE);
        System.out.println(Long.toHexString(Long.MAX_VALUE));
        System.out.println(Long.toHexString(Long.MAX_VALUE).length());
        Long now = ThinkMilliSecond.currentTimeMillis();
    }
}
