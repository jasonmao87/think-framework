package com.think.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * id 生成工具类
 */
@Slf4j
public class ThinkIdGenerator {
    private static ThinkIdGenerator instance = null;

//
//    public static void main(String[] args) {
//        try {
//            IdUtil.instance(1);
//
//        }catch (Exception e){}
//
//        long x  =1610446346435L ;
//        long id =696034626303426561L;
//        //946721446828
//        System.out.println(946721446828L);
//        System.out.println(timeMillis20000101);
//
//
//        System.out.println("A"+Long.toBinaryString(id) + "  " +Long.toBinaryString(id).length() + " " + id);
//        System.out.println("B"+Long.toBinaryString(IdUtil.idToMillis(id)) + " " + IdUtil.idToMillis(id) + " " + x);
////        System.out.println(Long.toBinaryString( id>>20 ) + " " +  (id >> 20)+ " " + (id>>20));
//        System.out.println("C"+Long.toBinaryString(x) +  " " + x );
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
//
//
//
//
//        long y = 1610439615211L ;
//        long y20 = y<<20 | 1<<10 | 1;
//        long z = y20>>20;
//        System.out.println( y == z );
//
//
//        long _id = IdUtil.nextId();
//        long t = IdUtil.idToMillis(_id);
//        System.out.println(_id);
//        System.out.println(IdUtil.idToDate(_id));
//        System.out.println(t);
//
//
//    }

    protected final static synchronized ThinkIdGenerator getInstance(int machineBitLength , int machineId){
        if(null == instance){
            try {
                instance = new ThinkIdGenerator(machineBitLength, machineId);
            }catch (Exception e){
                if(log.isErrorEnabled()){
                    log.error("初始化ThinkIdGenerator异常:",e);
                }
            }
        }
        return instance;
    }

    /**
     * 2000年1月1日 的时间毫秒数
     */
    final private static long timeMillis20000101 =  DateUtil.beginOfDate(DateUtil.from(2000,1,1)).getTime()  ;
    /**
     * 时间秒差所占位数
     */
    private final static int LEN_TIME_MILLIS_BIT = 43;
    /**
     * 索引所占位数
     */
    private final int LEN_INDEX_BIT ;
    /**
     * 机器所占位数
     */
    private final int LEN_MACHINE_BIT ;

    /**
     * 当前实例的machineId
     */
    private final int machineId  ;
    /**
     * 最大支持的machineid
     */
    private final int maxMachineId  ;

    /**
     * 当前索引值
     */
    private long currentIndex = 0L ;

    /**
     * 当前实例最大索引值
     */
    private final long maxIndex;

    /**
     * 索引中位值
     */
    private final long midIndex ;

    /**
     * 最后申请ID的时间
     */
    private volatile long lastIdTime = 0L ;

    private ThinkIdGenerator(int machineBitLength , int machineId) throws Exception{
        if(machineBitLength>10 || machineBitLength< 1){
            throw new Exception("machineBitLength占位不可超过10,且必须大于0。" );
        }
        this.LEN_MACHINE_BIT = machineBitLength;
        this.maxMachineId = ( 1<<this.LEN_MACHINE_BIT  ) -1  ;
        if(machineId>this.maxMachineId){
            throw new Exception("machineBitLength占位"+ machineBitLength +",MachineId最大值不可超过" +  maxMachineId +",最小为0。");
        }
        this.machineId = machineId;

        this.LEN_INDEX_BIT = 64 - 1 - LEN_MACHINE_BIT - LEN_TIME_MILLIS_BIT;
        maxIndex = 2L<<LEN_INDEX_BIT -1 ;
        midIndex = maxIndex / 2  +1 ;
        if(log.isDebugEnabled()) {
            log.debug("ThinkIdGenerator 实例化完成：" +
                    "\n\t当前机器编号\t\t\t: " + machineId +
                    "\n\t支持机器范围\t\t\t: 0 - " + maxMachineId + "" +
                    "\n\t最多支持机器\t\t\t: " + (maxMachineId + 1) + "" +
                    "\n\t机器所占位数\t\t\t: " + LEN_MACHINE_BIT + "" +
                    "\n\t索引占的位数\t\t\t: " + LEN_INDEX_BIT +
                    "\n\t秒差占的位数\t\t\t: " + LEN_TIME_MILLIS_BIT +
                    "\n\t每秒索引容量\t\t\t: " + maxIndex + "" +
                    "\n\t索引的中位值\t\t\t: " + midIndex + "" +
                    "\n\t-------------------------------------------");
        }
    }

    /**
     * 当前时间 秒
     * @return
     */
    private long currentMillis(){
        //return System.currentTimeMillis();
        return ThinkMilliSecond.currentTimeMillis();
    }

    /**
     * 不安全的方法
     * 获取到一个新的id 通过自定义时间赋值的
     * @return
     */
    @Deprecated
    protected synchronized long nextIdByTime(long millis){

        long generatorId = (millis - timeMillis20000101)<<( LEN_INDEX_BIT + LEN_MACHINE_BIT )  |   (currentIndex<< LEN_MACHINE_BIT) | machineId ;
        currentIndex ++ ;
//        log.info("通过 millis {} 生成id {}" ,millis , generatorId);
        return generatorId ;

    }




    /**
     * 获取到一个新的id
     * @return
     */
    protected synchronized long nextId(){
        long millis = currentMillis();
        if(millis > lastIdTime ){
            currentIndex = 0L;
            lastIdTime = millis;
        }else if(millis < lastIdTime){
            millis = lastIdTime;
        }
        if(currentIndex >= maxIndex){
            millis ++ ;
            currentIndex =0L;
            lastIdTime = millis;
            return nextId();
        }
        long generatorId = (millis - timeMillis20000101)<<( LEN_INDEX_BIT + LEN_MACHINE_BIT )  |   (currentIndex<< LEN_MACHINE_BIT) | machineId ;
        currentIndex ++ ;
        return generatorId;
//
//
//
//
//
////        log.info("{}",millis);
//        return nextIdByTime(millis,true);
    }



    /**
     * 通过id获取到 id生成时候的毫秒数
     * @param id
     * @return
     */
    protected long getTimeMillisById(long id){
//        log.info("通过id {} 获取时间戳 {} ",id , (LEN_INDEX_BIT+LEN_MACHINE_BIT) );
        return (id>>(LEN_INDEX_BIT+LEN_MACHINE_BIT))+ timeMillis20000101;
    }

    /**
     * 通过id获取到 id生成时候的 Date
     * @param id
     * @return
     */
    protected Date getDateById(long id){
        long millis = getTimeMillisById(id) ;
        return new Date( millis);
    }

    /**
     * 通过id 获取到 id的 machineId
     * @param id
     * @return
     */
    protected int getMachineIdById(long id){
        String btStr = Long.toBinaryString(id);
        String machineBt = btStr.substring(btStr.length() - LEN_MACHINE_BIT);
        int machindId = Integer.parseInt(machineBt,2);
        return machindId;
    }


    /**
     * 是否是本类生成的 Id 。通过的标准为，相同配置，不同机器产生的id 都能通过配置，如果是不同配置的实例产生的id，则无法通过
     *   1.通过位运算左移【索引占位 +  machineId占位】 得到时间差A           这个是直接算法反推得到正确的时间差。
     *   2.通过直接截取从左边开始36位，得到时间差B【第一位为符号位，时间差占位35位，所以要加一位截取36位】 如果不是标准id，这个值和 B肯定不一致 。
     *          因为 A是 从右边往左边取，取得长度不一定，B是从左到右取固定长度。
     *   3.若时间差校验一致，则通过第一部分。开始获取本配置实例下的机器占位，截取算出机器mid 。
     *   4.mid跟本配置下实例的最大id比较，如果不大于最大id，并且不为负数，则校验通过。
     *
     * @param id
     * @return
     */
    protected boolean isMyId(long id){
        String btStr = Long.toBinaryString(id);
        long idTime  =  id>>(LEN_INDEX_BIT+LEN_MACHINE_BIT) ;
        StringBuilder builder =new StringBuilder("");
        for(int i= 0 ; i <64- btStr.length();i++){
            builder.append("0");
        }
        builder.append(btStr);
        btStr = builder.toString();
        long idTimeCheck =Long.parseLong(btStr.substring(0,36),2);
        if(idTime == idTimeCheck && idTime > 0){

            String machineBt = btStr.substring(btStr.length() - LEN_MACHINE_BIT);

            int machindId = Integer.parseInt(machineBt,2);

            if(machindId<0){
                return false;
            }
            return !( machindId>maxMachineId);
        }
        return false;
    }



}
