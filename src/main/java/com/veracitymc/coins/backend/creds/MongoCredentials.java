package com.veracitymc.coins.backend.creds;

import lombok.Getter;

public class MongoCredentials {

    @Getter private final String hostname, username, password, database, authDb;
    @Getter private final int port;

    public MongoCredentials(String hostname, int port, String username, String password, String database, String authDb) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.authDb = authDb;
    }
}
