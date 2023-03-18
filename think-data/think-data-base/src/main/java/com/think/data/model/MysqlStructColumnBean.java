package com.think.data.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/8/25 17:24
 * @description :
 */
@Data
public class MysqlStructColumnBean implements Serializable {

    public MysqlStructColumnBean(Map<String,Object> source) {
        this.field = (String) source.getOrDefault("Field" ,"");
        this.type = (String) source.getOrDefault("Type","");
        this.allowNull = (String) source.getOrDefault("Null","");
        this.key = (String) source.getOrDefault("Key","");
        this.defaultValue = (String) source.getOrDefault("Default","");
        this.extra = (String) source.getOrDefault("Extra","");
    }

    private static final long serialVersionUID = 8913514620961870658L;
    private String field ;
    private String type ;
    private String allowNull;
    private String key ;
    private String defaultValue;
    private String extra;
}
