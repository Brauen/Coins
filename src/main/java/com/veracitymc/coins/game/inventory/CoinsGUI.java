package com.veracitymc.coins.game.inventory;

import com.veracitymc.coins.Coins;
import com.veracitymc.coins.api.chat.C;
import com.veracitymc.coins.game.player.VeracityProfile;
import com.veracitymc.coins.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class CoinsGUI {

    private Inventory menu;

    public void loadInventorys() {
        this.menu = loadMenu();
    }

    public void openMainInventory(Player player) {
        VeracityProfile profile = VeracityProfile.getByPlayer(player);
        Inventory toOpen = menu;

        List<String> beforeReplace = Coins.getInstance().getItemFile().getEmptyLore();
        List<String> lore = new ArrayList<>();

        for (String string : beforeReplace) {
            lore.add(string.replace("%AMOUNT%", String.valueOf(profile.getCoins())));
        }

        if (Coins.getInstance().getItemFile().isEmptySlot()) {

            for (int i = 0; i < 36; i++) {
                if (toOpen.getItem(i) == null) {
                    toOpen.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7)
                            .name(" ")
                            .lore(lore)
                            .get());
                }
            }
        }

        player.openInventory(toOpen);
    }

    private Inventory loadMenu() {
        Inventory inv = Bukkit.createInventory(null, 36, C.color(Coins.getInstance().getItemFile().getTitle()));

        for (CoinsItem item : Coins.getInstance().getItemFile().getItems().values()) {
            inv.setItem(item.getSlot(), item.getItemBuilder().get());
        }

        return inv;
    }
}
