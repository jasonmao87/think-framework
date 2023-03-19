package com.think.common.data.mysql;

import com.think.common.util.DateUtil;
import com.think.common.util.TAssert;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkStateColumn;
import com.think.core.bean.TFlowBuilder;
import com.think.core.bean.TFlowState;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date :2021/10/13
 * @Name :TFlowStateUpdate
 * @Description : 请输入
 */
public class TFlowStateUpdate implements Serializable {
    private static final long serialVersionUID = -2620711111321111811L;

    private TFlowStateUpdate(String mainKey) {
        this.mainKey = mainKey;
//        time = DateUtil.now();
        this.stateModel = TFlowState.newInstance();
    }

    private TFlowState getStateModel() {
        return stateModel;
    }

    private TFlowState stateModel ;

    private String mainKey ;

    private boolean start ;

    private boolean cancel ;

    private boolean completeResultTrue ;

    private boolean completeResultFalse ;

    private boolean clear;

    private String resultMessage ="".intern() ;

//    private Date time ;

    public static final TFlowStateUpdate buildStart(String mainKey){
        TFlowStateUpdate update = new TFlowStateUpdate(mainKey);
        TFlowState state =update.getStateModel();
        state.start();
        update.start = true;
        return update;
    }

    public static final TFlowStateUpdate buildCancel(String mainKey){
        TFlowStateUpdate update = buildStart(mainKey);
        update.getStateModel().cancel();
        update.start =false;
        update.cancel =true;
        return update;
    }

    public static final TFlowStateUpdate buildComplete(String mainKey ,boolean result ,String message){
        TFlowStateUpdate update = buildStart(mainKey);
        update.getStateModel().complete(result,message);
        if(result){
            update.completeResultTrue =true;
        }else{
            update.completeResultFalse = true;
        }
        update.start = false;
        update.cancel =false;
        return update;
    }

    public static final TFlowStateUpdate buildClear(String mainKey){
        TFlowStateUpdate update = buildStart(mainKey);
        update.getStateModel().clearState();
        update.start = false;
        update.cancel = false;
        update.completeResultFalse = false;
        update.completeResultTrue = false;
        update.clear =true;
        return update;
    }




    @Remark("是否允许增加tryCount值")
    public boolean tryCountIncAble(){
        return start;
    }

    public String getTryCountKey(){
        return mainKey +ThinkStateColumn.flowStateSuffix_TryCount;
    }
    public String getMessageKey(){
        return mainKey + ThinkStateColumn.flowStateSuffix_ResultMessage;
    }

    public String getStateValueKey(){
        return mainKey + ThinkStateColumn.flowStateSuffix_StateValue;
    }

    public String getTimeKey(){
        if(start){
            return mainKey + ThinkStateColumn.flowStateSuffix_StartTime;
        } else if(cancel){
            return mainKey + ThinkStateColumn.flowStateSuffix_CancelTime;
        } else if(completeResultFalse || completeResultTrue){
            return mainKey + ThinkStateColumn.flowStateSuffix_CompleteTime;
        } else if(clear){
            return null;
        } else{
            TAssert.isTrue(false,"未标识有效状态");
        }
        return null;
    }

    public int getStateValue(){
        return stateModel.getStateValue();
    }

    public Date getTime(){
        return DateUtil.now();
    }

    public String getMessage(){
        return resultMessage;
    }


    @Remark("获取允许执行改状态的stateValue值")
    public int[] getRequiredStateValue(){
        if(start){
            return TFlowBuilder.startAllowStateValues();
        } else if(cancel){
            return TFlowBuilder.cancelAllowStateValues();
        } else if(completeResultFalse || completeResultTrue){
            return TFlowBuilder.completeAllowStateValues();
        } else if(clear){
            return TFlowBuilder.clearAllowStateValues();
        } else{
            TAssert.isTrue(false,"未标识有效状态");
        }
        return new int[0];
    }
    
   

}
