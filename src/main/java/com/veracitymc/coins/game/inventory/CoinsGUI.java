package com.veracitymc.coins.game.inventory;

import com.veracitymc.coins.Coins;
import com.veracitymc.coins.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CoinsGUI {

    private Inventory menu;

    public void loadInventorys() {
        this.menu = loadMenu();
    }

    public void openMainInventory(Player player) {
        player.openInventory(menu);
    }

    private Inventory loadMenu() {
        Inventory inv = Bukkit.createInventory(null, 36, Coins.getInstance().getItemFile().getTitle());

        for (CoinsItem item : Coins.getInstance().getItemFile().getItems().values()) {
            inv.setItem(item.getSlot(), item.getItemBuilder().get());
        }

        for (int i = 0; i < 36; i++) {
            if (inv.getItem(i) == null) {
                if (i == 10 || i == 16 || i == 19 || i == 23) {
                    inv.setItem(i, Coins.getInstance().getItemFile().getItems().get(0).getItemBuilder().durability(15).get());
                } else {
                    inv.setItem(i, Coins.getInstance().getItemFile().getItems().get(0).getItemBuilder().durability(7).get());
                }
                inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS).durability(15).name("").get());
            }
        }

        return inv;
    }
}
