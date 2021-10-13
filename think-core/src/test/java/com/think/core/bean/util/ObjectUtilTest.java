package com.think.core.bean.util;

import com.think.common.util.DateUtil;
import com.think.common.util.L;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.think.core.bean.util.ObjectUtil.*;

public class ObjectUtilTest {

    @Test
    public  void main() throws Exception{
        List<TbX_FOR_TEST> list=new ArrayList<>();
        for(int i= 0 ; i < 100 ; i ++){
            list.add(new TbX_FOR_TEST().setDate(DateUtil.computeAddDays(DateUtil.now(),i)).setName("UI-你知道巨大大剂量的煎熬了多久了 的急啊离开军队垃圾的啦 大当家垃圾的老咔叽大剂量的急啊离开东京拉开大家立刻决定离开就大家来看建档立卡就对啦-" +i));
        }

        List<byte[]> listPro =new ArrayList<>();
        List<byte[]> listJdk = new ArrayList<>();
        long begin =  System.currentTimeMillis();

        for (TbX_FOR_TEST tbX : list) {
            byte[] x = protostufSerializeObject(tbX);
            listPro.add(x);

//            L.print(x.length + "完成");
        }

        long end = System.currentTimeMillis();
        L.print("protostuff 序列化完成 " + (end -begin));


        begin = System.currentTimeMillis();
        for (TbX_FOR_TEST tbX : list) {
//            L.print(x.length + "完成");
            byte[] x = serializeObject(tbX);
            listJdk.add(x);
        }
        end = System.currentTimeMillis();
        L.print("JDK 序列化完成 " + (end -begin));
        TbX_FOR_TEST xx =null;
        L.print("反序列化");
        begin = System.currentTimeMillis();
        for (byte[] bytes : listPro) {
            TbX_FOR_TEST x =  protostufDeserialization(bytes, TbX_FOR_TEST.class);
            xx = x ;
        }
        end = System.currentTimeMillis();
        L.print("protostuff 反序列化完成 " + (end -begin));

        L.print(xx );

        for (byte[] bytes : listJdk) {
            TbX_FOR_TEST x =  deserialization(bytes, TbX_FOR_TEST.class);
            xx =x ;
        }
        end = System.currentTimeMillis();
        L.print("jdk 反序列化完成 " + (end -begin));

        L.print(xx);






    }
}