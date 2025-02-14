package com.github.lukesky19.skylib.gui.interfaces;

import com.github.lukesky19.skylib.gui.abstracts.ChestGUI;
import com.github.lukesky19.skylib.gui.abstracts.MerchantGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckForNull;

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
    @CheckForNull
    InventoryView getInventoryView();

    /**
     * Get the Inventory associated with this GUI.
     * @return A Bukkit Inventory.
     */
    @NotNull
    Inventory getInventory();

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

    /**
     * Default handling of when any item is clicked inside an inventory.
     * You should override this if you want to do something with the event (i.e., return items the player input).
     * @param inventoryCloseEvent An InventoryCloseEvent
     */
    default void handleClose(@NotNull InventoryCloseEvent inventoryCloseEvent) {}

    /**
     * Opens this GUI's inventory for the player.
     * @param plugin The plugin opening the Inventory.
     * @param player The Player to open the GUI for.
     */
    default void openInventory(@NotNull Plugin plugin, @NotNull Player player) {
        if(getInventoryView() != null) {
            if(getInventoryView().getPlayer().getUniqueId().equals(player.getUniqueId())) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                        player.openInventory(getInventoryView()), 1L);
            } else {
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                        player.openInventory(getInventory()), 1L);
            }
        } else {
            plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    player.openInventory(getInventory()), 1L);
        }
    }

    /**
     * Closes this GUI's inventory for the player.
     * @param plugin The plugin closing the Inventory.
     * @param player The Player to close the GUI for.
     */
    default void closeInventory(@NotNull Plugin plugin, @NotNull Player player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.closeInventory(), 1L);
    }

    /**
     * Similar to {@link #update()} but can be used to refresh the GUI instead of completely update it.
     */
    void refresh();

    /**
     * Updates the contents of the Inventory or InventoryView.
     */
    void update();
}
