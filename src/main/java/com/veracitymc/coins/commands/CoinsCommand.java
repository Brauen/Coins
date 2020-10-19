package com.veracitymc.coins.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.veracitymc.coins.Coins;
import com.veracitymc.coins.api.chat.C;
import com.veracitymc.coins.game.player.VeracityProfile;
import com.veracitymc.coins.game.player.utils.PlayerUtil;
import com.veracitymc.coins.game.player.utils.UUIDPair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("coins|vcoins|veracitycoins")
@CommandPermission("veracity.coins")
public class CoinsCommand extends BaseCommand {

    @Default
    public void onDefault(Player player, @Optional VeracityProfile prof) {
        if (prof == null) {
            VeracityProfile profile = VeracityProfile.getByPlayer(player);
            player.sendMessage(C.color(Coins.getInstance().getItemFile().getCOINS_MESSAGE().replace("%AMOUNT%", String.valueOf(profile.getCoins()))));
        } else {

            player.sendMessage(C.color(Coins.getInstance().getItemFile().getCOINS_MESSAGE_OTHER().replace("%PLAYER%", prof.getName()).replace("%AMOUNT%", String.valueOf(prof.getCoins()))));
        }
    }

    @Syntax("<player>")
    public void onBalance(CommandSender sender, VeracityProfile profile) {
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player");
            return;
        }


    }

    @Subcommand("shop")
    public void onShop(Player player) {
        Coins.getInstance().getCoinsGUI().openMainInventory(player);
    }

    @Syntax("<player> <amount>")
    @CommandPermission("veracity.coins.admin")
    @Subcommand("set|s")
    public void setBalance(CommandSender sender, VeracityProfile profile, int amount) {
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player");
            return;
        }

        profile.setCoins(Math.abs(amount));
        sender.sendMessage(ChatColor.GOLD + "You have set " + ChatColor.RED + profile.getName() + ChatColor.GOLD + "'s coins to " + ChatColor.RED + String.valueOf(amount));
    }

    @Syntax("<player> <amount>")
    @CommandPermission("veracity.coins.admin")
    @Subcommand("add|a")
    public void addBalance(CommandSender sender, VeracityProfile profile, int amount) {
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player");
            return;
        }

        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "Amount must be at least 1");
            return;
        }

        profile.addCoins(Math.abs(amount));
        sender.sendMessage(ChatColor.GOLD + "You have added " + ChatColor.RED + String.valueOf(amount) + ChatColor.GOLD + " coins to " + ChatColor.RED + profile.getName());
    }

    @Syntax("<player> <amount>")
    @CommandPermission("veracity.coins.admin")
    @Subcommand("remove|r")
    public void removeBalance(CommandSender sender, VeracityProfile profile, int amount) {
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player");
            return;
        }

        if (amount < 1) {
            sender.sendMessage(ChatColor.RED + "Amount must be at least 1");
            return;
        }

        profile.removeCoins(Math.abs(amount));
        sender.sendMessage(ChatColor.GOLD + "You have removed " + ChatColor.RED + String.valueOf(amount) + ChatColor.GOLD + " coins from " + ChatColor.RED + profile.getName());
    }

    @HelpCommand
    @Subcommand("help")
    public void onHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "VeracityMC Coins Commands");
        sender.sendMessage(ChatColor.RED + "/coins [player] - View coin balance");
        sender.sendMessage(ChatColor.RED + "/coins set <player> <amount> - Set coin balance");
        sender.sendMessage(ChatColor.RED + "/coins add <player> <amount> - Add to coin balance");
        sender.sendMessage(ChatColor.RED + "/coins remove <player> <amount> - Remove from coin balance");
        sender.sendMessage(ChatColor.RED + "/coins shop - Open up Coins Shop");
    }
}
