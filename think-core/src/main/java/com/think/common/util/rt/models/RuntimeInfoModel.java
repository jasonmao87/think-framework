package com.think.common.util.rt.models;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("运行的内存和线程信息状态对象")
@Accessors(chain = true)
public class RuntimeInfoModel implements Serializable {


    @ApiModelProperty("最近一分钟平均load值")
    private double systemLoadAverage;

    @ApiModelProperty("当前虚拟机中的类的数量")
    private long classLoadedCountCurrent ;


    @ApiModelProperty("虚拟机开始执行到目前已经加载的类的总数")
    private long classLoadedCountTotal ;

    @ApiModelProperty("虚拟机开始执行到目前已经卸载的类的总数")
    private long classUnloadCountTotal;



    @ApiModelProperty("堆内存")
    private MemeryInfoModel memoryHeapMemoryUsage;
    @ApiModelProperty("非堆内存")
    private MemeryInfoModel memoryNoHeapMemoryUsage;

    @ApiModelProperty("活动线程的当前数目，包括守护线程和非守护线程")
    private int threadTotalCount;

    @ApiModelProperty("活动守护线程的当前数目")
    private int threadDaemonCount;

//    @ApiModelProperty(" 找到处于死锁状态（等待获取对象监视器）的线程的周期,id数组")
//    private long[] threadDeadLockedThreads;

//    @ApiModelProperty("找到处于死锁的线程 数目")
//    private int threadDeadLockedCount ;

    @ApiModelProperty("峰值线程数")
    private int threadPeakCount;

    @ApiModelProperty("记录时间")
    private Date recordTime;


    /**
     * 堆内存使用率
     * @return
     */
    public double heapUsagePercent(){
        return this.getMemoryHeapMemoryUsage().getUsed()*1.0/this.getMemoryHeapMemoryUsage().getCommitted();

    }

     /**
     * 非堆内存使用率
     * @return
     */
    public double noHeapUsaePercent(){
        return this.getMemoryNoHeapMemoryUsage().getUsed()*1.0/this.getMemoryNoHeapMemoryUsage().getCommitted();
    }




}