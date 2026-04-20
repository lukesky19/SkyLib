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
package com.github.lukesky19.skylib.paper.api.player;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skylib.paper.SkyLibPaper;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A class containing utilities for running actions on the Player.
 */
public class PlayerUtil {
    private static @Nullable Server server;

    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public PlayerUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Initialize the {@link SkyLibPaper}
     * @param skyLib A {@link SkyLibPaper} instance.
     */
    public static void init(@NonNull SkyLibPaper skyLib) {
        PlayerUtil.server = skyLib.getServer();
    }

    /**
     * For backwards compatibility purposes and calls {@link #getPlayerProfile(UUID)}.
     * @param playerId The {@link UUID} to retrieve the {@link PlayerProfile} for.
     * @return A {@link PlayerProfile}.
     * @deprecated The use of {@link #getPlayerProfile(UUID)} is preferred as that is the new method and what this method also calls.
     */
    @Deprecated(since = "1.5.0.0")
    public static @NonNull PlayerProfile getCachedPlayerProfile(@NonNull UUID playerId) {
        return getPlayerProfile(playerId);
    }

    /**
     * Get the {@link PlayerProfile} from the cache only for the given {@link UUID}. See {@link #getOrCreatePlayerProfile(UUID)} as well.
     * @apiNote The {@link PlayerProfile} may not be complete. Check {@link PlayerProfile#isComplete()}.
     * @param playerId The {@link UUID} to retrieve the {@link PlayerProfile} for.
     * @return A {@link PlayerProfile}.
     */
    public static @NonNull PlayerProfile getPlayerProfile(@NonNull UUID playerId) {
        if(server == null) throw new RuntimeException("PlayerUtil not initialized properly.");

        PlayerProfile playerProfile = server.createProfile(playerId);
        playerProfile.completeFromCache();

        return playerProfile;
    }

    /**
     * Get the {@link PlayerProfile} from the cache or create a new {@link PlayerProfile} and attempt to complete it for the given {@link UUID}.
     * @apiNote The {@link PlayerProfile} may not be complete. Check {@link PlayerProfile#isComplete()}.
     * @param playerId The {@link UUID} to retrieve the {@link PlayerProfile} for.
     * @return A {@link PlayerProfile} or null.
     */
    @SuppressWarnings("unused") // This method is used by other plugins.
    public static @NonNull CompletableFuture<@NonNull PlayerProfile> getOrCreatePlayerProfile(@NonNull UUID playerId) {
        if(server == null) throw new RuntimeException("PlayerUtil not initialized properly.");

        PlayerProfile playerProfile = server.createProfile(playerId);
        boolean result = playerProfile.completeFromCache();

        if(result) return CompletableFuture.completedFuture(playerProfile);

        return CompletableFuture.supplyAsync(() -> {
            playerProfile.complete();
            return playerProfile;
        });
    }

    /**
     * Gives an item to a player and drops any items that can't be added on the ground.
     * @param inventory The Inventory to add the ItemStack to.
     * @param addStack The ItemStack to add.
     * @param amount The amount of items to add.
     * @param location The location to drop any items that don't fit inside the inventory.
     */
    @SuppressWarnings("unused") // This method is used by other plugins.
    public static void giveItem(@NonNull Inventory inventory, @NonNull ItemStack addStack, int amount, @NonNull Location location) {
        addStack.setAmount(amount);

        giveItem(inventory, addStack, location);
    }

    /**
     * Gives an item to a player and drops any items that can't be added on the ground.
     * @param inventory The Inventory to add the item to.
     * @param addStack The ItemStack to add.
     * @param location The location to drop items if the inventory is full.
     */
    public static void giveItem(@NonNull Inventory inventory, @NonNull ItemStack addStack, @NonNull Location location) {
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
    private static void dropItem(@NonNull Location location, @NonNull ItemStack itemStack) {
        Item item = location.getWorld().dropItem(location, itemStack);
        item.setPickupDelay(0);
    }
}