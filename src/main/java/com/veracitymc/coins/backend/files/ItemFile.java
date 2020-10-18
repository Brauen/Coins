package com.veracitymc.coins.backend.files;

import com.veracitymc.coins.api.chat.C;
import com.veracitymc.coins.game.inventory.CoinsItem;
import com.veracitymc.coins.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;

import java.util.*;

public class ItemFile extends ConfigFile {

    @Getter private Map<Integer, CoinsItem> items = new HashMap<>();
    @Getter private String title;
    @Getter private boolean emptySlot;
    @Getter private String COINS_MESSAGE, COINS_MESSAGE_OTHER;

    public ItemFile() {
        super("items.yml");
    }

    public void init() {
        if(!config.contains("gui.items")) {
            config.createSection("gui.items");
            save();
            return;
        }

        COINS_MESSAGE = config.getString("coins-message");
        COINS_MESSAGE_OTHER = config.getString("coins-message-other");
        title = config.getString("gui.title");
        emptySlot = config.getBoolean("gui.empty-slot.enable");

        if (emptySlot) {
            CoinsItem empty = new CoinsItem(new ItemBuilder(Material.STAINED_GLASS_PANE).name("").lore(C.color(config.getStringList("gui.empty-slot.lore"))), 0, 0, Arrays.asList(""));
            items.put(0, empty);
        }

        for (String item : config.getConfigurationSection("gui.items").getKeys(false)) {
            int cost = config.getInt("gui.items." + item + ".cost");
            int slot = config.getInt("gui.items." + item + ".slot");
            ItemBuilder builder = new ItemBuilder(Material.matchMaterial(config.getString("gui.items." + item + ".material")))
                    .name(C.color(config.getString("gui.items." + item + ".name")))
                    .lore(C.color(config.getString("gui.items." + item + ".lore").replace("%COST%", String.valueOf(cost))));
            List<String> commands = config.getStringList("gui.items." + item + ".commands");

            CoinsItem cItem = new CoinsItem(builder, slot, cost, commands);
            items.put(slot, cItem);
        }
    }

    public CoinsItem getItem(int slot) {
        return items.get(slot);
    }

    public boolean isValidItem(int slot) {
        return getItem(slot) != null;
    }
}
