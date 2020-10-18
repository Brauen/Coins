package com.veracitymc.coins.game.player.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UUIDPair {

    private UUID key;
    private String value;

    public UUIDPair(UUID key, String value) {
        this.key = key;
        this.value = value;

        PlayerUtil.getPairs().put(value.toLowerCase(), this);
    }
}
