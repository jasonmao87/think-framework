package com.think.common.util.rt;


import com.think.common.util.DateUtil;
import com.think.common.util.rt.models.MemeryInfoModel;
import com.think.common.util.rt.models.RuntimeInfoModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.*;
import java.util.Enumeration;

public class ThinkRuntimeUtil {


    public static final String runtimeName(){
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        return runtime.getName();
    }


    public static final RuntimeInfoModel currentRuntimeInfo(){
        RuntimeInfoModel modal = new RuntimeInfoModel()
                .setRecordTime(DateUtil.now());
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        modal.setMemoryHeapMemoryUsage( MemeryInfoModel.ofUsage(memoryMXBean.getHeapMemoryUsage()))
                .setMemoryNoHeapMemoryUsage(MemeryInfoModel.ofUsage(memoryMXBean.getNonHeapMemoryUsage()));

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        modal.setThreadDaemonCount(threadMXBean.getDaemonThreadCount())
                .setThreadTotalCount(threadMXBean.getThreadCount())
                .setThreadPeakCount(threadMXBean.getPeakThreadCount());


        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        modal.setSystemLoadAverage(operatingSystemMXBean.getSystemLoadAverage());

        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        modal.setClassLoadedCountCurrent(classLoadingMXBean.getLoadedClassCount())
                .setClassLoadedCountTotal(classLoadingMXBean.getTotalLoadedClassCount())
                .setClassUnloadCountTotal(classLoadingMXBean.getUnloadedClassCount());

        return modal;
    }


    public static boolean isWindowsOS(){
        boolean b = true;
        try {
            return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
        }catch (Exception e){}
        Enumeration enumeration = System.getProperties().propertyNames();
        while (enumeration.hasMoreElements()){
            String k =  enumeration.nextElement().toString();
            String v =  System.getProperties().get(k).toString();
            if(v.toUpperCase().contains("WINDOWS")){
                return true;
            }
        }
        return false;
    }
    public static boolean isLinuxOS(){
        try{
            return System.getProperties().getProperty("rt.name").toUpperCase().indexOf("LINUX") != -1;
        }catch (Exception e) {
            Enumeration enumeration = System.getProperties().propertyNames();
            while (enumeration.hasMoreElements()) {
                String k = enumeration.nextElement().toString();
                String v = System.getProperties().get(k).toString();
                if (v.toUpperCase().contains("LINUX")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMacOS(){
        if(isLinuxOS()==false && isWindowsOS() ==false) {
            return System.getProperties().getProperty("rt.name").toUpperCase().indexOf("LINUX") != -1;
        }
        return false;
    }


    /**
     * 执行命令
     * @param cmd
     * @param maxResultLine
     * @return
     */
    public static final String executeOSCommand(String[] cmd,int maxResultLine) {
        StringBuilder builder = new StringBuilder("");
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader (is);
            int index = 0 ;
            while ((line = br.readLine ()) != null) {
                index ++ ;
                if(index > maxResultLine){
                    break;
                }
                builder.append(line).append("\n");
//                result +=line;
//                result +="\n";

            }

            br.close();
            proc.destroy();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

//
//    public static void main(String[] args) {
//
//        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
//
//        System.out.println(operatingSystemMXBean.getName());
//        System.out.println(operatingSystemMXBean.getArch());
//        System.out.println(operatingSystemMXBean.getAvailableProcessors());
//        System.out.println(operatingSystemMXBean.getSystemLoadAverage());
//        System.out.println(operatingSystemMXBean.getVersion());
//        System.out.println(operatingSystemMXBean.getObjectName());
//
//        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//
//        try {
//            System.out.println(InetAddress.getLocalHost().getHostName());
//            System.out.println(InetAddress.getLocalHost().getCanonicalHostName());
//            System.out.println(InetAddress.getLoopbackAddress());
//            System.out.println(InetAddress.getLocalHost().toString());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//
//
//
//    }
}