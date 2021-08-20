package com.think.data.provider;

import com.think.common.data.mysql.ThinkFilterBean;
import com.think.common.data.ThinkFilterOp;
import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.util.DateUtil;
import com.think.common.util.FastJsonUtil;
import com.think.common.util.IdUtil;
import com.think.common.util.StringUtil;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.data.Manager;
import com.think.data.ThinkDataRuntime;
import com.think.data.filter.ThinkDataFilter;
import com.think.data.model.DataModelBuilder;
import com.think.data.model.ThinkTableModel;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class _DaoSupport{
    private static final List<String> tableInitSQLList = new ArrayList<>();

    protected static <T extends _Entity> void callInsert(T t){
        ThinkDataRuntime rt = Manager.getDataSrvRuntimeInfo();
        doCallInsert(t,rt);
    }


    protected static <T extends _Entity> void callInsert(List<T> tList){
        ThinkDataRuntime rt = Manager.getDataSrvRuntimeInfo();
        for(T t :tList){
            doCallInsert(t,rt);
        }
    }

    private static  <T extends _Entity> void doCallInsert(T t ,ThinkDataRuntime rt ){
        for(ThinkDataFilter filter : Manager.filters()){
            filter.beforeExecuteInsert(t);
        }
        if( rt!=null){
            if(StringUtil.isNotEmpty(rt.getPartitionRegion())){
                t.setPartitionRegion(rt.getPartitionRegion());
            }
        }
        //处理 thinkLinkedId
        if(StringUtil.isEmpty(t.getThinkLinkedId())){
            try{
                t.setThinkLinkedId(IdUtil.nextShortId());
            }catch (Exception e){}
        }
    }

    protected static <T extends _Entity> void callUpdate(T t, ThinkUpdateMapper mapper){
        for(ThinkDataFilter filter :Manager.filters()){
            if(t !=null){
                filter.beforeExecuteUpdateEntity(t);
            }else {
                filter.beforeExecuteUpdateMapper(mapper);
            }
        }
    }

//    public static <T extends _Entity>  void initTable(JdbcTemplate template, Class<T> targetClass , String tableName,int splitYear){
//        try {
//            if (!Manager.isTableInitialized(tableName)) {
//                ThinkTableModal modal = Manager.getModalBuilder().get(targetClass);
//                if(log.isDebugEnabled()){
//                    log.debug(JSONObject.toJSONString(modal));
//                }
//                String sql = ThinkDataDDLBuilder.createSpiltSQL(modal,splitYear);
//                template.update(sql);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            if(log.isErrorEnabled()){
//                log.error("创建数据库表异常:" ,e);
//            }
//
//        }
//    }

    /**
     * 获取 基础表名称
     * @param tClass
     * @param <T>
     * @return
     */
    protected static <T extends _Entity> String baseTableName( Class<T> tClass){
        return finalSplitTableName(tClass,-1);
    }

    protected static <T extends _Entity>  String finalSplitTableName(Class<T> tClass ,int splitYear){
        String baseTableName =  DataModelBuilder.tableName(tClass) ;
        String tableName = null;
        if(splitYear > 2000) {
            tableName= baseTableName+ "_split_" + splitYear;
        }else{
            tableName= baseTableName;
        }
        return tableName;
    }


    /**
     * 通过id 计算 准确的分割年份
     * @param id
     * @return
     */
    protected static final int computeSpiltYearById(long id){
        return DateUtil.year(IdUtil.idToDate(id));
    }

    /**
     * 解决 id 值 可能被解析成 integer的问题
     * @param x
     * @return
     */
    public static long getIdValueForPossibleInteger(Serializable x){
        if(x instanceof Integer){
            return Long.valueOf(x.toString());
        }else  if( x instanceof  Long){
            return (Long)x ;
        }else{
            return Long.valueOf(x.toString());
        }
    }

    /**
     * 通过filter 计算数据可能存在表空间分割年份
     * @param sqlFilter
     * @param showSplitTables
     * @param <T>
     * @return
     */
    protected static final  <T extends SimplePrimaryEntity> int[] possibleSplitYears(ThinkSqlFilter<T> sqlFilter, List<String> showSplitTables){
        if(sqlFilter.getFilterSplitYear()>2000){
            if(log.isDebugEnabled()){
                log.debug("sqlFilter 指定了查询年份，将不在额外分析，直接限制查询年份为：{}",sqlFilter.getFilterSplitYear());
            }
            return new int[]{sqlFilter.getFilterSplitYear()};
        }

//        if(log.isDebugEnabled()){
//            log.debug("analysis think Sql Filter ： {} " , FastJsonUtil.parseToJSON(sqlFilter));
//        }
        boolean locationAble = false;
        long maxId =  Long.MAX_VALUE ;
        long minId = 0L ;         // max min 应该 取最小并集
        for(ThinkFilterBean fb : sqlFilter.getBeans()){
            if(fb.getKey().equalsIgnoreCase("id")){
                ThinkFilterOp op = fb.getOp();
                if(op == ThinkFilterOp.EQ){
                    long id = getIdValueForPossibleInteger(fb.getValues()[0]);
                    return new int[]{computeSpiltYearById(id)} ;
                }
                if(op == ThinkFilterOp.LE || op ==ThinkFilterOp.LEE ){
                    long id  =  getIdValueForPossibleInteger(fb.getValues()[0]);
                    if(id < maxId){
                        maxId = id ;
                    }
                    locationAble =true;

                }
                if(op == ThinkFilterOp.LG || op ==ThinkFilterOp.LGE){
                    long id =  getIdValueForPossibleInteger(fb.getValues()[0]);
                    if(id > minId){
                        minId = id ;
                    }
                    locationAble =true;
                }
                if(op == ThinkFilterOp.BETWEEN_AND){
                    long small = getIdValueForPossibleInteger(fb.getValues()[0]);
                    long big = getIdValueForPossibleInteger(fb.getValues()[0]);
                    if(log.isDebugEnabled()){
                        log.debug("analysis between {} and {} " , small,big);
                    }
                    if(small> minId){
                        minId = small;
                    }
                    if(big < maxId){
                        maxId = big;
                    }
                    locationAble =true;
                    if(log.isDebugEnabled()){
                        log.debug("current  {} -  {} " , minId,maxId);
                    }
                }
            }
        }//end of for
        int maxY = computeSpiltYearById(maxId);
        int minY = computeSpiltYearById(minId);
        List<Integer> list = getAllSplitYearSuffix(sqlFilter.gettClass(),showSplitTables,sqlFilter.isDesc());
        int initedMax = -1 ;
        int initedMin =  -1 ;
        if(list.size() > 0){
            if(locationAble) {
                if (sqlFilter.isDesc()) {
                    initedMax = list.get(0);
                    initedMin = list.get(list.size() - 1);
                } else {
                    initedMin = list.get(0);
                    initedMax = list.get(list.size() - 1);
                }
                if (maxY > initedMax) {
                    maxY = initedMax;
                }
                if (minY < initedMin) {
                    minY = initedMin;
                }
                if (minY > maxY) {
                    if (log.isDebugEnabled()) {
                        log.debug(" compute result is min largethan max {} {} ");
                    }
                    return new int[]{};
                } else {
                    int[] arr = new int[maxY - minY + 1];
                    for (int i = 0; i < arr.length; i++) {
                        if (sqlFilter.isDesc()) {
                            arr[i] = maxY - i;
                        } else {
                            arr[i] = i + minY;
                        }
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("{}", Arrays.toString(arr));
                    }

                    return arr;
                }
            }else{
                int[] arr = new int[list.size()];
                for(int i = 0 ; i < list.size();i++){
                    arr[i] = list.get(i);
                }
                return arr;
            }
        }else{
            if(log.isDebugEnabled()){
                log.debug("数据可能不存在");
            }
            return new int[]{};
        }

    }

    /**
     * 所有 按年切割表的年份 后缀
     * @param targetClass
     * @param showSplitTables
     * @param desc
     * @param <T>
     * @return
     */
    public static final <T extends _Entity> List<Integer> getAllSplitYearSuffix(Class targetClass, List<String> showSplitTables , boolean desc) {
        List<String> stringList = showSplitTables;
        List<Integer> list = new ArrayList<>();


        for(String t : stringList){
            String ts = t.toLowerCase().replace(( _DaoSupport.baseTableName(targetClass).toLowerCase() +"_") ,"");
            String[] splitS = ts.split("split_");
            /** 如果 存在 异常数据进来，以规避 ArrayIndexOutOfBoundsException 风险 */
            if(splitS.length>1) {
                int y = Integer.parseInt(splitS[1]);
                list.add(y);
            }
        }
        list.sort((x,y)->{
            if(desc){
                return x>y?-1:x<y?1:0;
            }else{
                return x>y?1:x<y?-1:0;
            }
        });

        if(log.isDebugEnabled()){
            log.debug(" {} splits -> {} ",desc?"DESC":"ASC" , Arrays.toString(list.toArray()));
        }
        return list;
    }


    /**
     * 提取 VO中的 KEY (如果vo中没有Id 字段，但是数据库查询语句中，会多出一个id key ，id 为必传KEY ，)
     * @param targetClass
     * @param voClass
     * @param <T>
     * @param <V>
     * @return
     */
    protected static final  <T extends _Entity ,V extends BaseVo<T>> String[] voKeys(Class<T> targetClass, Class<V> voClass){
        List<String> keys = new ArrayList<>();
        List<Field> list =  ClassUtil.getFieldList(voClass);
        boolean containsId = false;
        ThinkTableModel tableModal = Manager.getModelBuilder().get(targetClass);
        for(Field f : list){
            if(tableModal.containsKey(f.getName())){
                String k = f.getName();
                keys.add(k);
                if(k.equalsIgnoreCase("id")){
                    containsId = true;
                }
            }
        }
        if(containsId == false){
            keys.add(0,"id");
        }
        if(keys.size() > 0) {
            return keys.toArray(new String[keys.size()]);
        }else{
            return new String[]{"*"};
        }
    }
}
