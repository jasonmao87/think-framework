package com.think.common.result;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/2 9:29
 * @description : TODO
 */
public class ThinkMiddleState {
    private boolean enable = false;
    private boolean success = false;
    private String message ;

    public boolean isEnable() {
        return enable;
    }


    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }


    public ThinkMiddleState() {}

    public ThinkMiddleState success(){
        return this.success(null);
    }

    public synchronized ThinkMiddleState success(String message){
        if (this.enable) {
            return this;
        }
        this.enable  =true;
        this.success =true;
        this.message = message;
        return this;
    }

    public ThinkMiddleState fail(){
        return this.fail(null);
    }

    public synchronized ThinkMiddleState fail(String message){
        if (this.enable) {
            return this;
        }
        this.enable  =true;
        this.success =false;
        this.message = message;
        return this;
    }



}
