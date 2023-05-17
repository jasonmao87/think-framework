package com.think.data;

import com.think.FrameForceMatchFlag;
import com.think.common.data.IFilterChecker;
import com.think.common.data.IThinkQueryFilter;
import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.core.annotations.Remark;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.security.token.ThinkSecurityToken;
import com.think.core.security.token.ThinkSecurityTokenTransferManager;
import com.think.data.filter.DefaultThinkDataFilter;
import com.think.data.filter.ThinkDataFilter;
import com.think.data.model.DataModelBuilder;
import com.think.data.model.ThinkTableModel;
import com.think.data.verification.ThinkDataValidator;
import com.think.data.verification.ThinkKeyValidator;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Manager {

    private static final  ThreadLocal<ThinkDataRuntime> dataRuntimeThreadLocal= new ThreadLocal<>();

    private static final List<ThinkDataFilter> registedFilters = new ArrayList<>();

    private static DataModelBuilder modelBuilder = null;

    private static Set<String> initTables = new HashSet<String>();

    @Remark("启用数据校验")
    private static boolean verificationAble = true;

    @Remark("启用thinkLinkedId得非主键关联关系支持，会有一定得性能浪费")
    private static boolean thinkLinkedIdSupportAble= true;

    private static boolean enableSqlPrint =false;

    @Remark("输出可直接执行的SQL语句，不带TYPE等格式")
    private static boolean printExecutableSql = false;


    public static boolean sqlPrintAble(){
        return enableSqlPrint;
    }

    public static boolean isPrintExecutableSql(){
        return printExecutableSql;
    }



    public static final void setPrintExecutableSql(boolean b){
        printExecutableSql = b ;
    }


    public static final void enableSqlPrint(){
        enableSqlPrint =true;
    }
    public static final void disableSqlPrint(){
        enableSqlPrint = false;
    }

    public static final void disAbleThinkLinkedIdSupport(){
        thinkLinkedIdSupportAble =false;
    }

    public static boolean isThinkLinkedIdSupportAble() {
        return thinkLinkedIdSupportAble;
    }

    static {
        try{
            FrameForceMatchFlag.justMatch();
        }catch (Exception e){

        }
        /**
         * 注入默认的 filter ，避免dao层无法获得到filter,避免 NULL的错误
         */
        ThinkDataFilter defaultFilter = new DefaultThinkDataFilter();
        registedFilters.add(defaultFilter);

        ThinkSqlFilter.bindChecker(new IFilterChecker() {
            @Override
            public boolean checkKey(String key, Class targetClass) {
                ThinkTableModel model = getModelBuilder().get(targetClass);
                if(model!=null){
                    return model.containsKey(key);
                }
                return false;
            }


        });
        log.warn("ThinkData默认开启了thinkLinkedIdSupportAble得支持，进而会支持针对所有MYSQL表thinkLinkedId建立索引和默认赋值，如果不需要，请在配置文件中指定：think.data.thinkLinkedId.able =false,或手动调用Manage.disAbleThinkLinkedIdSupport() 禁用！");
    }

    private static IThinkQueryFilter iThinkQueryFilter;

    public static synchronized final void setThinkQueryFilter(IThinkQueryFilter queryFilter){
        if(iThinkQueryFilter == null){
            iThinkQueryFilter = queryFilter;
        }
    }

    public static IThinkQueryFilter getThinkQueryFilter() {
        return iThinkQueryFilter;
    }

    public static final void endDataSrv(){
        if(dataRuntimeThreadLocal.get() !=null){
            dataRuntimeThreadLocal.remove();
        }
    }
    public static final boolean beginDataSrv(){
        if(dataRuntimeThreadLocal.get() == null) {
            dataRuntimeThreadLocal.set(new ThinkDataRuntime());
            return true;
        }else{
            return false;
        }
    }
    public static final boolean beginDataSrv(String splitRegion){
        final ThinkDataRuntime localDataRuntime = dataRuntimeThreadLocal.get();
        if(  localDataRuntime == null ||
                ThinkDataRuntime.isNonePartitionRegion(localDataRuntime.getPartitionRegion())) {
            if (log.isTraceEnabled()) {
                log.trace("线程 指定数据分区 --- {}" ,splitRegion);
            }
            dataRuntimeThreadLocal.set(new ThinkDataRuntime(splitRegion));
            return true;
        }else{
            String currentP = null;
            ThinkDataRuntime runtime = getDataSrvRuntimeInfo() ;
            if(runtime !=null ){
                currentP = runtime.getPartitionRegion();
            }
            if (log.isDebugEnabled()) {
                log.debug("指定数据分区未成功,当前已经有分区--- {}" ,currentP);
            }
            return false;
        }
    }

    /**
     * 强制修改 dataRegion ，可能会引起一些 非正常的现象！！！
     * @param splitRegion
     */
    public static final void unsafeChangeDataSrv(String splitRegion){
       if(!beginDataSrv(splitRegion)){
           if (log.isDebugEnabled()) {
               log.debug("线程 强制切换指定数据分区 --- {}" ,splitRegion);
           }
           dataRuntimeThreadLocal.remove();
           dataRuntimeThreadLocal.set(new ThinkDataRuntime(splitRegion));
       }
    }

    public static ThinkDataRuntime getDataSrvRuntimeInfo(){
        if(dataRuntimeThreadLocal.get() == null) {
            final ThinkSecurityToken token = ThinkSecurityTokenTransferManager.getToken();

            if( token!=null){
                beginDataSrv(token.getCurrentRegion());
            }
        }


        return dataRuntimeThreadLocal.get();
    }

    public static final void registerThinkThings(Object o){
        //log.info("注册组件...");
        if( o instanceof DataModelBuilder){
            //log.info("注册DataModalBuilder");
            modelBuilder = (DataModelBuilder) o;
        }
    }

    public static DataModelBuilder getModelBuilder() {
        DataModelBuilder.checkInit();
//        log.info("get getModalBuilder ----- {}" , modalBuilder ==null?"NULL":"not null");
        return modelBuilder;
    }


    /**
     * 注册filter
     * @param filter
     */
    public static void registeDataFilter(ThinkDataFilter filter){
        if(registedFilters.size() == 1){
            if(registedFilters.get(0) instanceof DefaultThinkDataFilter){
                registedFilters.remove(0);
            }
        }
        registedFilters.add(filter);
    }


    /**
     * 缓存已经初始化的 数据库表
     * @param tableName
     */
    public static final void recordTableInit(String tableName){
        if(initTables.contains(tableName.trim())){
            return;
        }else{
            initTables.add(tableName);
        }
    }

    /**
     * 数据库表是否已经初始化
     * @param tableName
     * @return
     */
    public static final boolean isTableInitialized(String tableName){
        return initTables.contains(tableName.trim());

    }


    /**
     * 查找已经缓存了的初始化的表名
     * @param preStr
     * @return
     */
    public static final List<String> findInitializedTableNameListFromCache(String preStr){
        Iterator<String> iterable =initTables.iterator();
        List<String> list =new ArrayList<>();
        while (iterable.hasNext()){
            String t = iterable.next();
            if(t.startsWith(preStr)){
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 启用数据校验
     */
    public static final void enableVerification(){
        verificationAble  = true;
    }

    public static final void disableVerification(){
        verificationAble  = false;
    }

    public static final boolean verificationAble(){
        return verificationAble;
    }

    /**
     * 注册参数 值校验器
     * @param targetBeanClass
     * @param validator
     * @param <T>
     */
    public  static final <T> void registerBeanValidator(Class<T> targetBeanClass , ThinkKeyValidator validator){
        if (log.isDebugEnabled()) {
            log.debug("为对象[{}]注册指定的值校验器",targetBeanClass!=null?targetBeanClass.getName():"NULL CLASS");
        }
        ThinkDataValidator.registerBeanValidator(targetBeanClass,validator);
    }


    public static List<ThinkDataFilter> filters(){
        return registedFilters;
    }



    public static final <T extends SimplePrimaryEntity> void addInitializationData(T t) {
        ThinkDataInitializationDataHolder.hold(t);
    }
    public static final <T extends SimplePrimaryEntity> void addInitializationDataList(List<T> list){
        if(list.size() >0) {
            ThinkDataInitializationDataHolder.holdList( list);
        }
    }
}
