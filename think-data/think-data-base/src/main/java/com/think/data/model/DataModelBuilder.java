package com.think.data.model;

import com.think.common.util.StringUtil;
import com.think.common.util.TVerification;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.*;
import com.think.core.bean.TFlowState;
import com.think.core.bean._Entity;
import com.think.core.bean.util.ClassUtil;
import com.think.data.Manager;
import com.think.data.ThinkDataRuntime;
import com.think.data.exception.ThinkDataModelException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class DataModelBuilder {
    /**
     * 托管的 模型对象
     */
    private final static Map<Class, ThinkTableModel> thinkModalHolder = new HashMap<>();
    private static final ThreadLocal<Integer> indexValueThreadLocal = new ThreadLocal<>();
    private DataModelBuilder() { }
    static {
        Manager.registerThinkThings( new DataModelBuilder());
    }

    public static  final void checkInit(){
        // do nothing
    }

    public final static List<Class> buildClassList(){
        List<Class> list = new ArrayList<>();
        //new ThinkFastList<Class>(Class.class);
        Iterator<Class> iterator =thinkModalHolder.keySet().iterator();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * 唯一对外方法
     * @param tClass
     * @param <T>
     * @return
     */
    public final <T extends _Entity> ThinkTableModel get(Class<T> tClass){
        try{
            if(thinkModalHolder.containsKey(tClass)){
                return thinkModalHolder.get(tClass);
            }

            ThinkTableModel modal = Manager.getModelBuilder().build(tClass);
            if( modal!=null){
                return modal;
            }
            return build(tClass);
        }catch (Exception e){
            if (log.isErrorEnabled()) {
                log.error("构建数据模型抛出的异常信息",e);
            }
            //e.printStackTrace();
            return null;
        }
    }

    public static final  <T extends _Entity> String tableName(Class<T> tClass) throws ThinkDataModelException {
        ThinkTableModel modal = Manager.getModelBuilder().get(tClass);
//        String buildTableName = "";
        if(modal!=null ){
            if(modal.isPartitionAble()){
                if(Manager.getDataSrvRuntimeInfo() ==null ){
                    if (log.isDebugEnabled()) {
                        log.debug("未找到必要的RUNTIME INFO， 为了正常运行返回了 {}分区" , ThinkDataRuntime.NONE_PART);
                    }
                    return modal.getTableName() + "_" + ThinkDataRuntime.NONE_PART;
                    //throw new ThinkDataRuntimeException("未找到必要的RUNTIME INFO， 请检查是否是正常构建");
                }else{
//                    log.info("FIND {} ",Manager.getDataSrvRuntimeInfo().getPartitionRegion());
                    return modal.getTableName() + "_" + Manager.getDataSrvRuntimeInfo().getPartitionRegion();
                }

            }
            return modal.getTableName();
        }
        throw new ThinkDataModelException("无法找到或构建对象模型 ： " + tClass.getCanonicalName());
    }


    private final <T extends _Entity> ThinkTableModel build(Class<T> tClass) throws ThinkDataModelException {
        if(tClass == null){
            throw new ThinkDataModelException("Class对象不能为 NULL");
        }
        if(thinkModalHolder.containsKey(tClass)){
            return thinkModalHolder.get(tClass);
        }

        ThinkTableModel tableModal = preInit(tClass);
        List<Field> fields  = ClassUtil.getFieldList(tClass);
        //初始化 列模型
        List<ThinkColumnModel> columnModalList = new ArrayList<>();
        for(Field f : fields){

            ThinkColumnModel modal = buildColumn(f);
            if(modal!= null){
                columnModalList.add(modal);
            }

        }
        ThinkColumnModel[] clms = new ThinkColumnModel[columnModalList.size()];
        columnModalList.toArray(clms);
        tableModal.setColumnModels(clms);
        //列模型初始化完成
        // 开始构建索引模型
        ThinkIndexes thinkIndexes = tClass.getAnnotation(ThinkIndexes.class);
        try {
            indexValueThreadLocal.set(10000);
            initIndexes(tableModal, thinkIndexes);
            tableModal.resortColumns();
            //holder it
            thinkModalHolder.put(tClass,tableModal);
        }catch (ThinkDataModelException e){
            throw e;
        } finally {
            indexValueThreadLocal.remove();
        }
//        ThinkHistoryAble historyAble = tClass.getAnnotation(ThinkHistoryAble.class);
//        if(historyAble !=null){
//            tableModal.setHistoryAble(true);
//            tableModal.setHistoryType(historyAble.dataHistoryType());
//        }
        return tableModal;
    }



    private  final ThinkColumnModel buildColumn(Field field){
        if(field.getAnnotation(ThinkIgnore.class) != null){
            return null;
        }



        if(field.getName().equalsIgnoreCase("thinkLinkedId")){
            if(Manager.isThinkLinkedIdSupportAble() == false){
                return null;
            }
        }


        String name = field.getName();
        if(name.equalsIgnoreCase("serialVersionUID") || name.equals("log") ){
            return null;
        }
        boolean isId = name.equalsIgnoreCase("id");
        ThinkColumn tColumn = field.getAnnotation(ThinkColumn.class);
        ThinkColumnModel modal = new ThinkColumnModel();
        if(field.getType() == TFlowState.class){
            ThinkStateColumn stateColumn = field.getAnnotation(ThinkStateColumn.class);
            TVerification.valueOf(stateColumn).throwIfNull(field.getName() +"为流程状态字段，必须配合ThinkStateColumn注解使用");
            modal.setComment(stateColumn.comment());
            //标记为状态流程字段，会映射出一大堆字段出来 ！
            modal.setStateModel(true);
        }
        modal.setKey(name);
        modal.setType(field.getType());
        if(field.getType().getSimpleName().equalsIgnoreCase("String")){
            if(tColumn!=null) {
                modal.setUsingText(tColumn.usingText());
            }
        }
        try {
            if (field.getType().getSuperclass()!=null &&  field.getType().getSuperclass().equals(Enum.class)) {
                modal.setEnumState(true);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(isId){
            modal.setPk(isId);
            modal.setIndexValue(9999999);
        }
        if(tColumn == null){
            modal.setLength(32);
            modal.setNullable(false);
        }else {
            modal.setLength(tColumn.length());
            modal.setNullable(tColumn.nullable());
            modal.setSensitive(tColumn.sensitive());
            modal.setDefaultValue(tColumn.defaultValue());
            modal.setEditAble(tColumn.editAble());
            modal.setFastMatchAble(tColumn.fastMatch());
            modal.setNoSetDateDefaultValue(tColumn.noSetDateDefaultValue());
        }
        if(isId){
            modal.setNullable(false);
        }
        Remark remark = field.getAnnotation(Remark.class);
        if(remark!=null){
            modal.setComment(remark.value());
        }else {
            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
            if(apiModelProperty!=null){
                modal.setComment(apiModelProperty.value());
            }
        }
        Type conoverType = field.getType();
        if(modal.isEnumState()){
            conoverType = Enum.class;
        }
        String jdbcTypeString = ThinkJdbcTypeConverter.toJdbcTypeString(conoverType,tColumn);
        if(StringUtil.isEmpty(jdbcTypeString) && (conoverType!=TFlowState.class)){;
            throw new ThinkDataModelException(name + "对应的属性（"+field.getType().getName()+"）暂时无法映射成相应的数据库列属性！" );
        }
        modal.setSqlTypeString(jdbcTypeString);
        return modal;
    }

    private final <T extends _Entity> ThinkTableModel preInit(Class<T> tClass) throws ThinkDataModelException {
        if (log.isTraceEnabled()) {
            log.trace("build class model : {}" ,tClass );
        }
        String tableName = null;
        ThinkTable tBean = tClass.getAnnotation(ThinkTable.class);
        if(tBean == null){
            throw new ThinkDataModelException("未找到ThinkTable注解，故无法初始化ThinkTable");
        }
        boolean partitionAble = tBean.partitionAble();
        boolean businessModeSplitAble = tBean.businessModeSplitAble().isEnable();
        boolean autoIncPK = false;
        String tableComment = null;
        String dsId = null;
        boolean yearSplit = false;
        if(tBean == null){
            StringBuilder tb = new StringBuilder("");
            String dClassName = tClass.getCanonicalName().replaceAll(tClass.getPackage().getName()+".","");
            for(char c : dClassName.toCharArray()){
                if(c<='Z' && c>='A'){
                    if(tb.length() > 0){
                        tb.append("_");
                    }
                }
                tb.append(c);
            }
            tableName = tb.toString().toLowerCase().replaceAll("__","_");


            dsId = ThinkTable.DEFAULT_DS_ID;
        }else {
            autoIncPK = tBean.autoIncPK();
            tableComment = tBean.comment();
            tableName = tBean.value();
            dsId = tBean.dsId();
            yearSplit = tBean.yearSplit();
        }
        if(StringUtil.isEmpty(tableComment)){
            ApiModel apiModel = tClass.getAnnotation(ApiModel.class);
            if(apiModel == null) {
                Remark remark = tClass.getAnnotation(Remark.class);
                if (remark != null) {
                    tableComment = remark.value();
                } else {
                    tableComment = "";
                }
            }else{
                tableComment = apiModel.value();
            }

        }


        ThinkTableModel modal = new ThinkTableModel(tClass,tableName,tableComment);
        modal.setPartitionAble(partitionAble);
        modal.setDataSourceId(dsId);
        modal.setAutoIncPK(autoIncPK);
        modal.setYearSplitAble(yearSplit);
        modal.setBusinessModeSplitAble(businessModeSplitAble);
        return modal;
    }



    private final void initIndexes(ThinkTableModel thinkTableModal, ThinkIndexes thinkIndexes)  {
        if(thinkIndexes !=null) {
            ThinkIndexModel[] indexModalArray = new ThinkIndexModel[thinkIndexes.indexes().length];
            for (int i=0 ; i<indexModalArray.length ;i++) {
                ThinkIndex index = thinkIndexes.indexes()[i];
                ThinkIndexModel indexModal = new ThinkIndexModel(index.keys(), index.unique());
                indexModalArray[i] = indexModal;
                for(String k :index.keys()){
//                    if(log.isDebugEnabled()) {
//                        log.debug(" {}注入索引模型 {} ",k, indexModal);
//                    }
                    ThinkColumnModel columnModel = thinkTableModal.getKey(k) ;
                    if(columnModel.isUsingText()){
                        throw new ThinkDataModelException("禁止针对text类型的键["+k+"]建立索引。");
                    }
                    if(columnModel == null){
                        StringBuilder exInfo = new StringBuilder("非法的索引列")
                                .append("[").append(thinkTableModal.getTableName()).append(".").append(k)
                                .append("],映射表对象中并不包含此列。");
                        throw new ThinkDataModelException(exInfo.toString());
                    }
                    thinkTableModal.getKey(k).setIndexModal(indexModal);
                }
                computeIndexValue(thinkTableModal,index);
            }
            thinkTableModal.setIndexModels(indexModalArray);
        }
    }

    private void computeIndexValue(ThinkTableModel thinkTableModal, ThinkIndex index) {
        if(index.keys().length <1){
            throw  new ThinkDataModelException("无法构建空索引模型");
        }
        if(false == thinkTableModal.containsKeys(index.keys())){
            String keysStr = "[";
            for(String k : index.keys()){
                keysStr += (k +" ");
            }
            keysStr +="]";
            String clms =  "[";
            for(ThinkColumnModel columnModal : thinkTableModal.getColumnModels()){
                clms += (" " + columnModal.getKey());
            }
            clms+="]";
            throw  new ThinkDataModelException("无法构建"+keysStr+"相关的索引模型，请检查字段是否存在。列清单：" + clms);
        }
        int v = indexValueThreadLocal.get();
        indexValueThreadLocal.set(v -100);
        int len = index.keys().length;
        boolean uk = index.unique();
        if(uk){
            if(len >1) {
                v -= 1000;
                for(String k : index.keys()){
                    v -- ;
                    thinkTableModal.getKey(k).setIndexValue(v );

                }
            }else{
                v += 1000;
                v --;
                String k = index.keys()[0];
                thinkTableModal.getKey(k).setIndexValue(v);
            }
        }else {
            v -= 6000;
            if(len > 1){
                v -= 1000;
                for(String k : index.keys()){
                    v -- ;
                    thinkTableModal.getKey(k).setIndexValue(v );
                }
            }else{
                v -- ;
                String k = index.keys()[0];
                thinkTableModal.getKey(k).setIndexValue(v);
            }

        }
    }
}
