package com.veracitymc.coins.game.inventory;

import com.veracitymc.coins.utils.ItemBuilder;
import lombok.Getter;

import java.util.List;

@Getter
public class CoinsItem {

    private List<String> command;
    private ItemBuilder itemBuilder;
    private Integer slot, cost;

    public CoinsItem(ItemBuilder builder, Integer slot, Integer cost, List<String> commands) {
        this.itemBuilder = builder;
        this.slot = slot;
        this.cost = cost;
        this.command = commands;
    }

}