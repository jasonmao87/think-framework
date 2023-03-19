package com.think.common.util;

import com.think.common.util.rt.ThinkMachineUtil;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * ID工具类
 */
@Slf4j
public class IdUtil {
    private static ThinkIdGenerator idGenerator = null;
    private final static int machineBitLen = 10 ;

    static {
        /*
        不在自动初始化 id生成器
        int machineId =defaultServerId();

        try {
            idGenerator = new ThinkIdGenerator(machineBitLen, machineId);
        }catch (Exception e){
            if(log.isErrorEnabled()) {
                log.error("默认初始化IdGenerator失败", e);
            }
        }finally {
        }

         */
    }


    public static final boolean isInstance(){
        return idGenerator !=null;
    }


    public static final synchronized void instance(int machineId) throws Exception{
        if(isInstance()==false) {
             try{
                idGenerator =   ThinkIdGenerator.getInstance(machineBitLen,machineId);
            }catch (Exception e){
                idGenerator = null;
                if(log.isErrorEnabled()){
                    log.error("构建IdGenerator失败！",e);
                }
             }
        }else {
            //2020-12-9 允许重新构建id服务!
//            if(log.isWarnEnabled()){
//                log.info("已经构建了Id生成器，无法再次构建");
//            }
            if (log.isDebugEnabled()) {
                log.debug("ID生成器即将被重构，当前机器索引为 {} ,新构建的ID生成器将调整机器索引为 {}", idGenerator.getMachineId(),machineId);
            }
            try{
                idGenerator =   ThinkIdGenerator.getInstance(machineBitLen,machineId);
            }catch (Exception e){
                idGenerator = null;
                if(log.isErrorEnabled()){
                    log.error("重新构建IdGenerator失败！",e);
                }
            }

        }

    }


    /**
     * 申请一个新的id
     * @return
     */
    public static long nextId(){
        if( isInstance() ==false){
            throw new RuntimeException("IdGenerator尚未初始化，请调用 IdUtil.instance 初始化");
        }else {
            long id = idGenerator.nextId();
//            if(log.isDebugEnabled()){
//                log.debug("开始生成id ： 通过newDate {} | 通过time {} ,生成的id是 -> {} ,反推出时间是 ：{}-> 时间戳 =={}" ,
//                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.now()) ,
//                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ThinkMilliSecond.currentTimeMillis())) ,
//                        id ,
//                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( IdUtil.idToDate(id)),
//                        IdUtil.idToDate(id).getTime()
//                        );
//            }

            return id;
        }
    }

    /**
     * 根据时间参数 生成跟当时时间相似的ID
     * @param datetime 的毫秒数
     * @return
     */
    public static long idByDate(long datetime){
        if(isInstance() ==false){
            throw new RuntimeException("IdGenerator尚未初始化，请调用 IdUtil.instance 初始化");
        }else {
            return idGenerator.nextIdByTime(datetime);
        }
    }

    /**
     * 根据时间参数 生成跟当时时间相似的ID
     * @param date
     * @return
     */
    public static long idByDate(Date date){
        return idByDate(date.getTime());
    }


    /**
     * 根究id 反推出 id生成的时间
     * @param id
     * @return
     */
    public static Date idToDate(long id){
        if(isInstance() == false){
            throw new RuntimeException("IdGenerator尚未初始化，请调用 IdUtil.instance 初始化");
        }else {
            return idGenerator.getDateById(id);
        }
    }

    public static long idToMillis(long id){
        if(isInstance() == false){
            throw new RuntimeException("IdGenerator尚未初始化，请调用 IdUtil.instance 初始化");
        }else {
            return idGenerator.getTimeMillisById(id);
        }
    }


    private static int defaultServerId() {
        int serverId = defaultServerName().hashCode();
        if(serverId < 0){
            serverId = -serverId;
        }
        return serverId%99;
    }


    /**
     * 获得一个 36进制得 较短 id
     * @return
     */
    public static final String nextShortId(){
        if(isInstance()) {
            long id = IdUtil.nextId();
            return Long.toString(id, 36);
        }else{
            try{
                IdUtil.instance(0);

            }catch (Exception e){
            }
            if(isInstance() == false) {
                throw new ThinkRuntimeException("IdUtil 尚未初始化，且尝试临时初始化未成功！");
            }
        }
        long id = IdUtil.nextId();
        return Long.toString(id, 36);
    }

    private static String defaultServerName() {
        return ThinkMachineUtil.hostName()+"["+ThinkMachineUtil.macAddr()+"]";
    }

}
