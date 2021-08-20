package com.think.common.util.rt.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.lang.management.MemoryUsage;

@ApiModel("系统运行内存模型")
public class MemeryInfoModel implements Serializable {
    @ApiModelProperty("初始化字节数")
    private  long init;
    @ApiModelProperty("当前使用字节数")
    private  long used;
    @ApiModelProperty("当前提交字节数")
    private  long committed;
    @ApiModelProperty("申请的最大值，默认-1 未设置")
    private  long max;


    public static MemeryInfoModel ofUsage(MemoryUsage usage){
        return new MemeryInfoModel(usage.getInit(),usage.getUsed(),usage.getCommitted(),usage.getMax());
    }

    public MemeryInfoModel(long init,
                           long used,
                           long committed,
                           long max) {
        if (init < -1) {
            throw new IllegalArgumentException( "init parameter = " +
                    init + " is negative but not -1.");
        }
        if (max < -1) {
            throw new IllegalArgumentException( "max parameter = " +
                    max + " is negative but not -1.");
        }
        if (used < 0) {
            throw new IllegalArgumentException( "used parameter = " +
                    used + " is negative.");
        }
        if (committed < 0) {
            throw new IllegalArgumentException( "committed parameter = " +
                    committed + " is negative.");
        }
        if (used > committed) {
            throw new IllegalArgumentException( "used = " + used +
                    " should be <= committed = " + committed);
        }
        if (max >= 0 && committed > max) {
            throw new IllegalArgumentException( "committed = " + committed +
                    " should be < max = " + max);
        }

        this.init = init;
        this.used = used;
        this.committed = committed;
        this.max = max;
    }

    public long getInit() {
        return init;
    }

    public long getUsed() {
        return used;
    };


    public long getCommitted() {
        return committed;
    };


    public long getMax() {
        return max;
    };

    /**
     * Returns a descriptive representation of this memory usage.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("init = " + init + "(" + (init >> 10) + "K) ");
        builder.append("used = " + used + "(" + (used >> 10) + "K) ");
        builder.append("committed = " + committed + "(" +
                (committed >> 10) + "K) " );
        builder.append("max = " + max + "(" + (max >> 10) + "K)");
        return builder.toString();
    }


}