package com.think.mongo.config;

import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import lombok.Data;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Data
public class MongoConfig {

    protected String host;
    protected int port;
    protected String username;
    protected String password;
    protected String database;


}
