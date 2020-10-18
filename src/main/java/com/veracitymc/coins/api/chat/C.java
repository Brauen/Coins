package com.veracitymc.coins.api.chat;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class C {

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String strip(String input) {
        return ChatColor.stripColor(input);
    }

    public static List<String> color(List<String> input) {
        return input.stream()
                .map(C::color)
                .collect(Collectors.toList());
    }
}
