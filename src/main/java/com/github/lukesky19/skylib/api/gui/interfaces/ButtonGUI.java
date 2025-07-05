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
package com.github.lukesky19.skylib.api.gui.interfaces;

import com.github.lukesky19.skylib.api.gui.GUIType;
import com.github.lukesky19.skylib.api.gui.GUIButton;
import com.github.lukesky19.skylib.api.gui.abstracts.ChestGUI;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * This interface can be implemented to create a new button-style GUI.
 * You should only use this interface when creating custom implementations.
 * Otherwise, you should use {@link ChestGUI} for example.
 */
public interface ButtonGUI extends BaseGUI {
    /**
     * Used to create the {@link InventoryView} for GUITypes CHEST_9, CHEST_18, CHEST_27, CHEST_36, CHEST_45, and CHEST_54.
     * @param type The {@link GUIType} to create.
     * @param name The name to use for the GUI/{@link InventoryView}.
     * @param placeholders A {@link List} of {@link TagResolver.Single} for any placeholders in the GUI name.
     * @return true if created successfully, otherwise false.
     */
    boolean create(@NotNull GUIType type, @NotNull String name, @NotNull List<TagResolver.Single> placeholders);

    /**
     * Refreshes the {@link ItemStack}s displayed in the GUI. By default, this just executes {@link #update()}.
     * @return true if successful, otherwise false.
     */
    @Override
    default boolean refresh() {
        return update();
    }

    /**
     * Removes all buttons and ItemStacks for buttons from the Inventory.
     * @return true if successful, otherwise false.
     */
    boolean clearButtons();

    /**
     * Clears the Inventory or InventoryView of all ItemStacks, regardless if that slot has an associated GUIButton.
     * @return true if successful, otherwise false.
     */
    boolean clearInventory();

    /**
     * Maps a {@link GUIButton} to a slot.
     * To actually add the {@link ItemStack}s of the {@link GUIButton} to the Inventory, you must call {@link #update()}
     * @param slot The slot to map the {@link GUIButton} to.
     * @param button The {@link GUIButton} for the given slot.
     * @return true if successful, otherwise false.
     */
    boolean setButton(int slot, @NotNull GUIButton button);

    /**
     * Takes a {@link Map} of representing a collection of slots and GUIButtons and replaces the existing mapping.
     * To actually update the ItemStacks inside the Inventory, you must call {@link #update()}
     * @param buttonMap A HashMap containing a mapping of slots to GUIButtons.
     * @return true if successful, otherwise false.
     */
    boolean setButtons(@NotNull Map<Integer, GUIButton> buttonMap);
}
