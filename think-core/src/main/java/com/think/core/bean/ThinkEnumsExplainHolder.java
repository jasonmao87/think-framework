package com.think.core.bean;

import com.think.core.bean.util.ObjectUtil;
import com.think.structure.ThinkExplainList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/1/14 16:05
 * @description : 枚举 解释  holder
 */
public class ThinkEnumsExplainHolder {
    private static final Map<String, ThinkExplainList> holder = new ConcurrentHashMap<>();

    protected static final <T extends _Entity>  ThinkExplainList getThinkExplainList(T  t){
        Class<T> cls = (Class<T>) t.getClass();
        String key = Long.toHexString(cls.hashCode()) + "_" + cls.getName().length();
        if(holder.containsKey(key)){
            return holder.get(key);
        }else{
            ThinkExplainList thinkExplainList = ObjectUtil.doThinkEntityTEnumExplain(t);
            holder.put(key,thinkExplainList);
            return holder.get(thinkExplainList);
        }

    }


}
