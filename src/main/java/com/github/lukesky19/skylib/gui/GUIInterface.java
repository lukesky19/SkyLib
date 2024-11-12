/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skylib.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An interface to access a GUI.
 */
public interface GUIInterface {
    /**
     * Gets the Inventory for this GUI/
     * @return A Bukkit Inventory
     */
    Inventory getInventory();

    /**
     * Sets the Inventory to use for the GUI.
     * @param inventory A Bukkit Inventory
     */
    void setInventory(@NotNull Inventory inventory);

    /**
     * Gets Map of all buttons registered with this GUI.
     * The key is the inventory slot, the value is the GUIButton.
     * @return A {@literal Map<Integer, GUIButton>}
     */
    Map<Integer, GUIButton> getButtonMap();

    /**
     * Adds a button to be added to the Inventory when decorate() is called.
     * @param slot The slot for the GUIButton
     * @param button The GUIButton
     */
    void setButton(int slot, @NotNull GUIButton button);

    /**
     * Removes all buttons from the Inventory and clears the Inventory of items.
     */
    void clearButtons();

    /**
     * Creates a Bukkit Inventory for this GUI.
     * @param size The size of the Inventory.
     * @param name The name of the Inventory.
     */
    void createInventory(int size, @NotNull String name);

    /**
     * Adds all configured GUIButtons to the Inventory.
     */
    void decorate();

    /**
     * Handles when a player clicks a GUI.
     * @param event An InventoryClickEvent
     */
    void handleClick(@NotNull InventoryClickEvent event);

    /**
     * Handles when a player closes a GUI.
     * @param event An InventoryCloseEvent
     */
    void handleClose(@NotNull InventoryCloseEvent event);

    /**
     * Handles opening a GUI for a player
     * @param plugin The Plugin opening the GUI.
     * @param player The Player opening the GUI.
     */
    void openInventory(@NotNull Plugin plugin, @NotNull Player player);

    /**
     * Handles closing a GUI for a player
     * @param plugin The Plugin opening the GUI.
     * @param player The Player opening the GUI.
     */
    void closeInventory(@NotNull Plugin plugin, @NotNull Player player);
}
