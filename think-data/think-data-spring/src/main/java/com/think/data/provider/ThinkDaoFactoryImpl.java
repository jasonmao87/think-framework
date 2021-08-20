package com.think.data.provider;

import com.think.core.bean.SimplePrimaryEntity;
import com.think.core.bean.SimpleRefEntity;
import com.think.data.dao.ThinkDao;
import com.think.data.dao.ThinkDaoFactory;
import com.think.data.dao.ThinkSplitPrimaryDao;
import com.think.data.dao.ThinkSplitRefDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @Date :2021/8/18
 * @Name :ThinkDaoManager
 * @Description : 请输入
 */
@Component
public class ThinkDaoFactoryImpl implements ThinkDaoFactory {

    @Autowired
    private JdbcTemplate template;



    public <T extends SimplePrimaryEntity> ThinkDao<T> getDao(Class<T> target){
        ThinkDaoImpl<T> tThinkDao = new ThinkDaoImpl<>(target);
        tThinkDao.setJdbcTemplate(template);
        return tThinkDao;
    }

    public <T extends SimpleRefEntity>ThinkSplitRefDao<T> getSplitRefDao(Class<T> target){
        ThinkSplitRefDaoImpl<T> refDao = new ThinkSplitRefDaoImpl(target);
        refDao.setJdbcTemplate(template);
        return refDao;
    }

    public <T extends SimplePrimaryEntity> ThinkSplitPrimaryDao<T> getSplitDao(Class<T> target){
        ThinkSplitDaoImpl splitDao = new ThinkSplitDaoImpl(target);
        splitDao.setJdbcTemplate(template);
        return splitDao;
    }



}
