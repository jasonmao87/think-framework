package com.think.core.executor;

import com.think.common.util.StringUtil;
import com.think.common.util.ThinkMilliSecond;
import com.think.core.annotations.Remark;
import com.think.core.security.ThinkToken;
import com.think.exception.ThinkRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date :2021/3/24
 * @Name :ThinkExecutor
 * @Description : 请输入
 */
@Slf4j
public class ThinkThreadExecutor {

    private static int queueCapacity = 128;

    /**
     * 后台任务 队列，最大容量为128
     */
    private static BlockingQueue<BackTaskHolder> taskHolderArrayBlockingQueue = new ArrayBlockingQueue<BackTaskHolder>(128);
    /**
     * 核心线程数量，不会被回收
     */
    private static final int corePoolSize = 6;
    /**
     * 最大线程数量，超过核心数量当空闲时候会被回收
     */
    private static final int maximumPoolSize = 12 ;
    /**
     * 拒绝策略采用 thinkRunsPolicy ，尝试 加queue 三次，如果 失败，让生产线程 自己跑去 ！
     */
    private static final Executor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(queueCapacity), new DefaultThinkThreadFactory(),new ThinkRunsPolicy());


    /**
     * 运行指令控制标识
     */
    private static boolean startControlState = false ;

    /**
     * 运行状态
     */
    private static boolean runState = false;

    /**
     * （阻塞的）安全的停止后台任务。我们调用此方法后  后台任务不会马上停止，会执行完正在执行的任务后，静默的停止。
     */
    public static synchronized final boolean shutDownBackgroundTasks(){
        if(startControlState){
            startControlState = false;
        }else{
            return false;
        }
        while (runState){
            //正在执行，售后在检查。
            try{
                Thread.sleep(100);
            }catch (Exception e){}
        }
        return true;
    }


    private synchronized static boolean setStartState(){
        if(startControlState){
            return false;
        }
        startControlState =true;
        return true;
    }

    /**
     * 启动后任务
     */
    private static final void start(){
        if(!setStartState()){
            return;
        }
        ThinkAsyncExecutor.execute(()->{
            runState = true;
            if (log.isInfoEnabled()) {
                log.info("____________________________________________________________________________________________________________________________");
                log.info("think 后台任务线程启动，开始执行后台任务....");
                log.info("____________________________________________________________________________________________________________________________");
            }
            long start = ThinkMilliSecond.currentTimeMillis();
            long executeCount= 0;
            long executeErrorCount = 0;
            Set<String> removeSet = new HashSet<>();
            while (startControlState){
                for (BackTaskHolder holder : taskHolderArrayBlockingQueue) {
                    if(holder.canRun()){
                        long now = ThinkMilliSecond.currentTimeMillis();
                        if (holder.getMaxLoop() == 0) {
                            //说明执行次数用尽，不在继续执行.这边为了注释，让jvm 自己优化代码
                            removeSet.add(holder.getId());
                            //添加到 移除set
                        } else {
                            if (now - (holder.getInterval() * 1000 * 60) > holder.getLastExecuteTime()) {

                                try {
                                    if (log.isTraceEnabled()) {
                                        log.trace("未来需删除调试期日志：-------------------------------------START-----------------------------------------------");
                                        log.trace("本次执行携带得TOKEN信息：{}" , ThinkExecuteThreadSharedTokenManager.get());
                                        log.trace("未来需删除调试期日志：-------------------------------------FINISH----------------------------------------------");
                                    }
                                    holder.setLastExecuteTime(now);
                                    holder.getTask().execute();
                                } catch (Exception e) {
                                    executeErrorCount ++ ;

                                }finally {
                                    executeCount ++;
                                }
                            }
                        }
                        try {
                            /**休眠 1秒钟  */
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }
                    }

                }
            }

            long end = ThinkMilliSecond.currentTimeMillis();
            runState = false;
            if (log.isInfoEnabled()) {
                log.info("____________________________________________________________________________________________________________________________");
                log.info("think 后台任务线程安全结束，开始停止执行后台任务....");
                log.info("后台任务存活时长 = {} ，共执行{}次后台任务吗，其中捕获异常{}次" , (end -start) , executeCount,executeErrorCount);
                log.info("____________________________________________________________________________________________________________________________");
            }
            removeSet.forEach(k->{
                if (log.isInfoEnabled()) {
                    log.info("任务执行次数用尽，移除后台任务[{}]",k);
                }
                taskHolderArrayBlockingQueue.removeIf(t->t.getId().equals(k));
            });
            removeSet.clear();

        });
//        CompletableFuture.runAsync(()->{
//
//        },getExecutor());
    }


    /**
     * 增加一个 任务到后台 轮询执行的任务。我们无需关心它合适启动
     * @param backgroundTask
     * @param intervalMinutes
     * @param  maxLoop          轮询次数限制，如果小于0，则表示 永久执行下午。如果大于0，会在执行N次后停止执行
     */
    @Remark("启动永远不会退出得后台轮询任务！< 第一个参数为后台执行得任务，第二个参数为 执行间隔（分钟）,如果 intervalMinutes <=0 ,表示只执行1次  ,第三个参数表示轮询次数，如果小于0，表示永远不会停止>")
    public final static void addBackgroundTask(ThinkBackgroundTask backgroundTask,int intervalMinutes ,int maxLoop){
        addBackgroundTask(backgroundTask,intervalMinutes,maxLoop,0,TimeUnit.MILLISECONDS);
    }

    @Deprecated
    @Remark("启动永远不会退出得后台轮询任务！< 第一个参数为后台执行得任务，第二个参数为 执行间隔（分钟）, 第三个参数表示轮询次数，如果小于0，表示永远不会停止,delayMinutes = 延迟delayMinutes分钟后执行")
    public final static void addBackgroundTask(ThinkBackgroundTask backgroundTask,int intervalMinutes ,int maxLoop,int delayMinutes){
        addBackgroundTask(backgroundTask,intervalMinutes,maxLoop,delayMinutes,TimeUnit.MINUTES);
    }

    @Remark("启动永远不会退出得后台轮询任务！< 第一个参数为后台执行得任务，第二个参数为 执行间隔（分钟）, 第三个参数表示轮询次数，如果小于0，表示永远不会停止,delay  = 时间数量，unit 时间单位")
    public final static void addBackgroundTask(ThinkBackgroundTask backgroundTask,int intervalMinutes ,int maxLoop,int delay ,TimeUnit unit){

        if(intervalMinutes<1){
            throw new ThinkRuntimeException("intervalMinutes表示执行间隔分钟数，必须设置为>0");
        }
        if(maxLoop == 0){
            throw new ThinkRuntimeException("maxLoop不能设置为0，maxLoop>0表示执行次数，maxLoop<0表示永远会轮询执行下去");
        }
        BackTaskHolder holder =null;
        if(ThinkExecuteThreadSharedTokenManager.get()!=null){
            ThinkToken token = ThinkExecuteThreadSharedTokenManager.get();
            holder=new BackTaskHolder(backgroundTask, intervalMinutes, maxLoop ,token);
        }else{
            holder=new BackTaskHolder(backgroundTask, intervalMinutes, maxLoop ,null);
        }

        if(delay >0){
            if (log.isDebugEnabled()) {
                log.debug("添加后台执行任务,将在{} {} 后执行{}次....",delay,unit.toString(),maxLoop);
                log.debug("携带得tokenInfo：{}", ThinkExecuteThreadSharedTokenManager.get());
            }
            long delayMillis ;
            switch (unit){
                case SECONDS: delayMillis = delay * 1000; break;
                case MINUTES: delayMillis = delay * 60L * 1000 ; break;
                case HOURS  : delayMillis = delay * 60L * 1000 * 60 ; break;
                case DAYS   : delayMillis = delay * 60L * 1000 * 60 * 24 ; break;
                default: {
                    delayMillis = delay; break;
                }
            }
            holder.setDelayMillis( delayMillis);
        }
        taskHolderArrayBlockingQueue.add(holder);
        if(startControlState == false){
            /**
             * 默认会调用开始执行方法
             */
            start();
        }

    }




    protected static Executor getExecutor() {
        return executor;
    }

    /**
     * 自定义得 ThreadFactory ，用于创建线程，给线程标记可识别得名称
     */
    static class DefaultThinkThreadFactory implements ThreadFactory {
//        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThinkThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "think-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    /**
     * think framework 拒绝策略
     */
    static class ThinkRunsPolicy implements RejectedExecutionHandler{
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            boolean offerResult = false;
            int tryCount = 3 ;

            while (!offerResult && tryCount >0 ){
                tryCount -- ;

//
//                if(tryCount ==0){
//                    //log.warn("异步线程繁忙才可能出发得异常，需要执行得异步任务超过了{}项",queueCapacity);
//                }
                offerResult = tryOffer(r,executor.getQueue());

            }

            if(!offerResult ) {
                try{
                    // 阻塞pu
                    if (log.isWarnEnabled()) {
                        log.warn("【2】异步线程过于繁忙才提示得警告,多次异步排队未成功，尝试塞生产线程排队，强制等待任务进入队列。----当前异步线程缓冲队列容量[{}]",queueCapacity);
                    }
                    //阻塞生产线程 put 进去
                    executor.getQueue().put(r);
                }catch (Exception ex){
                    //实在没办法了，让生产线程自己玩吧。这边一般不可能进入
                    if (!executor.isShutdown()) {
                        if (log.isWarnEnabled()) {
                            log.warn("【3】异步线程远远超过了系统预期，出发最严格得容错机制，将直接使用生产线程执行该任务！" ,queueCapacity);
                        }
                        r.run();
                    }
                }
            }
        }
        private boolean tryOffer(Runnable r , Queue<Runnable> queue){
            try {
                Thread.sleep(1);
            }catch (Exception e){}
            return queue.offer(r);
        }
    }





}


class BackTaskHolder{
    private String id ;
    private ThinkBackgroundTask task;
    private long lastExecuteTime ;
    private int interval;
    private int maxLoop;

    private ThinkToken token;

    // @Remark("延迟时间")
    private long delayMillis =0;
    private long initTime ;

    public BackTaskHolder(ThinkBackgroundTask task,int interval,int maxLoop ,ThinkToken token) {
        this.id = StringUtil.uuid();
        this.task = task;
        this.lastExecuteTime = 0L;
        this.interval = interval;
        this.maxLoop =maxLoop;
        this.initTime = ThinkMilliSecond.currentTimeMillis();
        this.token= token;
    }

    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public String getId() {
        return id;
    }

    public int getInterval() {
        return interval;
    }

    public long getLastExecuteTime() {
        return lastExecuteTime;
    }

    public ThinkBackgroundTask getTask() {
        return task;
    }

    public ThinkToken getToken() {
        return token;
    }

    public int getMaxLoop() {
        return maxLoop;
    }

    /**
     * 是否允许执行？
     * @return
     */
    public boolean canRun(){
        long time =ThinkMilliSecond.currentTimeMillis();
        return time-initTime > delayMillis;
    }


    public void setLastExecuteTime(long lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
        if(maxLoop > 0) {
            this.maxLoop--;
        }
    }


}