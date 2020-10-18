package com.veracitymc.coins.listeners;

import com.veracitymc.coins.game.player.VeracityProfile;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CoinListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        if (event.getEntity() instanceof Player) return;
        if (!(event.getEntity() instanceof Monster)) return;

        Player player = event.getEntity().getKiller();
        VeracityProfile profile = VeracityProfile.getByPlayer(player);
        profile.addPvEKills();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (!(event.getEntity().getKiller() instanceof Player)) return;

        Player player = event.getEntity().getKiller();
        VeracityProfile profile = VeracityProfile.getByPlayer(player);
        profile.addPvPKills();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        VeracityProfile profile = VeracityProfile.getByPlayer(player);

        profile.addMined();
    }
}
