package com.think.core.supports;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

@Slf4j
public class ClasspathPackageScaner {
    private String basePackage;
    private ClassLoader cl;

    /**
     * 初始化
     * @param basePackage
     */
    public ClasspathPackageScaner(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();
    }
    public ClasspathPackageScaner(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }
    /**
     *获取指定包下的所有字节码文件的全类名
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     *doScan函数
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String splashPath = dotToSplash(basePackage);
        URL url = cl.getResource(splashPath);
        String filePath = getRootPath(url);
        List<String> names = null;
        if (isJarFile(filePath)) {
            names = readFromJarFile(filePath, splashPath);
            return names;
        } else {
            names = readFromDirectory(filePath);
            for (String name : names) {
                if (isClassFile(name)) {
                    nameList.add(toFullyQualifiedName(name, basePackage));
                } else {
                    doScan(basePackage + "." + name, nameList);
                }
            }
        }
        return nameList;
    }

    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(trimExtension(shortName));
        return sb.toString();
    }

    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {

        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> nameList = new ArrayList<String>();
        while (null != entry) {
            String name = entry.getName();//.replaceAll("/",".");
            if (name.contains(splashedPackageName) && isClassFile(name)) {
                name  = name.replace(name.split("com")[0],"").replaceAll("/",".").replaceAll(".class" ,"");
                nameList.add(name);
            }
            entry = jarIn.getNextJarEntry();
        }
        return nameList;
    }

    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();
        if (null == names) {
            return null;
        }
        return Arrays.asList(names);
    }

    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }





    /**
     * "file:/home/whf/cn/fh" - "/home/whf/cn/fh"
     * "jar:file:/home/whf/foo.jar!cn/fh" - "/home/whf/foo.jar"
     */
    public static String getRootPath(URL url) {
        if(log.isDebugEnabled()){
            log.info("get ROOT path  ->   {}" ,url );

        }
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    /**
     * "com.think.core" - "com/think/core"
     * @param name
     * @return
     */
    public static String dotToSplash(String name) {
        String result =name.replaceAll("\\.", "/");
        if(log.isDebugEnabled()){
            log.debug("{}   dotToSplash  >>  {}" , name ,result);
        }
        return result;
    }

    /**
     * "Apple.class" - "Apple"
     */
    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            String result = name.substring(0,pos);
            if(log.isDebugEnabled()){
                log.debug("{}   trimExtension > {} ",name,result);
            }
            return result;
        }
        if(log.isDebugEnabled()){
            log.debug("{}   trimExtension  , not contains '.' ",name);
        }
        return name;
    }


    public static void main(String[] args) {


    }

}
