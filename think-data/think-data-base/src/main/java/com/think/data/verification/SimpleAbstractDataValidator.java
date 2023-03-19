package com.think.data.verification;

import com.think.data.Manager;
import com.think.data.model.ThinkTableModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public abstract class SimpleAbstractDataValidator implements ThinkKeyValidator{

    public abstract void verification_int(String k ,Integer v) throws RuntimeException;

    public abstract void verification_string(String k ,String v) throws RuntimeException;

    public abstract void verification_date(String k , Date v) throws RuntimeException;

    public abstract void verification_long(String k , Long v) throws RuntimeException;

    public abstract void verification_boolean(String k , Boolean v) throws RuntimeException;


    @Override
    public void verification(Class targetClass, String k, Object v) throws RuntimeException{
        ThinkTableModel tableModel = Manager.getModelBuilder().get(targetClass);
        if(v == null){
            boolean nullAble = tableModel.getKey(k).isNullable();
            if(nullAble == false) {
                throw exception(targetClass.getName() + "中的" + k + "值无法通过数据校验，详情：不允许NULL！");
            }
        }
//        if(!v.getClass().getName().equals(tableModel.getKey(k).getType().getTypeName())){
//
//            throw exception(targetClass.getName() + "中的" + k + "值无法通过数据校验，详情：类型不符！");
//        }
        if(v instanceof  Integer ){
            // 这里无需关心 int ，传递进来时候 会 自动封装成Integer
            verification_int(k, (Integer) v);
        }
        if(v instanceof String){
            int  maxLength = tableModel.getKey(k).getLength();
            if(((String) v).length() > maxLength){
                throw exception(targetClass.getName() + "中的" + k + "值无法通过数据校验，详情：字符长度不可超过"+maxLength+"！");
            }
            verification_string(k, (String) v);
        }
        if(v instanceof Long){
            verification_long(k, (Long) v);
        }
        if(v instanceof Date){
            verification_date(k, (Date) v);
        }
        if(v instanceof Boolean){
            verification_boolean(k, (Boolean) v);
        }
    }

    public RuntimeException exception(String message){
        return new RuntimeException(message);
    }
}
