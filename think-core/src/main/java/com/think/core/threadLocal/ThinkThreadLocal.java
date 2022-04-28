package com.think.core.threadLocal;

import java.util.Date;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/4/25 20:15
 * @description :  think 支持的 本地线程变量封装工具 类 ，支持 过期
 */
public class ThinkThreadLocal {
    private final static ThreadLocal<ThreadLocalBean> local = new ThreadLocal<>();

    private static final boolean isExits() {
        return local.get() != null;
    }
    private static  final ThreadLocalBean getBean() {
        if (isExits()) {
            final ThreadLocalBean bean = local.get();
            if (bean.isExpire()) {
                local.remove();
                return null;
            }
            bean.active();
            return bean;
        }
        return null;
    }


    public static final void set(Object o ) {
        ThreadLocalBean bean =new ThreadLocalBean(o);
        local.set(bean);
    }

    public static Long getLong(){
        final ThreadLocalBean bean = getBean();
        if(bean!=null){
            return (Long) bean.getValue();
        }
        return null;
    }

    public static boolean isTypeOf(Class tclass){
        final Object o = get();
        if(o == null){
            return false;
        }
        if(o.getClass() == tclass){
            return true;
        }
        return false;
    }

    public static Integer getInt(){
        if (isTypeOf(Integer.class)) {
            return (Integer) get();
        }
        return null;
    }

    public static String getString(){
        if (isTypeOf(String.class)) {
            return (String) get();
        }
        return null;
    }

    public static Date getDate(){
        if (isTypeOf(Date.class)) {
            return (Date) get();
        }
        return null;
    }

    public static final Object get(){
        final ThreadLocalBean bean = getBean();
        if(bean!=null){
            return bean.getValue();
        }
        return null;
    }


    public static final void remove(){
        local.remove();
    }


}
