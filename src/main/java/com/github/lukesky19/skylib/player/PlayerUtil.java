/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (c) 2024 lukeskywlker19

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package com.github.lukesky19.skylib.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skylib.version.VersionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
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
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public PlayerUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

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
     * Gives an item to a player and drops any items that can't be added on the ground.
     * @param inventory The Inventory to add the ItemStack to.
     * @param addStack The ItemStack to add.
     * @param amount The amount of items to add.
     * @param location The location to drop any items that don't fit inside the inventory.
     */
    public static void giveItem(Inventory inventory, ItemStack addStack, int amount, Location location) {
        addStack.setAmount(amount);

        giveItem(inventory, addStack, location);
    }

    /**
     * Gives an item to a player and drops any items that can't be added on the ground.
     * @param inventory The Inventory to add the item to.
     * @param addStack The ItemStack to add.
     * @param location The location to drop items if the inventory is full.
     */
    private static void giveItem(Inventory inventory, ItemStack addStack, Location location) {
        HashMap<Integer, ItemStack> leftover = inventory.addItem(addStack);

        if (!leftover.isEmpty()) {
            for (ItemStack item : leftover.values()) {
                if (item.getAmount() > 0) {
                    dropItem(location, item);
                }
            }
        }
    }

    /**
     * Drops an item stack at the specified location.
     * @param location The location where the item will be dropped.
     * @param itemStack The item stack to drop.
     */
    private static void dropItem(@NotNull Location location, @NotNull ItemStack itemStack) {
        Item item = location.getWorld().dropItem(location, itemStack);
        item.setPickupDelay(0);
    }
}
