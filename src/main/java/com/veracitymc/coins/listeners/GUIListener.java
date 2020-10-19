package com.veracitymc.coins.listeners;

import com.veracitymc.coins.Coins;
import com.veracitymc.coins.api.chat.C;
import com.veracitymc.coins.game.inventory.CoinsItem;
import com.veracitymc.coins.game.player.VeracityProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        if (!(event.getClickedInventory().getTitle()).equals(C.color(Coins.getInstance().getItemFile().getTitle()))) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        CoinsItem item = Coins.getInstance().getItemFile().getItem(event.getSlot());
        if (item == null || item.getSlot() == 0) return;

        VeracityProfile profile = VeracityProfile.getByPlayer(player);
        if (profile.getCoins() < item.getCost()) {
            player.sendMessage(ChatColor.RED + "Invalid Funds!");
            return;
        }

        profile.removeCoins(item.getCost());
        player.sendMessage(ChatColor.GREEN + "Purchase successful!");

        player.closeInventory();

        if (item.getCommand() == null || item.getCommand().size() == 0) return;

        for (String command : item.getCommand()) {
            System.out.println(command.replace("%PLAYER%", player.getName()));
            System.out.println(Bukkit.getServer().getConsoleSender());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%PLAYER%", player.getName()));
        }
    }
}
