package com.think.core.bean.util;

import com.think.common.util.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * class filter
 */
public class ClassUtil {

    protected final static void setValue(Field field, Object bean  ,Object v){
        try{
            field.setAccessible(true);
            if(field.getName().equals("serialVersionUID")){
                return;
            }
            /*
            if(field.getType().isEnum()){
                Object[] enumConstants = field.getType().getEnumConstants();


            }
            */

            if(v instanceof BigDecimal){
                String typeName = field.getType().getCanonicalName().toLowerCase();
                if(typeName.contains("double")){
                    field.set(bean,    ((BigDecimal) v) .doubleValue());
                }else if(typeName.contains("float")){
                    field.set(bean,    ((BigDecimal) v) .floatValue());
                }else{
                    field.set(bean, v);
                }

            }else {
                field.set(bean, v);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static boolean isChildTypeOfClass(Class _class ,Class type){
        while (!_class.getSuperclass().toString().contains("java.lang.Object")){
            _class = _class.getSuperclass();
            if(_class == type){
                return true;
            }
            return isChildTypeOfClass(_class,type);
        }
        return false;
    }

    /**
     * 获得 field
     * @param _class
     * @param fieldName
     * @return
     */
    public static Field getField(Class _class ,String fieldName){
        Field _field = null ;
        Field[] fields = _class.getDeclaredFields();
        Field.setAccessible(fields, true);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        if(_field == null && _class.getGenericSuperclass()!=null){
            _field = getField(_class.getSuperclass(), fieldName);
        }
        return _field;
    }

    /**
     * 获得指定 字段的值
     * @param bean
     * @param fieldName
     * @return
     */
    public static final Object getProperty(Object bean, String fieldName) {
        try{
            Object obj = getProperty(bean.getClass(),bean,fieldName);
            return obj;
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 获取指定字段的值
     * @param _class
     * @param bean
     * @param fieldName
     * @return
     */
    private static final Object getProperty(Class _class,Object bean,String fieldName){
        try {
            if(_class ==null){
                _class = bean.getClass();
            }
            return getField(_class, fieldName).get(bean);
        }catch (Exception e){
            return null;
        }

    }

    /**
     * 获取 Field list
     * @param _class
     * @return
     */
    public static List<Field> getFieldList(Class _class){
        List<Field> list = new ArrayList<Field>();
        for( Field field : _class.getDeclaredFields()){
            list.add(field);
        }
        while (!_class.getSuperclass().toString().contains("java.lang.Object")){
            _class = _class.getSuperclass() ;
            for( Field field : _class.getDeclaredFields()){
                boolean containsName = false;
                for(int i = 0 ; i < list.size() ; i++){
                    if(list.get(i).getName().equals(field.getName())){
                        containsName = true;
                        break;
                    }
                }
                if(!containsName){
                    list.add(field);
                }
            }
        }
        return list;
    }



    public static final List<Class> getClassesFromPackage(String packageName){
        //第一个class类的集合
        List<Class> classes = new ArrayList<Class>();
        //是否循环迭代
        boolean recursive = true;
        //获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        //定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            //循环迭代下去
            while (dirs.hasMoreElements()){
                //获取下一个元素
                URL url = dirs.nextElement();
                //得到协议的名称
                String protocol = url.getProtocol();
                //如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    //获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), StringUtil.UTF8);
                    //以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)){
                    //如果是jar包文件
                    //定义一个JarFile
                    JarFile jar;
                    try {
                        //获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        //从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        //同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            //如果是以/开头的
                            if (name.charAt(0) == '/') {
                                //获取后面的字符串
                                name = name.substring(1);
                            }
                            //如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                //如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    //获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                //如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive){
                                    //如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        //去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            //添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;

    }

    /**
     * 以文件的形式来获取包下的所有Class
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class> classes){
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : dirfiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            }
            else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    //添加到集合中去
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
