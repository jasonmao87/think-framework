package com.think.common.util.rt;

import com.think.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 服务器信息UTIL
 */
@Slf4j
public class ThinkMachineUtil {

    private static String hostname = null;

    private static String macAddr = null;


    /**
     * 获取当前的HOSTNAME
     * @return
     */
    public static final String hostName(){
        try {
            if (hostname == null) {
                InetAddress inetAddress = InetAddress.getLocalHost();
                hostname = inetAddress.getHostName();
            }
        }catch (Exception e){
            return "EX_"+ StringUtil.randomStr(16);
        }

        return hostname;
    }

    /**
     * 获取本机MAC地址
     * @return
     */
    public static final String macAddr() {
        if (macAddr == null) {
            try {
                String macStr =null;
                Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
                if(log.isDebugEnabled() ){
                    log.debug(ThinkRuntimeUtil.isWindowsOS() + " is windows ");
                }
                if(ThinkRuntimeUtil.isLinuxOS()) {
                    macStr= getMacHexStrOfLinuxOs(networkInterfaceEnumeration);
                }else if(ThinkRuntimeUtil.isWindowsOS()){
                    macStr = getMacHExStringOfUnknowOs(networkInterfaceEnumeration);
                }else{
                    macStr = getMacHExStringOfUnknowOs(networkInterfaceEnumeration);
                }
                if(macAddr == null){

                }
                macAddr =macStr.toUpperCase();
            } catch (Exception e) {
                if(log.isErrorEnabled()){
                    //log.error("Exception catched while getMacAddr : {}",e);
                }
                return StringUtil.uuid();
            }
        }
        return macAddr;
    }

    private static final String getHexFromNetworkInterface(NetworkInterface networkInterface ) throws SocketException {
        String macStr = null;
        byte[] mac = networkInterface.getHardwareAddress();
        if (mac == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder("");
        for (int i = 0; i < mac.length; i++) {
            if (i != 0) {
                stringBuilder.append("-");
            }
            /**
             * 转换mac的字节数组
             */
            int temp = mac[i] & 0xFF;
            String str = Integer.toHexString(temp);
            if (str.length() == 1) {
                stringBuilder.append("0" + str);
            } else {
                stringBuilder.append(str);
            }
            macStr = stringBuilder.toString();
        }
        return macStr;
    }

    private static String getMacHExStringOfUnknowOs(Enumeration<NetworkInterface> networkInterfaceEnumeration ) throws SocketException{
        String macStr = null;
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            macStr = getHexFromNetworkInterface(networkInterface);
            if(macStr !=null){
                return macStr;
            }
        }
        return null;
    }

    private static String getMacHexStrOfLinuxOs(Enumeration<NetworkInterface> networkInterfaceEnumeration ) throws SocketException{
        String macStr = null;
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            if (
                    (macStr == null || macStr.length() < 6)
                            &&
                            (networkInterface.isLoopback() ||
                                    networkInterface.getName().equalsIgnoreCase("eth0") ||
                                    networkInterface.getName().equalsIgnoreCase("eth1") ||
                                    networkInterface.getName().equalsIgnoreCase("en0") ||
                                    networkInterface.getName().equalsIgnoreCase("en1"))
             ) {
                macStr = getHexFromNetworkInterface(networkInterface);
                if (macStr != null) {
                    break;
                }
            }
        }
        return macStr;
    }
}
