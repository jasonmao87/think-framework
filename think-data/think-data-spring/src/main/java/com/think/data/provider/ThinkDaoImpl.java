package com.think.data.provider;

import com.think.core.bean.SimplePrimaryEntity;
import com.think.data.dao.ThinkDao;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Date :2021/8/18
 * @Name :ThinkSimpleDao
 * @Description : 基本dao的实现
 */
public class ThinkDaoImpl<T extends SimplePrimaryEntity> extends ThinkDaoProvider<T> {

    ThinkDaoImpl(Class<T> targetClass){
        super(targetClass);
    }


    public static <T extends SimplePrimaryEntity>  ThinkDao<T> staticBuild(Class<T> targetClass,JdbcTemplate jdbcTemplate){
        ThinkDao<T> dao = new ThinkDaoImpl<>(targetClass);
        ((ThinkDaoImpl)dao).setJdbcTemplate(jdbcTemplate);
        return dao;
    }

}
