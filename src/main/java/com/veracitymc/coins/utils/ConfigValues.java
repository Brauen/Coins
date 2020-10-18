package com.veracitymc.coins.utils;

import com.veracitymc.coins.Coins;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigValues {

    public static final Integer PVP_KILLS, PVP_KILLS_COINS, PVE_KILLS, PVE_KILLS_COINS, MINING_TIMES, MINING_COINS, ONLINE_TIME, ONLINE_TIME_COINS;

    static {
        FileConfiguration config = Coins.getInstance().getConfig();

        PVP_KILLS = config.getInt("activities.kills.pvp.times");
        PVP_KILLS_COINS = config.getInt("activities.kills.pvp.coins");

        PVE_KILLS = config.getInt("activities.kills.pve.times");
        PVE_KILLS_COINS = config.getInt("activities.kills.pve.coins");

        MINING_TIMES = config.getInt("activities.mining.times");
        MINING_COINS = config.getInt("activities.mining.coins");

        ONLINE_TIME = config.getInt("activities.online.time");
        ONLINE_TIME_COINS = config.getInt("activities.online.coins");
    }
}
