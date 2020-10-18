package com.veracitymc.coins.utils;

import com.veracitymc.coins.Coins;
import org.bukkit.Bukkit;

public class TaskUtils {

    public static void runSync(Runnable runnable) {
        if(Bukkit.isPrimaryThread())
            runnable.run();
        else
            Bukkit.getScheduler().runTask(Coins.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        if(Bukkit.isPrimaryThread())
            Bukkit.getScheduler().runTaskAsynchronously(Coins.getInstance(), runnable);
        else
            runnable.run();
    }
}
