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
     * A method to give player items on Minecraft versions 1.21 and 1.21.1 due to a bug in the API with stack sizes.
     * Will attempt to add any items to existing ItemStacks of similar items, then fill empty slots, then drop the rest on the ground.
     * @param inventory The Inventory to add items to.
     * @param itemStack The ItemStack to add.
     * @param amount The amount of items to add.
     * @param location The location to drop items if the inventory is full.
     */
    private static void giveItem1_21(@NotNull Inventory inventory, @NotNull ItemStack itemStack, int amount, Location location) {
        // Loop through the player's inventory
        for(int i = 0; i <= (inventory.getSize() - 1); i++) {
            // Ignore armor slots
            if(i >= 36 && i <= 39) continue;

            // Get the ItemStack for this slot
            final ItemStack invItem = inventory.getItem(i);

            // Check if it is not null and the type is not air
            if(invItem != null && !invItem.getType().equals(Material.AIR)) {
                // Get the max stack size of the inventory item
                int invItemMaxSize = invItem.getMaxStackSize();

                // If the item to give is similar to the inventory item, attempt to add to the existing stack.
                if(itemStack.isSimilar(invItem)) {
                    // Check if the inventory item is less than it's max stack size
                    if(invItem.getAmount() < invItemMaxSize) {
                        // Get the result of adding the invItem amount and the amount to give.
                        final int result = invItem.getAmount() + amount;

                        // If the result is less than or equal to the items max stack size, set the inventory items amount to the result.
                        if(result <= invItemMaxSize) {
                            invItem.setAmount(result);

                            // Since the result fit inside the existing item stack, we can return as there would be no more left to add.
                            return;
                        } else {
                            // Get the leftover amount that cannot fit inside the invItem stack
                            final int leftover = result - invItemMaxSize;
                            // Get the amount that fit inside the invItem stack
                            final int transferred = amount - leftover;
                            // Subtract the amount transferred from the total amount we are adding
                            amount -= transferred;

                            // Set the invItem's amount to it's max stack size.
                            invItem.setAmount(invItemMaxSize);

                            // If the total amount to add is less than or equal to 0, return
                            if (amount <= 0) return;
                        }
                    }
                }
            } else {
                // Get the itemStack's max size that we are transferring.
                int maxSize = itemStack.getMaxStackSize();
                // Clone the itemStack
                final ItemStack copyItem = itemStack.clone();

                // Check if the total amount is greater than or equal to the itemStack's max size
                if(amount >= maxSize) {
                    // Set the cloned ItemStack to it's max stack size
                    copyItem.setAmount(maxSize);

                    // Put the cloned ItemStack at the given slot
                    inventory.setItem(i, copyItem);

                    // Subtract the max stack size (the amount added) from the total amount
                    amount -= maxSize;

                    // If the total amount is less than or equal to 0, return
                    if(amount <= 0) return;
                } else {
                    // Set the cloned ItemStack to the total amount
                    copyItem.setAmount(amount);

                    // Put the cloned ItemStack at the given slot
                    inventory.setItem(i, copyItem);

                    // The total amount fit inside the new stack we added so we can return
                    return;
                }
            }
        }

        if(amount > 0) {
            ItemStack copyItem = itemStack.clone();
            copyItem.setAmount(amount);

            dropItem(location, copyItem);
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
