package com.think.data.dao;

import com.think.common.data.mysql.ThinkSqlFilter;
import com.think.common.data.mysql.ThinkUpdateMapper;
import com.think.common.result.ThinkResult;
import com.think.core.annotations.Remark;
import com.think.core.bean.BaseVo;
import com.think.core.bean.SimplePrimaryEntity;

import java.util.List;

public interface ThinkDao<T extends SimplePrimaryEntity> extends ThinkSelector<T> {

    ThinkResult<T> insert(T t);

    ThinkResult<Integer> batchInsert(List<T> tList);


    @Deprecated
    @Remark("尽量不推荐使用全量的update方法，毕竟会引起很多不必要的麻烦，比如覆盖某些不该覆盖的值等")
    ThinkResult<Integer> update(T t);


    <V extends BaseVo<T>> ThinkResult<Integer> update(V v,long id );

    <V extends BaseVo<T>> ThinkResult<Integer> update(V v,ThinkSqlFilter<T> sqlFilter );



    ThinkResult<Integer> update(ThinkUpdateMapper<T> updaterMapper );

    ThinkResult<Integer> delete(long id);

    ThinkResult<Integer> delete(Long[] ids);

    ThinkResult<Integer> delete(ThinkSqlFilter<T> sqlFilter);


    ThinkResult<Integer> physicalDelete(long id);

    ThinkResult<Integer> physicalDelete(Long[] ids);

    ThinkResult<Integer> physicalDelete(long[] ids);




}
