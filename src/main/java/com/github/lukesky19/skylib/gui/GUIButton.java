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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This class supports the creation of inventory GUIs.
*/
public class GUIButton {
    private final ItemStack itemStack;
    private final Component itemName;
    private final List<Component> lore;
    private final Consumer<InventoryClickEvent> action;

    /**
     * Gets the ItemStack associated with this GUIButton.
     * @return A Bukkit ItemStack
     */
    public ItemStack itemStack() {
        return this.itemStack;
    }

    /**
     * Gets the item name associated with this GUIButton.
     * @return An item name as a Component.
     */
    public Component itemName() {
        return this.itemName;
    }

    /**
     * Gets the lore associated with this GUIButton.
     * @return A list of Components.
     */
    public List<Component> lore() {
        return this.lore;
    }

    /**
     * Gets the action associated with this GUIButton.
     * @return A Consumer that takes an InventoryClickEvent.
     */
    public Consumer<InventoryClickEvent> action() {
        return this.action;
    }

    /**
     * Create a new GUIButton from a Builder.
     * @param builder A Builder representing an incomplete GUIButton.
     */
    public GUIButton(Builder builder) {
        this.itemStack = builder.itemStack;
        this.itemName = builder.itemName;
        this.lore = builder.lore;
        this.action = builder.action;
    }

    /**
     * Builds a new GUIButton.
     */
    public static class Builder {
        private ItemStack itemStack;
        private Component itemName;
        private List<Component> lore = new ArrayList<>();
        private Consumer<InventoryClickEvent> action;

        /**
         * Sets the ItemStack associated with this GUIButton.
         * @param itemStack A Bukkit ItemStack.
         * @return A GUIButton Builder.
         */
        public Builder setItemStack(@NotNull ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        /**
         * Sets the name of the ItemStack associated with this GUIButton.
         * @param itemName The item name as a Component.
         * @return A GUIButton Builder.
         */
        public Builder setItemName(@NotNull Component itemName) {
            this.itemName = itemName;
            return this;
        }

        /**
         * Sets the lore of the ItemStack associated with this GUIButton.
         * @param lore A list of Components.
         * @return A GUIButton Builder.
         */
        public Builder setLore(@NotNull List<Component> lore) {
            this.lore = lore;
            return this;
        }

        /**
         * Sets the action to be taken when a slot is clicked that the GUIButton is associated with.
         * @param action A Consumer that takes an InventoryClickEvent.
         * @return A GUIButton Builder.
         */
        public Builder setAction(@NotNull Consumer<InventoryClickEvent> action) {
            this.action = action;
            return this;
        }

        /**
         * Builds a complete GUIButton.
         * @return A completed GUIButton.
         */
        public GUIButton build() {
            return new GUIButton(this);
        }
    }
}
