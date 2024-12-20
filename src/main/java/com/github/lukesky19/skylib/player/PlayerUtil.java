package com.github.lukesky19.skylib.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckForNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class containing utilities for running actions on the Player.
 */
public class PlayerUtil {
    private static final Map<UUID, PlayerProfile> profileCache = new HashMap<>();

    /**
     * Caches a PlayerProfile.
     * @param uuid The UUID the PlayerProfile belongs to.
     * @param playerProfile The PlayerProfile to cache.
     */
    public static void cachePlayerProfile(@NotNull UUID uuid, @NotNull PlayerProfile playerProfile) {
        profileCache.put(uuid, playerProfile);
    }

    /**
     * Removes a PlayerProfile from the cache.
     * @param uuid The UUID the PlayerProfile belongs to.
     */
    public static void removeCachedPlayerProfile(UUID uuid) {
        profileCache.remove(uuid);
    }

    /**
     * Removes all PlayerProfiles from the cache.
     */
    public static void clearCachedPlayerProfiles() {
        profileCache.clear();
    }

    /**
     * Gets the PlayerProfile from the cache or null if there is no PlayerProfile cached for the given UUID.
     * @param uuid The UUID to retrieve the PlayerProfile for.
     * @return A PlayerProfile or null.
     */
    @CheckForNull
    public static PlayerProfile getCachedPlayerProfile(UUID uuid) {
        return profileCache.get(uuid);
    }

    /**
     * Attempts to add an ItemStack to the player's inventory and if it can't, it will instead drop it at the player's feet.
     * @param player A Bukkit Player
     * @param itemStack A Bukkit ItemStack
     * @param amount The amount of items to give
     */
    public static void giveItem(@NotNull Player player, @NotNull ItemStack itemStack, int amount) {
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
