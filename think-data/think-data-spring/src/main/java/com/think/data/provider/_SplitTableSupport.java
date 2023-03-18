package com.think.data.provider;

import com.think.common.data.ThinkFilterOp;
import com.think.common.data.mysql.ThinkFilterBean;
import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.util.DateUtil;
import com.think.common.util.IdUtil;
import com.think.common.util.StringUtil;
import com.think.common.util.ThinkCollectionUtil;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/3/8 17:02
 * @description :
 */
@Slf4j
public class _SplitTableSupport {
    private static final int minYearLimit = 2020;

    protected static final int computeSpiltYearById(long id){
        int year =  DateUtil.year(IdUtil.idToDate(id));
        if(year < minYearLimit){
            year = minYearLimit;
        }
        return year;
    }


    protected static final  <T extends SimplePrimaryEntity> int[] possibleSplitYears(ThinkSqlFilter<T> sqlFilter, List<String> showSplitTables){
        boolean locationAble = false;
        long maxId =  Long.MAX_VALUE ;
        long minId = 0L ;         // max min 应该 取最小并集
        for(ThinkFilterBean fb : sqlFilter.getBeans()){
            if(fb.getKey().equalsIgnoreCase("id")){
                ThinkFilterOp op = fb.getOp();
                if(op == ThinkFilterOp.EQ){
                    long id = getIdValueForPossibleInteger(fb.getValues()[0]);
                    return new int[]{computeSpiltYearById(id)} ;
                }
                if(op == ThinkFilterOp.LE || op ==ThinkFilterOp.LEE ){
                    long id  =  getIdValueForPossibleInteger(fb.getValues()[0]);
                    if(id < maxId){
                        maxId = id ;
                    }
                    locationAble =true;

                }
                if(op == ThinkFilterOp.LG || op ==ThinkFilterOp.LGE){
                    long id =  getIdValueForPossibleInteger(fb.getValues()[0]);
                    if(id > minId){
                        minId = id ;
                    }
                    locationAble =true;
                }
                if(op == ThinkFilterOp.BETWEEN_AND){
                    long small = getIdValueForPossibleInteger(fb.getValues()[0]);
                    long big = getIdValueForPossibleInteger(fb.getValues()[0]);
                    if(log.isTraceEnabled()){
                        log.trace("analysis between {} and {} " , small,big);
                    }
                    if(small> minId){
                        minId = small;
                    }
                    if(big < maxId){
                        maxId = big;
                    }
                    locationAble =true;
                    if(log.isTraceEnabled()){
                        log.trace("current  {} -  {} " , minId,maxId);
                    }
                }
            }
        }//end of for
        int userLimitYearFrom = sqlFilter.getFilterSplitYearFrom();
        int userLimitYearEnd = sqlFilter.getFilterSplitYearEnd();

        int maxY = computeSpiltYearById(maxId);
        int minY = computeSpiltYearById(minId);
        if(userLimitYearEnd > 0 && userLimitYearEnd < maxY){
            if (log.isDebugEnabled()) {
                log.debug("用户限制了数据查询结束年份并且范围小于智能推算的年份，尊重用户选择，数据查询结束范围年费设置为 {}年" , userLimitYearEnd);
            }
            maxY = userLimitYearEnd;
        }
        if(userLimitYearFrom > 0 && userLimitYearFrom > minY){
            if (log.isDebugEnabled()) {
                log.debug("用户限制了数据查询开始年份并且范围小于智能推算的年份，尊重用户选择，数据查询开始范围年费设置为 {}年" , userLimitYearFrom);
            }
            minY = userLimitYearFrom;
        }
        // 计算  合并 并 返回
        return doResult(sqlFilter,showSplitTables,locationAble,maxY,minY);

    }


    private static  <T extends SimplePrimaryEntity> int[] doResult(ThinkSqlFilter<T> filter , List<String> splitTables , boolean locationAble , int maxYearSet ,int minYearSet ){
        int from = maxYearSet>minYearSet?minYearSet:maxYearSet;
        int end = maxYearSet>minYearSet?maxYearSet:minYearSet;
        if(end > DateUtil.year()+1){
            end = DateUtil.year() +1 ;
        }
        if(from < minYearLimit){
            from = minYearLimit;
        }
        if(end<minYearLimit){
            end = minYearLimit;
        }
        if(from == end){
            return new int[]{from};
        }
        int[] array = new int[end - from  + 1 ];
        for (int i = 0; i < array.length; i++) {
            array[i] = from + i ;
        }
        if(filter.isDesc()){
            for (int i = 0; i < array.length/2; i++) {
                int t = array[i];
                array[i] = array[array.length-1 - i];
                array[array.length-1-i] = t;
            }
        }
        if(true) {
            return array;
        }
        // 后面不跑了 ！！！
        //其实不去判断那些表存不存在 似乎也不重要 ？？？

        List<Integer> list = getAllSplitYearSuffix(splitTables,filter.isDesc());
        int initedMax = -1 ;
        int initedMin =  -1 ;
        if(list.size() > 0){
            if(locationAble) {
                if (filter.isDesc()) {
                    initedMax = list.get(0);
                    initedMin = list.get(list.size() - 1);
                } else {
                    initedMin = list.get(0);
                    initedMax = list.get(list.size() - 1);
                }
                if (maxYearSet > initedMax) {
                    maxYearSet = initedMax;
                }
                if (minYearSet < initedMin) {
                    minYearSet = initedMin;
                }
                if (minYearSet > maxYearSet) {
                    if (log.isTraceEnabled()) {
                        log.trace(" compute result is min large than max {} {} ");
                    }
                    return new int[]{};
                } else {
                    int[] arr = new int[maxYearSet - minYearSet + 1];
                    for (int i = 0; i < arr.length; i++) {
                        if (filter.isDesc()) {
                            arr[i] = maxYearSet - i;
                        } else {
                            arr[i] = i + minYearSet;
                        }
                    }
                    if (log.isTraceEnabled()) {
                        log.trace("{}", Arrays.toString(arr));
                    }

                    return arr;
                }
            }else{
                int[] arr = new int[list.size()];
                for(int i = 0 ; i < list.size();i++){
                    arr[i] = list.get(i);
                }
                return arr;
            }
        }else{
            // TODO !!!!!!!!!!!!!!!!!!!!!!!!!!

            int minYear =2022 ;
            int year = DateUtil.year();
            List<Integer> listAll = new ArrayList<>();
            for (int i = year; i >=minYear ; i--) {
                listAll.add(i);
            }
            int[] arr = new int[listAll.size()];
            for(int i = 0 ; i < listAll.size();i++){
                arr[i] = listAll.get(i);
                // ---
            }

            return arr;

        }

    }





    /**
     * 所有 按年切割表的年份 后缀
     * @param showSplitTables
     * @param desc
     * @param <T>
     * @return
     */
    public static final <T extends _Entity> List<Integer> getAllSplitYearSuffix(List<String> showSplitTables , boolean desc) {
        List<String> stringList = showSplitTables;
        List<Integer> list = new ArrayList<>();
        for(String t : stringList){
            if (StringUtil.isNotEmpty(t)) {
                String lowCaseTableName = t.toLowerCase();
                if (lowCaseTableName.contains("_split_")) {
                    String[] tempArray = lowCaseTableName.split("_split_");
                    if (tempArray.length>1 && StringUtil.isAllNumber(tempArray[1])) {
                        int tempYear = Integer.valueOf(tempArray[1]);
                        list.add(tempYear);
                    }
                }
            }
        }
        Collections.sort(list);
        if(desc){
            Collections.reverse(list);
        }
        return list;
    }



    /**
     * 解决 id 值 可能被解析成 integer的问题
     * @param x
     * @return
     */
    private static long getIdValueForPossibleInteger(Serializable x){
        if(x instanceof Integer){
            return Long.valueOf(x.toString());
        }else  if( x instanceof  Long){
            return (Long)x ;
        }else{
            return Long.valueOf(x.toString());
        }
    }


}
