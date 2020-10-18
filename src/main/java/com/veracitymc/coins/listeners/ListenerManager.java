package com.veracitymc.coins.listeners;

import com.veracitymc.coins.Coins;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class ListenerManager {

    @Getter private PluginManager pluginManager;

    public ListenerManager() {
        this.pluginManager = Bukkit.getServer().getPluginManager();
    }

    public void registerListeners() {
        registerListener(new CoinListener());
        registerListener(new GUIListener());
        registerListener(new PlayerListener());
    }

    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, Coins.getInstance());
    }
}
