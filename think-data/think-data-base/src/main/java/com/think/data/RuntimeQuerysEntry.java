package com.think.data;


import java.io.Serializable;

public class RuntimeQuerysEntry implements Serializable {

    private String sql ;

    private long duration ;

    int affectedCount ;

    private boolean success ;

    private Throwable throwable;

    private Serializable[] paramsValues;

    protected RuntimeQuerysEntry(String sql , boolean success ,int affectedCount, long duration, Serializable... paramsValues ) {
        this.sql = sql;
        this.duration = duration;
        this.success = success;
        this.affectedCount =affectedCount;
        this.paramsValues = paramsValues;
    }

    public long getDuration() {
        return duration;
    }

    public String getSql() {
        return sql;
    }

    protected void setSuccess(boolean success){
        this.success = success;

    }
    protected void setSql(String sql) {
        this.sql = sql;
    }

    protected void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isSuccess() {
        return success;
    }

    public void throwInfo(Throwable throwable){
        if(throwable!=null) {
            this.throwable = throwable;
        }
    }

}
