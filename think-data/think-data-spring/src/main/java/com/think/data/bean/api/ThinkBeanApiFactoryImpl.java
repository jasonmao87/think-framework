package com.think.data.bean.api;

import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean.SimpleRefEntity;
import com.think.data.dao.ThinkDaoFactory;
import com.think.data.provider.ThinkDaoFactoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Date :2021/8/18
 * @Name :ThinkApiManager
 * @Description : 请输入
 */
@Slf4j
@Component
public class ThinkBeanApiFactoryImpl  implements ThinkBeanApiFactory{


    private ThinkDaoFactory daoFactory;


    @Autowired
    public ThinkBeanApiFactoryImpl(ThinkDaoFactoryImpl daoFactory) {
        this.daoFactory = daoFactory;
    }

    //
//    @Remark("执行API updateMap方法前针对map做数据过滤的支持 filter")
    public static ThinkApiUpdateMethodFilter thinkApiUpdateMethodFilter;

    public ThinkApiUpdateMethodFilter getThinkApiUpdateMethodFilter() {
        return thinkApiUpdateMethodFilter;
    }

    public void setThinkApiUpdateMethodFilter(ThinkApiUpdateMethodFilter thinkApiUpdateMethodFilter) {
        ThinkBeanApiFactoryImpl.thinkApiUpdateMethodFilter = thinkApiUpdateMethodFilter;
    }

    public <T extends SimplePrimaryEntity> ThinkSplitBeanApi<T> getSplitApi(Class<T> targetClass){
        return new ThinkSplitBeanApiImpl<>(daoFactory.getSplitDao(targetClass));
    }

    public <T extends SimplePrimaryEntity> ThinkBeanApi<T> getApi(Class<T> targetClass){
        return new ThinkBeanApiImpl<>(daoFactory.getDao(targetClass));
    }

    public <T extends SimpleRefEntity> ThinkSplitRefBeanApi getRefApi(Class<T> targetClass){
        return new ThinkSplitRefBeanApiImpl(daoFactory.getSplitRefDao(targetClass));
    }

    public ThinkBeanApiFactoryImpl() {
    }

    //    public static  <T extends SimplePrimaryEntity> ThinkBeanApi getApi(Class<T> tClass){
//
//
//    }
}
