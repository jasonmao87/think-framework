package com.think.core.bean;

import com.think.common.util.DateUtil;
import com.think.common.util.TVerification;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkStateColumn;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date :2021/10/9
 * @Name :StateEntity
 * @Description : 流程状态控制类，必须配合 @ThinkStateColumn使用！！
 */
public class TFlowState implements Serializable {
    private static final long serialVersionUID = -6512682111390000009L;
//
//    @ApiModelProperty(value = "业务对象id",hidden = true)
//    private long id;

    @ApiModelProperty(value = "关联的key",hidden = true)
    String mainKey ;

    @ApiModelProperty(value = "意义",hidden = true)
    String comment;

    @ApiModelProperty(value = "当前状态是否有意义",hidden = true)
    private boolean ableState;

    @ApiModelProperty(value = "状态开始（即进行中，这个时候结果无意义）",hidden = true)
    private boolean startState ;

    @ApiModelProperty(value = "是否完结",hidden = true)
    private boolean completeState;

    @ApiModelProperty(value = "取消标志" ,hidden = true)
    private boolean cancelFlag;

    @ApiModelProperty(value = "结果",hidden = true)
    private boolean resultState ;

    @ApiModelProperty(value = "流程开始时间",hidden = true)
    private Date startTime = DateUtil.zeroDate();

    @ApiModelProperty(value = "流程完成时间",hidden = true)
    private Date completeTime = DateUtil.zeroDate();

    @ApiModelProperty(value = "流程状态取消时间",hidden = true)
    private Date cancelTime  = DateUtil.zeroDate();

    @ApiModelProperty(value = "流程执行次数（反复次数）",hidden = true)
    private int tryCount  =0;

    @ApiModelProperty(value = "完结备注消息",hidden = true)
    private String resultMessage  ="";

    public static final TFlowState newInstance(){
        return new TFlowState();
    }

    public TFlowState() {
    }

    protected TFlowState(String mainKey, String comment, boolean startState, boolean completeState, boolean cancelFlag, boolean resultState, Date startTime, Date completeTime, Date cancelTime, int tryCount, String resultMessage) {
        this.mainKey = mainKey;
        this.comment = comment;
        this.startState = startState;
        this.completeState = completeState;
        this.cancelFlag = cancelFlag;
        this.resultState = resultState;
        this.startTime = startTime;
        this.completeTime = completeTime;
        this.cancelTime = cancelTime;
        this.tryCount = tryCount;
        this.resultMessage = resultMessage;
        this.isAbleState();
    }





    @Remark(value = "重置状态(清除状态)",description = "仅当流程状态完成且未通过时候调用")
    public TFlowState clearState(){
        TVerification.valueOf(TFlowBuilder.isClearAble(this.getStateValue())).throwIfFalse("当前状态不允许开始");
//        if(this.isCancelFlag() ==false) {
//            TVerification.valueOf(startState && completeState && resultState ).throwIfTrue("状态已经完结，无法重置");
//            TVerification.valueOf(completeState).throwIfFalse("状态正在进行中，无法重置");
//        }else{
//            //TVerification.valueOf(cancelFlag).throwIfFalse("状态无需重置");
//        }
        this.completeState = false;
        this.startState =false;
        this.cancelFlag = false;
        this.ableState =false;
        return this;
    }

    @Remark(value = "重新开始流程状态",description = "仅当流程状态完成且未通过时候调用,重新初始化状态")
    public TFlowState reStart(){
        return this.clearState()
                .start();
    }


    @Remark(value = "流程状态开始",description = "初始状态事件，状态流程执行的开始")
    public TFlowState start(){

        TVerification.valueOf(TFlowBuilder.isStartAble(this.getStateValue())).throwIfFalse("当前状态不允许开始");

        this.startTime = DateUtil.now();
        this.startState = true;
        this.cancelFlag = false;
        this.tryCount ++ ;
        return this;
    }

    @Remark(value = "流程状态完结" ,description = "状态完结事件，通过或拒绝")
    public TFlowState complete(boolean result ){
        return this.complete(result,"");
    }
    @Remark(value = "流程状态完结",description = "状态完结事件，通过或拒绝")
    public TFlowState complete(boolean result ,String resultMessage){
        TVerification.valueOf(TFlowBuilder.isCompleteAble(this.getStateValue())).throwIfFalse("当前状态不允许开始");
        this.completeTime = DateUtil.now();
        this.completeState = true;
        this.resultState =result;
        this.resultMessage = resultMessage;
        return this;
    }

    @Remark(value = "流程状态取消",description = "进行中的状态，被通知取消时候调用")
    public TFlowState cancel(){
        TVerification.valueOf(TFlowBuilder.isCancelAble(this.getStateValue())).throwIfFalse("当前状态不允许开始");
        this.cancelTime = DateUtil.now();
        this.resultState = false;
        this.startState = false;
        this.completeState = false;
        this.cancelFlag =true;
        return this;
    }


    public boolean isAbleState(){
        ableState = (startState || completeState);
        return ableState;
    }

    public boolean isStartState() {
        return startState;
    }

    @ApiModelProperty(hidden = true)
    public boolean getResult(){
        return this.isResultState();
    }
    public boolean isResultState(){
        return startState;
    }

    public boolean isCancelFlag() {
        return cancelFlag;
    }

    public boolean isCompleteState() {
        return completeState;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public int getTryCount() {
        return tryCount;
    }


    @ApiModelProperty(value = "存储的状态值，二进制解析",name = "stateValue",hidden = true)
    public int getStateValue(){
        /**    完成   通过  开始  取消  参考值   意义
         *      1     1     1    0     14    完成并通过
         *      1     0     1    0     10    完成并未通过
         *      0     0     1    0     2     开始，进行中
         *      0     0     0    1     1     取消
         */

        StringBuilder sb = new StringBuilder();
        sb.append(completeState?"1":"0")
                .append(resultState?"1":"0")
                .append(startState?"1":"0")
                .append(cancelFlag?"1":"0");
        return Integer.valueOf(sb.toString(),2).intValue();
    }

    public String getMainKey() {
        return mainKey;
    }

    public String getComment() {
        return comment;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }


    @ApiModelProperty(hidden = true)
    private final String keySplitFlag = ThinkStateColumn.splitFlag;

    public String getKeySplitFlag() {
        return keySplitFlag;
    }

    @ApiModelProperty(hidden = true)
    public String[] getAllowedDbKeys(){
        return new String[]{
                this.getMainKey()  + ThinkStateColumn.flowStateSuffix_ResultMessage,
                this.getMainKey()  + ThinkStateColumn.flowStateSuffix_StartTime,
                this.getMainKey()  + ThinkStateColumn.flowStateSuffix_CompleteTime,
                this.getMainKey()  + ThinkStateColumn.flowStateSuffix_StateValue,
                this.getMainKey()  + ThinkStateColumn.flowStateSuffix_TryCount,
                this.getMainKey()  + ThinkStateColumn.flowStateSuffix_CancelTime
        };


    }
}

