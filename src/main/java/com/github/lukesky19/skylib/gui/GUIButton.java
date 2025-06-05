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
package com.github.lukesky19.skylib.gui;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * This class creates buttons for use in button-based GUIs.
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
     * The default constructor should not be used. Use {@link GUIButton#GUIButton(Builder)} instead.
     * @throws RuntimeException if the constructor is used.
     */
    public GUIButton() {
        throw new RuntimeException("The use of the default constructor is not allowed. Use GUIButton(Builder builder) instead.");
    }

    /**
     * Create a new GUIButton from a Builder.
     * @param builder A Builder representing an incomplete GUIButton.
     */
    public GUIButton(@NotNull Builder builder) {
        if(builder.itemStack == null) throw new RuntimeException("Unable to create GUIButton due to null ItemStack.");

        this.itemStack = builder.itemStack;

        this.action = Objects.requireNonNullElseGet(builder.action, () -> inventoryClickEvent -> {});
    }

    /**
     * Builds a new GUIButton.
     */
    public static class Builder {
        @Nullable
        private ItemStack itemStack;
        @Nullable
        private Consumer<InventoryClickEvent> action;

        /**
         * Constructor
         * Creates an instance of the {@link GUIButton.Builder} class.
         * Use the methods {@link #setItemStack(ItemStack)}, {@link #setAction(Consumer)} and {@link #build()}
         */
        public Builder() {}

        /**
         * Sets the ItemStack associated with this GUIButton
         * @param itemStack A Bukkit ItemStack
         */
        public void setItemStack(@NotNull ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        /**
         * Sets the action to be taken when a slot is clicked that the GUIButton is associated with.
         * @param action A Consumer that takes an InventoryClickEvent.
         */
        public void setAction(@NotNull Consumer<InventoryClickEvent> action) {
            this.action = action;
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
