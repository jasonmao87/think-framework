package com.think.data.provider;

import com.mysql.cj.api.exceptions.ExceptionInterceptor;
import com.mysql.cj.api.log.Log;
import com.mysql.cj.jdbc.Blob;
import com.think.core.bean.BlobEntity;
import com.think.core.enums.TEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Properties;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/27 15:50
 * @description : TODO
 */
@Repository
public class ThinkBlobDao {

    @Autowired
    JdbcTemplate template;


    private  void createTable(){
        String sql = "CREATE TABLE `thinkdid-cloud-dc`.`tb_binary_data`  (\n" +
                "  `id` bigint(0) NOT NULL,\n" +
                "  `dataKey` varchar(255) NULL,\n" +
                "  `dataRegion` varchar(255) NULL,\n" +
                "  `dataName` varchar(255) NULL,\n" +
                "  `data` varbinary(0) NULL,\n" +
                "  `remark` varchar(255) NULL,\n" +
                "  `createTime` datetime(0) NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");";
    }

    public void save(BlobEntity entity){
        String sql = "INSERT INTO `tb_binary_data` (id,dataKey,dataRegion,dataName ,data,remak,createTime)VALUES (?, ?, ?, ?, ?, ?, ?)";

        template.update(sql,entity.getId(),entity.getDataKey(),entity.getDataRegion(), entity.getDataName(), entity.getData(), entity.getRemark(),entity.getCreateTime());


    }


    public byte[] read(long id){
        final Map<String, Object> map = template.queryForMap("select * from tb_binary_data where id = ?",id);
        byte[] data = (byte[]) map.get("data");
        for (byte datum : data) {
            System.out.print(datum );
            System.out.print(" ");
        }

        return data;

    }
}
