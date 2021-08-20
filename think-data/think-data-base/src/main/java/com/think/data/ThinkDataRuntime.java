package com.think.data;

import com.think.common.util.ThinkMilliSecond;
import com.think.structure.ThinkFastList;

import java.io.Serializable;
import java.util.List;

public class ThinkDataRuntime implements Serializable {

    /**
     * 数据分区region ，如果是 分表 对象，那么需要引用这个参数作为 分表 后缀
     */
    private String partitionRegion = null;

    /**
     * 线程id
     */
    private long threadId ;

    /**
     * 初始化创建时间
     */
    private long initTime ;

    /**
     * 执行的 select 数量
     */
    private int selectNum = 0;

    /**
     * 执行的  update数量
     */
    private int updateNum = 0 ;

    /**
     * 执行DDL语句的数量
     */
    private int ddlNum = 0 ;

//    /**
//     * 是否启用使用逻辑删除，默认都是逻辑删除
//     */
//    private boolean logicDelete =true;

    /**
     * 执行的 del数量
     */
    private int delNum = 0 ;

    /**
     * 执行的insert 数量
     */
    private int insertNum  = 0;

    /**
     * sql 执行总耗时
     */
    private long totalDuration = 0;

    /**
     * 执行的sql 清单
     */
    private List<RuntimeQuerysEntry> executedQueryList;



    protected ThinkDataRuntime(String partitionRegion) {
        this.threadId = Thread.currentThread().getId();
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.executedQueryList = new ThinkFastList<>(RuntimeQuerysEntry.class);
        this.partitionRegion = partitionRegion;
    }


    protected ThinkDataRuntime(  ) {
        this.threadId = Thread.currentThread().getId();
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.executedQueryList = new ThinkFastList<>(RuntimeQuerysEntry.class);
    }


    public long getInitTime() {
        return initTime;
    }


    public long getThreadId() {
        return threadId;
    }

    public String getPartitionRegion() {
        return partitionRegion;
    }

    public RuntimeQuerysEntry fireUpdate(String updateSql,boolean success ,int affectedCount ,long duration , Serializable[] paramsValues ){
        RuntimeQuerysEntry rt = new RuntimeQuerysEntry(updateSql,success,affectedCount,duration,paramsValues);
        this.updateNum ++ ;
        this.totalDuration += duration;
        this.executedQueryList.add(rt);
        return rt;
    }

    public RuntimeQuerysEntry fireInsert(String insertSql,boolean success,int affectedCount,long duration  , Serializable[] paramsValues ){
        RuntimeQuerysEntry rt = new RuntimeQuerysEntry(insertSql,success,affectedCount,duration,paramsValues) ;
        this.executedQueryList.add( rt );
        this.insertNum ++ ;
        this.totalDuration += duration;
        return rt;
    }

    public RuntimeQuerysEntry fireDelete(String  delSql,boolean success,int affectedCount,long duration  , Serializable[] paramsValues ){
        RuntimeQuerysEntry rt = new RuntimeQuerysEntry(delSql,success,affectedCount,duration,paramsValues) ;
        this.delNum ++ ;
        this.totalDuration += duration;
        this.executedQueryList.add(rt);
        return rt;
    }

    public RuntimeQuerysEntry fireSelect(String  selectSql,boolean success,int affectedCount,long duration , Serializable[] paramsValues  ){
        RuntimeQuerysEntry rt = new RuntimeQuerysEntry(selectSql,success, affectedCount,duration,paramsValues) ;
        this.selectNum ++ ;
        this.totalDuration += duration;
        this.executedQueryList.add(rt);
        return rt;
    }

    public RuntimeQuerysEntry fireDDL(String ddl ,long duration ){
        RuntimeQuerysEntry rt = new RuntimeQuerysEntry(ddl,true,0,duration) ;
        this.ddlNum ++ ;
        this.totalDuration += duration;
        this.executedQueryList.add(rt);
        return rt;
    }

    public int getSelectNum() {
        return selectNum;
    }

    public int getUpdateNum() {
        return updateNum;
    }

    public int getDelNum() {
        return delNum;
    }

    public int getInsertNum() {
        return insertNum;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public List<RuntimeQuerysEntry> getExecutedQueryList() {
        return executedQueryList;
    }

}
