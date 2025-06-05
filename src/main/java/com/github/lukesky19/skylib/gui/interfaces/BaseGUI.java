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
package com.github.lukesky19.skylib.gui.interfaces;

import com.github.lukesky19.skylib.gui.abstracts.ChestGUI;
import com.github.lukesky19.skylib.gui.abstracts.MerchantGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * This class is used to create a base to build other Gui interfaces off of.
 * You should not use this directly unless creating your own Gui interfaces.
 * See {@link ChestGUI} and {@link MerchantGUI} for implementations to use.
 */
public interface BaseGUI {
    /**
     * Get the InventoryView associated with this GUI.
     * @return A Bukkit InventoryView.
     */
    @NotNull
    InventoryView getInventoryView();

    /**
     * Sets the InventoryView associated with this GUI.
     * @param view An InventoryView.
     */
    void setInventoryView(@NotNull InventoryView view);

    /**
     * Opens this GUI's inventory for the player.
     * @param plugin The plugin opening the Inventory.
     * @param player The Player to open the GUI for.
     */
    default void open(@NotNull Plugin plugin, @NotNull Player player) {
        if(getInventoryView().getPlayer().getUniqueId().equals(player.getUniqueId())) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    player.openInventory(getInventoryView()), 1L);
        }
    }

    /**
     * Updates the contents of the Inventory or InventoryView.
     * @return Returns a {@link CompletableFuture} of type {@link Void} when the update has completed.
     */
    CompletableFuture<Void> update();

    /**
     * Similar to {@link #update()} but can be used to refresh the GUI instead of completely update it.
     * @return Returns a {@link CompletableFuture} of type {@link Void} when the update has completed.
     */
    CompletableFuture<Void> refresh();

    /**
     * Closes the GUI's Inventory.
     * @param plugin The plugin closing the GUI.
     * @param player The player to close the GUI for.
     */
    default void close(@NotNull Plugin plugin, @NotNull Player player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.closeInventory(), 1L);
    }

    /**
     * Similar to {@link #close(Plugin, Player)}, but can be used to run different code when a GUI is closed vs unloaded (i.e., on reload).
     * @param plugin The plugin unloading the GUI.
     * @param player The player to close the GUI for.
     * @param onDisable Whether the unload is happening while the plugin is being disabled or not.
     */
    void unload(@NotNull Plugin plugin, @NotNull Player player, boolean onDisable);

    /**
     * Default handling of when any item is clicked inside an inventory.
     * You should override this if you want to do something with the event (i.e., return items the player input).
     * @param inventoryCloseEvent An InventoryCloseEvent
     */
    default void handleClose(@NotNull InventoryCloseEvent inventoryCloseEvent) {}

    /**
     * Default handling of when any item is dragged across the top part of an inventory.
     * You should override this if you want to do something with the event (i.e., cancel it).
     * @param inventoryDragEvent An InventoryDragEvent.
     */
    default void handleTopDrag(@NotNull InventoryDragEvent inventoryDragEvent) {}

    /**
     * Default handling of when any item is dragged across the bottom part of an inventory.
     * You should override this if you want to do something with the event (i.e., cancel it).
     * @param inventoryDragEvent An InventoryDragEvent.
     */
    default void handleBottomDrag(@NotNull InventoryDragEvent inventoryDragEvent) {}

    /**
     * Default handling of when any item is dragged across any part of an inventory.
     * You should override this if you want to do something with the event (i.e., cancel it).
     * @param inventoryDragEvent An InventoryDragEvent
     */
    default void handleGlobalDrag(@NotNull InventoryDragEvent inventoryDragEvent) {}

    /**
     * Handles of when any item is clicked inside a top-part of an inventory.
     * You should override this if you want to do something with the event (i.e., cancel it).
     * @param inventoryClickEvent An InventoryClickEvent.
     */
    default void handleTopClick(@NotNull InventoryClickEvent inventoryClickEvent) {}

    /**
     * Handles of when any item is clicked inside a bottom-part of an inventory.
     * You should override this if you want to do something with the event (i.e., cancel it).
     * @param inventoryClickEvent An InventoryClickEvent
     */
    default void handleBottomClick(@NotNull InventoryClickEvent inventoryClickEvent) {}

    /**
     * Handles of when any item is clicked inside any part of an inventory.
     * You should override this if you want to do something with the event (i.e., cancel it).
     * @param inventoryClickEvent An InventoryClickEvent
     */
    default void handleGlobalClick(@NotNull InventoryClickEvent inventoryClickEvent) {}
}
