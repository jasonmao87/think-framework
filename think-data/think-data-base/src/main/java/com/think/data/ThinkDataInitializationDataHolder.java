package com.think.data;

import com.think.core.bean.SimplePrimaryEntity;
import com.think.data.exception.ThinkDataRuntimeException;
import com.think.data.model.ThinkTableModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  2020/12/25
 *  JasonMao
 * 2020/12/25
 * :   用于thinkData 数据初始化，会在启动后，构建数据库表后，一次性insert到 对应数据库表中！
 */
@Slf4j
public class ThinkDataInitializationDataHolder {

    private static final Map<Class,List> initDataHolder = new HashMap<Class, List>();

    /**
     * hold 一条数据
     * @param data
     * @param <T>
     */
    protected static  <T extends SimplePrimaryEntity> void hold( T data) throws ThinkDataRuntimeException {
        ThinkTableModel tableModel = Manager.getModelBuilder().get(data.getClass());
        if(tableModel.isYearSplitAble() || tableModel.isPartitionAble()){
            throw new ThinkDataRuntimeException("不适宜为拆分表和分区表设置初始化数据");
        }
        List<T> list = initDataHolder.getOrDefault(data.getClass(),new ArrayList());
        if(list.contains(data) == false){
            list.add(data);
//            log.info("将会在启动时候初始化 : {}   -- {} " ,data ,data.getClass().getName());
        }

        initDataHolder.put(data.getClass(),list);
    }


    protected static  <T extends SimplePrimaryEntity> void holdList(  List<T> dataList){
//        dataList.forEach(t->hold(targetClass,t));
        for (T t : dataList) {
            hold(t);
        }
    }


    /**
     * 读取并移除 数据
     * @param targetClass
     * @param <T>
     * @return
     */
    public static final <T extends SimplePrimaryEntity> List<T> getInitData(Class<T> targetClass){
        try {
            if (initDataHolder.containsKey(targetClass)) {

                List<T> dataList =  initDataHolder.get(targetClass);
                if (log.isDebugEnabled()) {
                    log.debug("读取待初始化数据{}条，类型 ： {}" , dataList.size(), targetClass.getName());
                }
                if(dataList!=null) {
                    return dataList;
                }
                //return initDataHolder.get(dataList);
            }

        }finally {
            //initDataHolder.remove(targetClass);
        }
        return new ArrayList<>();
    }

    public static final <T extends SimplePrimaryEntity> void removeInitData(Class<T> targetClass){
        initDataHolder.remove(targetClass);
    }


    public static final <T extends SimplePrimaryEntity> boolean containsData(Class<T> targetClass){
        return initDataHolder.containsKey(targetClass);
    }


}
