package com.think.web.core;

import com.think.common.util.rt.ThinkMachineUtil;
import com.think.common.util.rt.ThinkRuntimeUtil;
import com.think.common.util.rt.models.MemeryInfoModel;
import com.think.common.util.rt.models.RuntimeInfoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/6/30 13:02
 * @description : TODO
 */
@Slf4j
@Component
public class ThinkWebApplicationRuntimeInfo {
    private static ThinkWebApplicationRuntimeInfo info;
    public static final ThinkWebApplicationRuntimeInfo getServerRuntimeInfo(){
        return info;
    }

//    @LocalServerPort 这注解不靠谱，
    private int webAppPort;

    private String osType ;

    private String webAppName;

    private String webAppLocalHost;

    private String webAppMacAddr ;

    private RuntimeInfoModel runtimeInfo ;


    private ApplicationContext applicationContext;

    @Autowired
    public ThinkWebApplicationRuntimeInfo(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        try{
            init();
        }catch (Exception e){
            log.error("",e);
        }
        if(ThinkWebApplicationRuntimeInfo.info == null) {
            ThinkWebApplicationRuntimeInfo.info = this;
        }

    }

    private void init(){
        // application name
        this.webAppName = applicationContext.getApplicationName();
        // host name
        this.webAppLocalHost = ThinkMachineUtil.hostName();
        //
        this.webAppMacAddr =ThinkMachineUtil.macAddr();
    }


    public RuntimeInfoModel getRuntimeInfo(){
        return ThinkRuntimeUtil.currentRuntimeInfo();
    }











}
