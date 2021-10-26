package com.think.common.data.mysql;

/**
 * @Date :2021/10/18
 * @Name :ThinkSqlCheckResult
 * @Description : 请输入
 */
public class ThinkSqlCheckResult {

    boolean result ;

    boolean containsFastMatch ;

    boolean containsFlowState;

    public ThinkSqlCheckResult(boolean result, boolean containsFastMatch, boolean containsFlowState) {
        this.result = result;
        this.containsFastMatch = containsFastMatch;
        this.containsFlowState = containsFlowState;
    }

    public boolean isResult() {
        return result;
    }

    public boolean isContainsFastMatch() {
        return containsFastMatch;
    }

    public boolean isContainsFlowState() {
        return containsFlowState;
    }
}
