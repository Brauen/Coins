package com.veracitymc.coins.backend.creds;

import lombok.Getter;

public class RedisCredentials {

    @Getter private final String host, password;
    @Getter private final int port;

    public RedisCredentials(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public boolean password() {
        return this.getPassword().length() > 0;
    }
}
