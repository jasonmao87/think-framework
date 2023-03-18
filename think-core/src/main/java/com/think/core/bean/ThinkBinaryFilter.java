package com.think.core.bean;

import com.think.common.data.mysql.ThinkFilterBean;
import com.think.common.util.StringUtil;

import java.util.*;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/27 20:23
 * @description : TODO
 */
public class ThinkBinaryFilter {

    private String nameQuery ;

    private String nameValue ;

    private String typeQuery ;
    private String typeValue ;

    private String createDateQuery ;

    private Date[] createTimeValue ;

    private String updateDateQuery;

    private Date[] updateTimeValue ;


    public ThinkBinaryFilter() {
    }


    public ThinkBinaryFilter nameLike(String nameValue){
        this.nameQuery = " AND name like ? " ;
        this.nameValue = nameValue;
        return this;
    }

    public ThinkBinaryFilter nameEq(String nameValue){
        this.nameQuery = " AND name  = ? " ;
        this.nameValue = nameValue;
        return this;
    }

    public ThinkBinaryFilter typeEq(String typeName){
        this.typeQuery = " AND typeName = ? ";
        this.typeValue = typeName;
        return this;
    }


    public ThinkBinaryFilter createAfter(Date from){
        return createBetweenAnd(from,null);
    }
    public ThinkBinaryFilter createBefore(Date end){
        return createBetweenAnd(null,end);
    }
    public ThinkBinaryFilter createBetweenAnd(Date from ,Date end){

        return this;
    }


    public Map getQuery(){
        List list =new ArrayList();
        StringBuilder sb = new StringBuilder(" WHERE  1 =1 ");
        if(StringUtil.isNotEmpty(nameQuery)){
            sb.append(nameQuery);
            list.add(nameValue);
        }
        if(StringUtil.isNotEmpty(typeQuery)){
            sb.append(typeQuery);
            list.add(typeValue);
        }
        Map map =new HashMap();
        map.put("sql",sb.toString());
        map.put("params",list);
        return map;
    }


}
