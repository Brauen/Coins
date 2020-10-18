package com.veracitymc.coins.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.veracitymc.coins.game.player.VeracityProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("coins|vcoins|veracitycoins")
@CommandPermission("veracity.coins")
public class CoinsCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void onDefault(Player player) {
        VeracityProfile profile = VeracityProfile.getByPlayer(player);
        player.sendMessage(ChatColor.YELLOW + "Coins: " + ChatColor.GREEN + profile.getCoins());
    }

    @Syntax("<player>")
    public void onBalance(CommandSender sender, VeracityProfile profile) {
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Coins: " + ChatColor.GREEN + profile.getCoins());
    }

    @Subcommand("shop")
    public void onShop(Player player) {

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
