package com.mapscanner.mapscanner.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

// mongodb 连接数据库工具类
public class MongoDBUtill {
    private MongoDBUtill(){ }

    /**
     * 不通过认证进行连接
     * @param host 服务器主机地址
     * @param port 端口号
     * @param dbName 数据库名称
     * @return 返回连接数据库
     */
    public static MongoDatabase getConnection(String host, int port, String dbName){
        MongoClient mongoClient = new MongoClient(host, port);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
        return mongoDatabase;
    }

    /**
     * 通过认证进行连接
     * @param host 服务器主机地址
     * @param port 端口号
     * @param dbName 数据库名称
     * @param username 用户名
     * @param pwd 密码
     * @return 返回目标数据库
     */
    public static MongoDatabase getConnection(String host, int port, String dbName, String username, String pwd){
        List<ServerAddress> adds = new ArrayList<>();
        ServerAddress serverAddress = new ServerAddress(host, port);
        adds.add(serverAddress);

        List<MongoCredential> credentials = new ArrayList<>();
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(username, "admin", pwd.toCharArray());
        credentials.add(mongoCredential);

        MongoClient mongoClient = new MongoClient(adds, credentials);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
        return mongoDatabase;
    }
}
