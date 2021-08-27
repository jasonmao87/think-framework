package com.think.mongo.dao;

import com.think.common.data.ThinkMongoFilterOp;
import com.think.common.data.mongo.ThinkMongoFilterBean;
import com.think.common.data.mongo.ThinkMongoQueryFilter;
import com.think.mongo.ThinkMongoManager;
import com.think.mongo.model.ThinkMongoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class ThinkMongoQueryBuilder {

    protected static Query build(ThinkMongoQueryFilter queryFilter){
        int limit = queryFilter.getLimit();
        Query query =null;
        if(queryFilter.getBeans().size() == 0){
            query = new Query();
        }else{
            List<ThinkMongoFilterBean> fbs = queryFilter.getBeans();
            Map<String,List<ThinkMongoFilterBean>> fbMap = new HashMap();
            for(ThinkMongoFilterBean fb : fbs){
                if(fbMap.containsKey(fb.getKey())) {
                    fbMap.get(fb.getKey()).add(fb);
                }else{
                    ArrayList<ThinkMongoFilterBean> filterBeanList = new ArrayList<>();
                    filterBeanList.add(fb);
                    fbMap.put(fb.getKey(),filterBeanList);
                }
            }
            ThinkMongoModel modal = ThinkMongoManager.getModal(queryFilter.gettClass());
            String[] indexes = modal.getUseIndexKeys();

            Criteria criteria= initCriteria(fbMap,indexes);


            if(criteria != null) {
                query = Query.query(criteria);
            }else{
                query = new Query();
            }



        }

        //新增排序
        Sort sort = null;
        String sortKey = queryFilter.getSortKey();
        if(sortKey.equalsIgnoreCase("id")){
            sortKey = "_id";
        }
        boolean isDesc = queryFilter.isDesc();
        //List<Sort.Order> list = new ArrayList<>();
        if(isDesc){
            sort= Sort.by(Sort.Order.desc(sortKey));
        }else {
            sort= Sort.by(Sort.Order.asc(sortKey));
        }
        if(limit>0){
            return query.with(sort).limit(limit);
        }else {
            return query.with(sort);
        }

    }

    private static final Criteria buildPart(Criteria criteria ,List<ThinkMongoFilterBean> fbList){
        sort(fbList);
        ThinkMongoFilterBean filterBean = fbList.get(0);
        String realKey = filterBean.getKey().equals("id")?"_id":filterBean.getKey();
        if(criteria == null){
            criteria = Criteria.where(realKey);
        }else {
            criteria = criteria.and(realKey);
        }
        for(ThinkMongoFilterBean t : fbList) {
            switch (t.getOp()) {
                case EQ: {
                    criteria.is(t.getValues()[0]);
                    break;
                }
                case LE: {
                    criteria.lt(t.getValues()[0]);
                    break;
                }
                case LEE:{
                    criteria.lte(t.getValues()[0]);
                    break;
                }
                case LG:{
                    criteria.gt(t.getValues()[0]);
                    break;
                }
                case LGE:{
                    criteria.gte(t.getValues()[0]);
                    break;
                }
                case BETWEEN_AND:{
                    criteria.gte(t.getValues()[0]).lte(t.getValues()[1]);
                    break;
                }
                case OR:{
                    criteria.in(t.getValues());
                    break;
                }
                case IN:{
                    criteria.in(t.getValues());
                    break;
                }
                case LIKE:{
                    String v = (String) t.getValues()[0];
                    v = "^" +v + "$";
                    v = v.replaceAll("%",".*");
                    Pattern pattern =   Pattern.compile(v);
                    criteria.regex(pattern);
                }
                default:{
                }

            }
        }
        return criteria;
    }


    private static Criteria initCriteria( Map<String,List<ThinkMongoFilterBean>> fbMap,String[] indexes ) {


        Criteria criteria = null;
        for (String index : indexes) {
            if (fbMap.containsKey(index)) {
                List<ThinkMongoFilterBean> fbList = fbMap.get(index);
                boolean execute = true;
                if (containsLike(fbList)) {
                    if (fbList.size() == 1) {
                        execute = false;
                    }
                }
                if (execute) {
                    criteria = buildPart(criteria, fbList);
                    fbMap.remove(index);
                }
            }
        }

        Iterator<String> iterator = fbMap.keySet().iterator();
        Set<String> waitRemove =new HashSet<>();
        while (iterator.hasNext()){
            String k = iterator.next();
            List<ThinkMongoFilterBean> fbList = fbMap.get(k);
            boolean execute = true;
            if (containsLike(fbList)) {
                if (fbList.size() == 1) {
                    execute = false;
                }
            }
            if (execute) {
                criteria =buildPart(criteria, fbList);
//                fbMap.remove(k);
                waitRemove.add(k);
            }

        }

        for(String k: waitRemove){
            fbMap.remove(k);
        }

        Iterator<String> iteratorLast = fbMap.keySet().iterator();
        while (iteratorLast.hasNext()){
            String k = iteratorLast.next();
            List<ThinkMongoFilterBean> fbList = fbMap.get(k);
            criteria  =buildPart(criteria, fbList);
            waitRemove.add(k);

        }
        for(String k: waitRemove){
            fbMap.remove(k);
        }
        // last loop

        return criteria;
    }





    private static final void sort(List<ThinkMongoFilterBean> list){
        list.sort((x,y)->{
            boolean b = ThinkMongoFilterOp.indexValue(x.getOp()) > ThinkMongoFilterOp.indexValue(y.getOp());
            return b?-1:1;
        });
    }

    private static boolean containsLike(List<ThinkMongoFilterBean> list){
        for (ThinkMongoFilterBean fb : list){
            if(fb.getOp() == ThinkMongoFilterOp.LIKE){
                return true;
            }
        }
        return false;
    }

//    public static void main(String[] args) {
//        String s = "abc123";
//        String v= "%b%2";
//        v = "^" +v + "$";
//        v = v.replaceAll("%",".*");
//        System.out.println(v);
//        Pattern pattern = Pattern.compile(v);
//
//        System.out.println(pattern.matcher(s).find());
//
//    }

}
