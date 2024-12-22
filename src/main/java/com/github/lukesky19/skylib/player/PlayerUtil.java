package com.github.lukesky19.skylib.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skylib.version.VersionUtil;
import org.bukkit.Location;
import org.bukkit.World;
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
        if(VersionUtil.getMajorVersion() >= 21 && VersionUtil.getMinorVersion() < 3) {
            giveItem1_21(inventory, addStack, amount, location);
        } else {
            addStack.setAmount(amount);

            giveItem(inventory, addStack, location);
            // appropriate
        }
    }

    /**
     * Gives an item to a player and drops any items that can't be added on the ground.
     * This works for all versions except 1.21 and 1.21.1 due to a bug in the API that is fixed in 1.21.3 and newer.
     * @param inventory The Inventory to add the item to.
     * @param addStack The ItemStack to add.
     * @param location The location to drop items if the inventory is full.
     */
    private static void giveItem(Inventory inventory, ItemStack addStack, Location location) {
        // Try to add the item stack to the inventory
        HashMap<Integer, ItemStack> leftover = inventory.addItem(addStack);

        // If there are leftover items, we need to handle them
        if (!leftover.isEmpty()) {
            // Drop the leftover items on the ground
            for (ItemStack item : leftover.values()) {
                if (item.getAmount() > 0) {
                    dropItem(location.getWorld(), location, item);
                }
            }
        }
    }

    /**
     * Gives an item to a player.
     * This handles stack sizes because of a bug in the api for versions 1.21 and 1.21.1 with the new stack size component.
     * @param inventory The Inventory to add items to.
     * @param itemStack The ItemStack to add.
     * @param amount The amount of items to add.
     * @param location The location to drop items if the inventory is full.
     */
    private static void giveItem1_21(@NotNull Inventory inventory, @NotNull ItemStack itemStack, int amount, Location location) {
        int maxStackSize = itemStack.getMaxStackSize();
        int remainder = amount % maxStackSize;
        int stacks = (amount - remainder) / maxStackSize;

        if(stacks == 0 && remainder == 0) return;

        if(stacks >= 1) {
            final ItemStack addStack = itemStack.clone();
            addStack.setAmount(maxStackSize);

            for(int i = 1; i <= stacks; i++) {
                giveItem(inventory, addStack, location);
            }
        }

        if(remainder >= 1) {
            final ItemStack addStack = itemStack.clone();
            addStack.setAmount(remainder);

            for(int i = 1; i <= stacks; i++) {
                giveItem(inventory, addStack, location);
            }
        }
    }

    /**
     * Drops an item stack at the specified location.
     * @param world    The world where the item will be dropped.
     * @param location The location where the item will be dropped.
     * @param itemStack The item stack to drop.
     */
    private static void dropItem(World world, Location location, ItemStack itemStack) {
        if (world != null && location != null && itemStack != null && itemStack.getAmount() > 0) {
            Item item = world.dropItem(location, itemStack);
            item.setPickupDelay(0); // Optional: Set pickup delay to 0 so players can pick it up immediately
        }
    }
}
