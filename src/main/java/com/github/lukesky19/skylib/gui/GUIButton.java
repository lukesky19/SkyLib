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
import java.util.Objects;
import java.util.function.Consumer;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skylib.version.VersionUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckForNull;

/**
 * This class supports the creation of inventory GUIs.
*/
public class GUIButton {
    @NotNull
    private final ItemStack itemStack;
    @NotNull
    private final Consumer<InventoryClickEvent> action;

    /**
     * Gets the ItemStack associated with this GUIButton.
     * @return A Bukkit ItemStack
     */
    @NotNull
    public ItemStack itemStack() {
        return this.itemStack;
    }

    /**
     * Gets the action associated with this GUIButton.
     * @return A Consumer that takes an InventoryClickEvent.
     */
    @NotNull
    public Consumer<InventoryClickEvent> action() {
        return this.action;
    }

    /**
     * Create a new GUIButton from a Builder.
     * @param builder A Builder representing an incomplete GUIButton.
     */
    public GUIButton(Builder builder) {
        // Create a new ItemStack and get the ItemMeta
        if(builder.material == null) throw new RuntimeException("Material is required to build the button.");

        final ItemStack builtStack = new ItemStack(builder.material);
        ItemMeta itemMeta = builtStack.getItemMeta();

        // Set the item name.
        if(builder.name != null) itemMeta.displayName(builder.name);

        // Set the item lore
        itemMeta.lore(builder.lore);

        // Set the item flags
        builder.itemFlags.forEach(itemMeta::addItemFlags);

        // Set the item model
        if(builder.model != null) {
            itemMeta.setItemModel(builder.model);
        }

        // Set the PlayerProfile of a skull if the material matches PLAYER_HEAD.
        if(builder.material == Material.PLAYER_HEAD) {
            if (builder.playerProfile != null) {
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                skullMeta.setPlayerProfile(builder.playerProfile);
            }
        }

        // Set the ItemStack's ItemMeta
        builtStack.setItemMeta(itemMeta);

        // Set the ItemStack's size (amount)
        if(builder.amount != null) {
            builtStack.setAmount(builder.amount);
        }

        // Copy the final ItemStack and action to the class variables
        this.itemStack = builtStack;
        this.action = Objects.requireNonNullElseGet(builder.action, () -> inventoryClickEvent -> {});
    }

    /**
     * Builds a new GUIButton.
     */
    public static class Builder {
        @CheckForNull private Material material;
        @CheckForNull private Integer amount;
        @CheckForNull Component name;
        @NotNull private List<Component> lore = new ArrayList<>();
        @NotNull private List<ItemFlag> itemFlags = new ArrayList<>();
        @CheckForNull private NamespacedKey model;
        @CheckForNull PlayerProfile playerProfile;
        @CheckForNull private Consumer<InventoryClickEvent> action;

        /**
         * Sets the Material of the final ItemStack associated with this GUIButton.
         * @param material A Bukkit Material.
         * @return A GUIButton Builder.
         */
        public Builder setMaterial(@NotNull Material material) {
            this.material = material;
            return this;
        }

        /**
         * Sets the amount of the final ItemStack associated with this GUIButton.
         * @param amount An int representing the amount in the ItemStack.
         * @return  A GUIButton Builder.
         */
        public Builder setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        /**
         * Sets the name of the final ItemStack associated with this GUIButton.
         * @param name The item name as a Component.
         * @return A GUIButton Builder.
         */
        public Builder setItemName(@NotNull Component name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the lore to add to the final ItemStack associated with this GUIButton.
         * @param lore A list of Components.
         * @return A GUIButton Builder.
         */
        public Builder setLore(@NotNull List<Component> lore) {
            this.lore = lore;
            return this;
        }

        /**
         * Sets the {@literal List<ItemFlags>} to add to the final ItemStack associated with this GUIButton.
         * @param itemFlags A list of ItemFlag
         * @return A GUIButton Builder.
         */
        public Builder setItemFlags(@NotNull List<ItemFlag> itemFlags) {
            this.itemFlags = itemFlags;
            return this;
        }

        /**
         * Sets the item model of the final ItemStack associated with this GUIButton.
         * @param model The NamespacedKey of the model.
         * @return A GUIButton Builder.
         */
        public Builder setModel(@NotNull NamespacedKey model) {
            if(VersionUtil.getMajorVersion() >= 21 && VersionUtil.getMinorVersion() >= 3) {
                this.model = model;
                return this;
            }

            throw new RuntimeException("Item Models are only available on Minecraft 1.21.3 and newer.");
        }

        /**
         * Sets the PlayerProfile if the Material is that of a PLAYER_HEAD
         * @param playerProfile A PlayerProfile of an online player.
         * @return A GUIButton Builder.
         */
        public Builder setPlayerProfile(@NotNull PlayerProfile playerProfile) {
            this.playerProfile = playerProfile;
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
