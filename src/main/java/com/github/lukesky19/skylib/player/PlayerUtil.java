package com.github.lukesky19.skylib.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skylib.version.VersionUtil;
import org.bukkit.Location;
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
     * If the inventory is full, all items will drop to the ground.
     * If there is existing stacks in the inventory, the items will be added to them.
     * Then the items will be added to empty slots.
     * Lastly, any remaining items that didn't fit will drop on the ground.
     * @param inventory The Inventory to add items to.
     * @param itemStack The ItemStack to add.
     * @param amount The amount of items to add.
     * @param location The location to drop items if the inventory is full.
     */
    private static void giveItem1_21(@NotNull Inventory inventory, @NotNull ItemStack itemStack, int amount, Location location) {
        // If the Inventory is full, just drop the items on the ground.
        if(!inventory.isEmpty()) {
            dropItem(location, itemStack);

            return;
        }

        // Add any items to existing stacks.
        for(int i = 0; i <= inventory.getSize() -1; i++) {
            ItemStack slotStack = inventory.getItem(i);

            if(slotStack != null && !slotStack.isEmpty() && slotStack.isSimilar(itemStack)) {
                int slotAmount = slotStack.getAmount();
                int slotMaxSize = slotStack.getMaxStackSize();

                if(slotAmount < slotMaxSize) {
                    int finalAmount = slotAmount + amount;
                    amount = finalAmount % slotMaxSize;

                    if(amount > 0) {
                        slotStack.setAmount(slotMaxSize);
                    } else {
                        slotStack.setAmount(finalAmount);

                        return;
                    }
                }
            }
        }

        // Add any leftover items to empty stacks.
        if(amount > 0) {
            for(int i = 0; i <= inventory.getSize() -1; i++) {
                ItemStack slotStack = inventory.getItem(i);

                if(slotStack != null && slotStack.isEmpty()) {
                    final ItemStack cloneStack = itemStack.clone();

                    int cloneStackMaxSize = cloneStack.getMaxStackSize();
                    int finalAmount = Math.min(amount, cloneStackMaxSize);

                    cloneStack.setAmount(finalAmount);
                    amount -= finalAmount;

                    inventory.setItem(i, cloneStack);

                    if(amount  == 0) return;
                }
            }
        }

        // Drop any leftover items on the ground.
        if(amount > 0) {
            final ItemStack cloneStack = itemStack.clone();
            cloneStack.setAmount(amount);

            dropItem(location, cloneStack);
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
