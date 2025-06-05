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

import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This enum contains all the GUITypes that can be created.
 * It contains the associated {@link MenuType} and size of the inventory (not 0-indexed)
 */
public enum GUIType {
    /**
     * Creates a GUIType with a {@link MenuType} of ANVIL and an Inventory size of 3.
     */
    ANVIL(MenuType.ANVIL, 3),
    /**
     * Creates a GUIType with a {@link MenuType} of BARREL and an Inventory size of 27.
     */
    BARREL(MenuType.GENERIC_9X3,27),
    /**
     * Creates a GUIType with a {@link MenuType} of BEACON and an Inventory size of 1.
     */
    BEACON(MenuType.BEACON, 1),
    /**
     * Creates a GUIType with a {@link MenuType} of BLAST_FURNACE and an Inventory size of 3.
     */
    BLAST_FURNACE(MenuType.BLAST_FURNACE, 3),
    /**
     * Creates a GUIType with a {@link MenuType} of BREWING_STAND and an Inventory size of 5.
     */
    BREWING_STAND(MenuType.BREWING_STAND, 5),
    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_9X1 and an Inventory size of 9.
     */
    CHEST_9(MenuType.GENERIC_9X1, 9),
    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_9X2 and an Inventory size of 18.
     */
    CHEST_18(MenuType.GENERIC_9X2,18),    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_9X3 and an Inventory size of 27.
     */
    CHEST_27(MenuType.GENERIC_9X3,27),
    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_9X4 and an Inventory size of 36.
     */
    CHEST_36(MenuType.GENERIC_9X4,36),
    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_9X5 and an Inventory size of 45.
     */
    CHEST_45(MenuType.GENERIC_9X5,45),
    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_9X6 and an Inventory size of 54.
     */
    CHEST_54(MenuType.GENERIC_9X6,54),
    /**
     * Creates a GUIType with a {@link MenuType} of CARTOGRAPHY_TABLE and an Inventory size of 2.
     */
    CARTOGRAPHY_TABLE(MenuType.CARTOGRAPHY_TABLE, 2),
    /**
     * Creates a GUIType with a {@link MenuType} of CRAFTER and an Inventory size of 9.
     */
    CRAFTER(MenuType.CRAFTER_3X3, 9),
    /**
     * Creates a GUIType with a {@link MenuType} of CRAFTING and an Inventory size of 10.
     */
    CRAFTING_TABLE(MenuType.CRAFTING, 10),
    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_3X3 and an Inventory size of 9.
     */
    DISPENSER(MenuType.GENERIC_3X3, 9),
    /**
     * Creates a GUIType with a {@link MenuType} of GENERIC_3X3 and an Inventory size of 9.
     */
    DROPPER(MenuType.GENERIC_3X3, 9),
    /**
     * Creates a GUIType with a {@link MenuType} of ENCHANTMENT and an Inventory size of 3.
     */
    ENCHANTMENT_TABLE(MenuType.ENCHANTMENT, 3),
    /**
     * Creates a GUIType with a {@link MenuType} of FURNACE and an Inventory size of 3.
     */
    FURNACE(MenuType.FURNACE, 3),
    /**
     * Creates a GUIType with a {@link MenuType} of GRINDSTONE and an Inventory size of 3.
     */
    GRINDSTONE(MenuType.GRINDSTONE, 3),
    /**
     * Creates a GUIType with a {@link MenuType} of HOPPER and an Inventory size of 5.
     */
    HOPPER(MenuType.HOPPER, 5),
    /**
     * Creates a GUIType with a {@link MenuType} of LECTERN and an Inventory size of 1.
     */
    LECTERN(MenuType.LECTERN, 1),
    /**
     * Creates a GUIType with a {@link MenuType} of LOOM and an Inventory size of 4.
     */
    LOOM(MenuType.LOOM, 4),
    /**
     * Creates a GUIType with a {@link MenuType} of MERCHANT and an Inventory size of 3.
     */
    MERCHANT(MenuType.MERCHANT, 3),
    /**
     * Creates a GUIType with a {@link MenuType} of SHULKER_BOX and an Inventory size of 27.
     */
    SHULKER_BOX(MenuType.SHULKER_BOX, 27),
    /**
     * Creates a GUIType with a {@link MenuType} of SMOKER and an Inventory size of 3.
     */
    SMOKER(MenuType.SMOKER, 3),
    /**
     * Creates a GUIType with a {@link MenuType} of STONECUTTER and an Inventory size of 2.
     */
    STONECUTTER(MenuType.STONECUTTER, 2);

    private final MenuType menuType;
    private final int size;

    /**
     * Creates a GUIType from the given menuType and size.
     * @param menuType The MenuType for this GUIType. {@link MenuType}
     * @param size The size of the Inventory.
     */
    GUIType(MenuType menuType, int size) {
        this.menuType = menuType;
        this.size = size;
    }

    /**
     * Gets the MenuType associated with this GUIType.
     * @return {@link MenuType}
     */
    @NotNull
    public MenuType getMenuType() {
        return menuType;
    }

    /**
     * Gets the size of the inventory associated with this GUIType. (not zero-indexed)
     * @return an int
     */
    public int getSize() {
        return size;
    }

    /**
     * Gets the GUIType based on the string provided.
     * @param type The name of the GUIType to get.
     * @return the GUIType for the string provided or null if invalid.
     */
    @Nullable
    public static GUIType getType(@Nullable String type) {
        if(type == null) return null;

        try {
            return GUIType.valueOf(type);
        } catch(IllegalArgumentException ignored) {
            return null;
        }
    }
}
