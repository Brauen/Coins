package com.veracitymc.coins.utils;

import com.veracitymc.coins.Coins;
import com.veracitymc.coins.api.chat.C;
import com.veracitymc.coins.game.player.VeracityProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class CommandUtils {

    public static <K,V extends Comparable<? super V>>  List<Map.Entry<K, V>> sortByValue(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        return sortedEntries;
    }

    public static void listLeaderboard(CommandSender sender, Integer pagenumber) {
        File d = new File(Coins.getInstance().getDataFolder() + File.separator + "profile.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(d);

        Map<String, Integer> leaderboard = new HashMap<>();

        for (String s : data.getConfigurationSection("profiles").getKeys(false)) {
            if (VeracityProfile.getProfiles().containsKey(s))  {
                leaderboard.put(VeracityProfile.getByUuid(UUID.fromString(s)).getName(), VeracityProfile.getByUuid(UUID.fromString(s)).getCoins());
                continue;
            }

            leaderboard.put(data.getString("profiles." + s + ".name"), data.getInt("profiles." + s + ".coins"));
        }

        List<Map.Entry<String, Integer>> sorted = sortByValue(leaderboard);

        int maxPage = leaderboard.size() / 10;

        if (leaderboard.size() % 10 > 0)
            maxPage++;

        int max = leaderboard.size() -1;

        int between_low = (pagenumber -1) * 10;
        int between_high = pagenumber * 10;

        while (between_low > max) {
            pagenumber--;
            between_low = between_low - 10;
            between_high = between_high - 10;
        }

        if (max >= between_low)
            if (between_high > max)
                between_high = max + 1;

        List<String> message = new ArrayList<>();
        int i = 0;

        while (between_low < between_high) {


            message.add("&6" + i+1 + ". " + sorted.get(i).getKey() + ": " + "&c" +sorted.get(i).getValue());

            i++;
            between_low++;
        }

        for (String string : message) {
            sender.sendMessage(C.color(string));
        }
        sender.sendMessage(ChatColor.GOLD + "Viewing page " + ChatColor.LIGHT_PURPLE + pagenumber + ChatColor.GOLD + " out of " + ChatColor.LIGHT_PURPLE + maxPage);

    }
}
