package com.think.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class RandomUtil {
    private static final int cacheSize = 512 ;
    private static int int_index = -1 ;
    private static int[] int_array = new int[cacheSize];

    private static void init(){
        Random random = new Random();
        for(int i= 0 ; i < cacheSize ; i ++){
            int_array[i] = random.nextInt(512);
        }
        int_index = 0 ;
    }
    public final synchronized static int nextInt(){
        if(int_index == -1 || int_index >= cacheSize ){
            init();
        }
        int_index ++ ;
        return int_array[int_index -1 ];
    }

}
