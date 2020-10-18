package com.veracitymc.coins.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null)
            return;
        if (!event.getClickedInventory().getTitle().contains("VeracityMC Coins Shop")) return;
    }
}
