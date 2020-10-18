package com.veracitymc.coins.listeners;

import com.veracitymc.coins.game.player.VeracityProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        VeracityProfile profile = VeracityProfile.getByPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onKick(PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    private void handleDisconnect(Player player) {
        VeracityProfile profile = VeracityProfile.getByPlayer(player);

        profile.save();
        VeracityProfile.getProfiles().remove(profile.getUuid().toString());
    }
}
