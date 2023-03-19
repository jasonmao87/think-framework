package com.think.mongo.config;

import lombok.Data;

@Data
public class MongoConfig {

    protected String host;
    protected int port;
    protected String username;
    protected String password;
    protected String database;


}
