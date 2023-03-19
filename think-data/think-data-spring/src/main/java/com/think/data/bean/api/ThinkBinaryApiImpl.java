package com.think.data.bean.api;

import com.think.common.result.ThinkResult;
import com.think.common.util.DateUtil;
import com.think.common.util.FileUtil;
import com.think.core.bean.TbBinaryEntity;
import com.think.core.bean.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/27 20:16
 * @description : TODO
 */
@Repository
@Slf4j
public class ThinkBinaryApiImpl implements ThinkBinaryApi {

    private JdbcTemplate template;

    public JdbcTemplate getTemplate() {
        return template;
    }

    final String tableName = "tb_binary_data";
    private boolean init = false;
    @Autowired
    public ThinkBinaryApiImpl(JdbcTemplate template) {
        this.template = template;
//        doIniting();
    }
//
//    private void doIniting(){
//
//
//        try {
//            ThinkThreadExecutor.runDelay(() -> {
//                tableInit();
//            }, 60);
//        }catch (Exception e){}
//
//    }
//


    private void tableInit(){
        if(init ){
            return;
        }
        String sql = "CREATE TABLE  " + tableName + "  (\n" +
                "  `id` bigint(0) NOT NULL,\n" +
                "  `binaryData` varbinary(60000) NULL,\n" +
                "  `dataLength` int(0) NULL,\n" +
                "  `name` varchar(64) NULL,\n" +
                "  `typeName` varchar(256) NULL,\n" +
                "  `createTime` datetime(0) NULL,\n" +
                "  `updateTime` datetime(0) NULL,\n" +
                "  PRIMARY KEY (`id`)\n" +
                ");";
        log.info("init table  >>>>  {}" ,sql  );
        try{
            this.init = true;
            template.update(sql);

        }catch (Exception e){
//            log.error("error while init table ",e);
        }
    }




    @Override
    public TbBinaryEntity buildBean(byte[] data) {
//        tableInit();
        TbBinaryEntity t= new TbBinaryEntity();
        t.setBinaryData(data);
        t.setCreateTime(DateUtil.now());
        t.setDataLength(data.length);
        t.setUpdateTime(DateUtil.now());
        t.setName("");

        return t;
    }

    @Override
    public TbBinaryEntity buildBean(byte[] data, String name, String typeName) {
        final TbBinaryEntity t = buildBean(data);
        t.setTypeName(typeName);
        t.setName(name);

        return t;
    }

    @Override
    public TbBinaryEntity buildFile(File file ,String typeName ) {
        try {
            final byte[] bytes = FileUtil.fileToBytes(file);
            String name =file.getName();
            return buildBean(bytes,name,typeName);
        }catch (Exception e){}
        return null;
    }

    @Override
    public ThinkResult<Integer> store(TbBinaryEntity entity) {
        tableInit();
        String sql = "INSERT INTO " +tableName + " (id,binaryData,dataLength,name,typeName ,createTime, updateTime )VALUES (?,?,?,?,?,?,?) ; ";
        int i = template.update(sql,entity.getId(),entity.getBinaryData(),entity.getDataLength(),entity.getName(),entity.getTypeName(),entity.getCreateTime(),entity.getUpdateTime());
        if(i > 0){
            return ThinkResult.success(i);
        }
        return ThinkResult.fastFail();
    }

    @Override
    public TbBinaryEntity get(long id) {
        tableInit();
        Map map = template.queryForMap("select id,binaryData,dataLength,name,typeName ,createTime, updateTime   from   "+ tableName + " where id = ? ",id );
        return ObjectUtil.mapToBean(map,TbBinaryEntity.class);
    }

    @Override
    public ThinkResult<Integer> remove(long id) {
        tableInit();
        final int update = template.update("delete from " + tableName + " where id = ?", id);
        return ThinkResult.success(update);
    }

    @Override
    public ThinkResult<Integer> updateName(long id, String name) {
        return ThinkResult.notSupport();
    }

    @Override
    public List<TbBinaryEntity> listByType(String typeName) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public long countByType(String typeName) {
        return 0;
    }

    @Override
    public List<TbBinaryEntity> listByNameLike(String nameLikeStr) {
        return null;
    }

    @Override
    public long countByNameLike(String nameLikeStr) {
        return 0;
    }

    @Override
    public List<TbBinaryEntity> ListByNameLikeAndType(String nameLikeStr, String typeName) {
        return null;
    }

    @Override
    public long countByNameLikeAndType(String nameLikeStr, String typeName) {
        return 0;
    }
}

