package com.think.core.bean;

import com.think.common.util.BinaryUtil;
import com.think.common.util.TAssert;
import com.think.core.annotations.Remark;
import org.apache.poi.xwpf.usermodel.BreakType;

import java.util.*;

import static com.think.core.annotations.bean.ThinkStateColumn.*;

/**
 * @Date :2021/10/9
 * @Name :TFlowBuilder
 * @Description : 请输入
 */
public class TFlowBuilder {



    private static final Set<String> startAllowValues = new HashSet<>();
    private static final Set<String>  cancelAllowValues = new HashSet<>();
    private static final Set<String> completeAllowValues = new HashSet<>();
    private static final Set<String> clearAllowValues = new HashSet<>();
    static {
        /**    完成   通过  开始  取消  参考值   意义
         *      1     1     1    0     14    完成并通过
         *      1     0     1    0     10    完成并未通过
         *      0     0     1    0     2     开始，进行中
         *      0     0     0    1     1     取消
         */
        int[] possible = {0,1,2,10,14};
        for (int v : possible){
            AbleChecker checker = new AbleChecker(v);
            if(checker.isStartAble()){
                startAllowValues.add( String.valueOf(v));
            }
            if(checker.isCompleteAble()){
                completeAllowValues.add(String.valueOf(v));
            }
            if(checker.isCancelAble()){
                cancelAllowValues.add(String.valueOf(v));
            }
            if(checker.isClearAble()){
                clearAllowValues.add(String.valueOf(v));
            }
        }
        // 0  1 2 10 14
    }


    public static final boolean isStartAble(int stateValue){
        return startAllowValues.contains(String.valueOf(stateValue));
    }
    public static final boolean isCancelAble(int stateValue){
        return cancelAllowValues.contains(String.valueOf(stateValue));
    }
    public static final boolean isCompleteAble(int stateValue){
        return completeAllowValues.contains(String.valueOf(stateValue));
    }
    public static final boolean isClearAble(int stateValue){
        return clearAllowValues.contains(String.valueOf(stateValue));
    }


    private static final int[] toIntArray(Set<String> set){
        TAssert.notNull(set,"转换SET不能为NULL");
        TAssert.notEmpty(set,"转换SET不能为空");
        int size = set.size();
        int[] values = new int[size];
        Iterator<String> iterator = set.iterator();
        int index = 0 ;
        while (iterator.hasNext()) {
            String v = iterator.next();
            values[index] = Integer.valueOf(v).intValue();
            index ++ ;
        }
        return values;
    }

    public static final int[] startAllowStateValues(){
        return toIntArray(startAllowValues);
    }
    public static final int[] cancelAllowStateValues(){
        return toIntArray(cancelAllowValues);
    }

    public static final int[] completeAllowStateValues(){
        return toIntArray(completeAllowValues);
    }
    public static final int[] clearAllowStateValues(){
        return toIntArray(clearAllowValues);
    }





    public static boolean safeKeySuffix(String suffix){
        if(!suffix.startsWith(splitFlag)){
            suffix = splitFlag + suffix;
        }
        switch (suffix){
            case flowStateSuffix_CompleteTime : return true;
            case flowStateSuffix_CancelTime : return true;
            case flowStateSuffix_ResultMessage : return true;
            case flowStateSuffix_StartTime : return true;
            case flowStateSuffix_StateValue : return  true;
            case flowStateSuffix_TryCount : return true;
            default: return false;
        }


    }
    public static TFlowState build(String key , String comment , int value , Date startTime , Date completeTime , Date cancelTime ,int tryCount ,String resultMessage) {
        boolean cancelFlag = BinaryUtil.checkPositionIsTrue(value, 0);
        boolean startState = BinaryUtil.checkPositionIsTrue(value, 1);
        boolean resultState = BinaryUtil.checkPositionIsTrue(value, 2);
        boolean completeState = BinaryUtil.checkPositionIsTrue(value, 3);
        TFlowState flowState = new TFlowState(key, comment, startState, completeState, cancelFlag, resultState, startTime, completeTime, cancelTime, tryCount, resultMessage);
       return flowState;
    }



    static class AbleChecker{
        boolean cancelFlag ;
        boolean startState ;
        boolean resultState ;
        boolean completeState;

        public AbleChecker(int value) {
            cancelFlag = BinaryUtil.checkPositionIsTrue(value, 0);
            startState = BinaryUtil.checkPositionIsTrue(value, 1);
            resultState = BinaryUtil.checkPositionIsTrue(value, 2);
            completeState = BinaryUtil.checkPositionIsTrue(value, 3);
        }


        /**
         * 是否允许start
         * @return
         */
        public boolean isStartAble(){
            return (completeState==false )&&( startState == false);
        }

        /**
         * 是否允许cancel
         * @return
         */
        public boolean isCancelAble(){
            return (startState == true) && (completeState == false);
        }

        /**
         * 是否允许清除状态 ，clear
         * @return
         */
        public boolean isClearAble(){
            return ((startState==false) && (completeState ==false) )
                    || ((startState == true ) && completeState == true && resultState == false)
                    || ( (cancelFlag == true )&& (startState ==false));
        }

        /**
         * 是否允许complete
         * @return
         */
        public boolean isCompleteAble(){
            return startState && (completeState ==false);
        }
    }



}
