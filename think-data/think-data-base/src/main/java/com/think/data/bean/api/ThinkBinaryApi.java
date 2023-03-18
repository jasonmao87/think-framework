package com.think.data.bean.api;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.result.ThinkResult;
import com.think.core.bean.TbBinaryEntity;

import java.io.File;
import java.util.List;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/27 20:06
 * @description : TODO
 */
public interface ThinkBinaryApi {

    TbBinaryEntity buildBean(byte[] data) ;

    TbBinaryEntity buildBean(byte[] data,String name ,String typeName);

    TbBinaryEntity buildFile(File file,String typeName);


    ThinkResult<Integer> store(TbBinaryEntity entity);

    TbBinaryEntity get(long id);

   ThinkResult<Integer> remove(long id);

   ThinkResult<Integer> updateName(long id ,String name );

   List<TbBinaryEntity> listByType(String typeName);

   long countByType(String typeName);


   List<TbBinaryEntity> listByNameLike( String nameLikeStr);

   long countByNameLike(String nameLikeStr);


   List<TbBinaryEntity> ListByNameLikeAndType(String nameLikeStr ,String typeName );

    long countByNameLikeAndType(String nameLikeStr ,String typeName );



}
