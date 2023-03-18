package com.think.structure;

import com.think.core.bean.util.ObjectUtil;
import com.think.exception.ThinkException;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/23 10:46
 * @description : 字节传输对象
 *  用于在一些特殊场景中，处理 和转换  不同服务间 不存在的 类 的对象
 */
public class ByteBean implements Serializable {

    private static final long serialVersionUID = 3465256009749986008L;
    private byte[] objBytes ;

    private String className ;


    private ByteBean() {
    }

    public static final <T extends Serializable> ByteBean valueOf(T t){
        ByteBean bean = new ByteBean();
        String classNameString = t.getClass().getName();
        bean.objBytes = ObjectUtil.serializeObject(t);
        bean.className = t.getClass().getName();
        return bean;
    }


    private Class valueType() throws ClassNotFoundException, ThinkException {
        if(this.className != null) {
            return Class.forName(className);
        }
        throw new ThinkException("ByteBean 尚未被正确初始化构建");
    }

//    public T entryValue() throws ClassNotFoundException,ThinkException{
//        return (T) objValue();
//    }

    public Serializable value() throws ClassNotFoundException,ThinkException {
        if(this.className != null) {
            return (Serializable) ObjectUtil.deserialization(objBytes, valueType());
        }
        throw new ThinkException("ByteBean 尚未被正确初始化构建");
    }



}
