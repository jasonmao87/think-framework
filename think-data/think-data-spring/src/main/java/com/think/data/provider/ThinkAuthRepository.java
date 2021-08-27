package com.think.data.provider;

import com.think.core.bean.util.ObjectUtil;
import com.think.moudles.auth.ThinkAuthModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 专门用于 用户登录的 dao
 */
@Repository
public class ThinkAuthRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    private static final String tableName = "tb_think_auth";

    /**
     * 初始化表
     */
    public void init(){
        String sql = "CREATE TABLE `"+tableName+"`  (" +
                "  `id` varchar(64) NOT NULL," +
                "  `pw` varchar(128) NOT NULL," +
                "  `bindId` bigint NOT NULL," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE INDEX `bind_account_id`(`bindId`)" +
                ")";
        jdbcTemplate.update(sql);
    }

    public ThinkAuthModel get(String id ){
//        StringBuilder sb = new StringBuilder("SELECT * from ").append(tableName).append(" where id = ? ");
        String sql = "SELECT * from " + tableName + " where id = ? ";
        Map<String,Object> map = jdbcTemplate.queryForMap(sql, id);
        if(map == null){
            return null;
        }
        return ObjectUtil.mapToBean(map,ThinkAuthModel.class);
    }


    public ThinkAuthModel get(String id ,String pw){
        Map<String,Object> map = jdbcTemplate.queryForMap("SELECT * from " + tableName + " where id = ? and pw =?", id,pw);
        if(map == null){
            return null;
        }
        return ObjectUtil.mapToBean(map,ThinkAuthModel.class);
    }

    public ThinkAuthModel get(long bindId ,String pw){
        Map<String,Object> map = jdbcTemplate.queryForMap("SELECT * from " + tableName + " where bindId = ? and pw =?", bindId,pw);
        if(map == null){
            return null;
        }
        return ObjectUtil.mapToBean(map,ThinkAuthModel.class);
    }

    public boolean insert(String id,String pw,long bindId){
        String sql = "insert into " +tableName + "(id,pw,bindid) values (?,?,?)" ;
        return jdbcTemplate.update(sql,id,pw,bindId) >0 ;
    }


    public boolean updatePw(String id ,String pw){
        String sql = "update " + tableName + " set pw = ? where id = ?";
        return jdbcTemplate.update(sql,pw,id)>0;
    }

//
//    public boolean bindAccountId(String id ,long accountId){
//        String sql = "update " + tableName + " set bindid = ? where id = ?";
//        return jdbcTemplate.update(sql,accountId,id)>0;
//    }



}
