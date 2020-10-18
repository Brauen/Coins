package com.veracitymc.coins.backend;

import com.veracitymc.coins.Coins;
import lombok.Getter;
import lombok.Setter;

public abstract class VeracityBackend implements IBackend {

    @Getter private final BackendType type;

    @Getter @Setter private boolean loaded;

    public VeracityBackend(BackendType type) {
        this.type = type;
    }

    protected void logInfoMessage(String message) {
        Coins.getInstance().getLogger().info("(Backend) {" + this.getType().getVerboseName() + "} - " + message);
    }

    protected void logException(String message, Exception e) {
        Coins.getInstance().getLogger().severe("(Backend) {" + this.getType().getVerboseName() + "} - " + message);
        Coins.getInstance().getLogger().severe("-------------------------------------------");
        e.printStackTrace();
        Coins.getInstance().getLogger().severe("-------------------------------------------");
    }
}
