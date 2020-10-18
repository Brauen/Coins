package com.veracitymc.coins.utils;

import com.veracitymc.coins.api.chat.C;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack is;

    public ItemBuilder(ItemStack is) {
        this.is = is.clone();
    }

    public ItemBuilder(Material material) {
        this.is = new ItemStack(material, 1);
    }

    public ItemBuilder type(Material material) {
        this.is.setType(material);

        return this;
    }

    public ItemBuilder data(int data) {
        this.is.setData(new MaterialData(this.is.getType(), (byte) data));

        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = this.is.getItemMeta();

        meta.setDisplayName(C.color(name));
        this.is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(String lore) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> loreList = meta.getLore();

        if(loreList == null)
            loreList = new ArrayList<>();

        loreList.add(C.color(lore));
        meta.setLore(loreList);
        this.is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(String... lore) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> loreList = meta.getLore();

        if(loreList == null)
            loreList = new ArrayList<>();

        Arrays.stream(lore).map(C::color).forEach(loreList::add);
        meta.setLore(loreList);
        this.is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = this.is.getItemMeta();
        List<String> loreList = meta.getLore();

        if(loreList == null)
            loreList = new ArrayList<>();

        lore.stream().map(C::color).forEach(loreList::add);
        meta.setLore(loreList);
        this.is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder durability(int durability) {
        this.is.setDurability((short) durability);
        return this;
    }

    public ItemBuilder owner(String owner) {
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwner(owner);
        this.is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder amount(int amount) {
        this.is.setAmount(amount);

        return this;
    }

    public ItemStack get() {
        return this.is;
    }
}
