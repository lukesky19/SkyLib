package com.github.lukesky19.skylib.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A class containing utilities for running actions on the Player.
 */
public class PlayerUtil {
    /**
     * Attempts to add an ItemStack to the player's inventory and if it can't, it will instead drop it at the player's feet.
     * @param player A Bukkit Player
     * @param itemStack A Bukkit ItemStack
     * @param amount The amount of items to give
     */
    public static void giveItem(@NotNull Player player, @NotNull ItemStack itemStack, @NotNull int amount) {
        if(amount > itemStack.getMaxStackSize()) {
            int count = amount / itemStack.getMaxStackSize();
            itemStack.setAmount(itemStack.getMaxStackSize());

            for(int i = 1; i <= count; i++) {
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(itemStack);
                for(Map.Entry<Integer, ItemStack> leftoverEntry : leftover.entrySet()) {
                    ItemStack leftoverStack = leftoverEntry.getValue();
                    player.getWorld().dropItem(player.getLocation(), leftoverStack);
                }
            }
        } else {
            itemStack.setAmount(amount);
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(itemStack);
            for(Map.Entry<Integer, ItemStack> leftoverEntry : leftover.entrySet()) {
                ItemStack leftoverStack = leftoverEntry.getValue();
                player.getWorld().dropItem(player.getLocation(), leftoverStack);
            }
        }
    }
}
