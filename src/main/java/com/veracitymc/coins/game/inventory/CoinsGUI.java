package com.veracitymc.coins.game.inventory;

import com.veracitymc.coins.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CoinsGUI {

    private Inventory menu, gkits, items;

    public void loadInventorys() {
        this.menu = loadMenu();
      //  gkits = loadGKits();
     //   items = loadItems();
    }

    public void openMainInventory(Player player) {
        player.openInventory(menu);
    }

    private Inventory loadMenu() {
        Inventory inv = Bukkit.createInventory(null, 27, "VeracityMC Coins Shop");

        // gkits
        // items

        for (int i = 0; i < 27; i++) {
            if (inv.getItem(i) == null)
                inv.setItem(i, new ItemBuilder(Material.STAINED_GLASS).durability(15).name("").get());
        }

        return inv;
    }
}
