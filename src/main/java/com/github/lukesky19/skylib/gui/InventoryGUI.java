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

import java.util.HashMap;
import java.util.Map;

import com.github.lukesky19.skylib.format.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A class that represents an Inventory with functions to manipulate it.
 */
public class InventoryGUI implements InventoryHolder, GUIInterface {
    private final Map<Integer, GUIButton> buttonMap = new HashMap<>();
    private Inventory inventory;

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public void setInventory(@NotNull Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Map<Integer, GUIButton> getButtonMap() {
        return buttonMap;
    }

    @Override
    public void setButton(int slot, @NotNull GUIButton button) {
        this.buttonMap.put(slot, button);
    }

    @Override
    public void clearButtons() {
        this.buttonMap.forEach((slot, button) -> inventory.clear(slot));
        this.buttonMap.clear();
    }

    @Override
    public void createInventory(int size, @NotNull String title) {
        Bukkit.createInventory(null, size, FormatUtil.format(title));
    }

    @Override
    public void decorate() {
        this.buttonMap.forEach((slot, button) -> inventory.setItem(slot, button.itemStack()));
    }

    @Override
    public void handleClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getSlot();
        GUIButton button = this.buttonMap.get(slot);
        if (button != null) {
            button.action().accept(event);
        }
    }

    @Override
    public void handleClose(@NotNull InventoryCloseEvent event) {}

    @Override
    public void openInventory(@NotNull Plugin plugin, @NotNull Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.openInventory(inventory), 1L);
    }

    @Override
    public void closeInventory(@NotNull Plugin plugin, @NotNull Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.closeInventory(), 1L);
    }
}
