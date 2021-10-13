package com.think.mongo;

import com.think.common.util.IdUtil;
import com.think.common.util.StringUtil;
import com.think.mongo.dao.ThinkMongoDao;
import com.think.structure.ThinkFastList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
//@RestController
public class WWWWW {

//    @Autowired
//    ThinkMongoDao dao;
//
//    @GetMapping("/test")
//    public Map test(int size){
//        try {
//            List<MongotestBean> list = new ThinkFastList<>(MongotestBean.class);
//            long begin = System.currentTimeMillis();
//            for (int i = 0; i < size; i++) {
//                String name = StringUtil.randomStr(16);
//                MongotestBean bean = new MongotestBean()
//                        .setHello("HELLO_" + name)
//                        .setName(name)
//                        .setSchool(name + "'s School ");
//                bean.setId(IdUtil.nextId() + "");
//                list.add(bean);
//            }
//
//            long finish = System.currentTimeMillis();
//            long start = System.currentTimeMillis();
//            int x = dao.saveAll(list).size();
//            long end = System.currentTimeMillis();
//
//            Map map = new HashMap();
//            map.put("totalData", list.size());
//            map.put("successCount", size);
//            map.put("duration", end - start);
//            map.put("listDuration", finish - begin);
//            return map;
//        }catch (Exception e){
//            e.printStackTrace();
//            Map map = new HashMap();
//            map.put("e",e);
//            map.put("totalData", "-1");
//            return map;
//        }
//    }


}
