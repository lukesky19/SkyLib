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

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This interface provides a base structure for the creation of other GUIs.
 * You should use {@link ButtonGUI} and {@link TradeGUI} unless you are creating a brand-new type of GUI.
 */
public interface BaseGUI {
    /**
     * Get the {@link InventoryView} associated with this GUI.
     * @return An {@link InventoryView}.
     */
    @NotNull Optional<InventoryView> getInventoryView();

    /**
     * Opens the {@link InventoryView} for this GUI.
     * @return true if successful, otherwise false.
     */
    boolean open();

    /**
     * Closes the GUI.
     * You should define a {@link InventoryCloseEvent.Reason} when closing the player's Inventory and in
     * {@link #handleClose(InventoryCloseEvent)} ignore that {@link InventoryCloseEvent.Reason}.
     */
    void close();

    /**
     * Closes the GUI.
     * @param onDisable Is the plugin being disabled?
     */
    void unload(boolean onDisable);

    /**
     * Updates what is displayed in the GUI.
     * @return A {@link CompletableFuture} containing a {@link Boolean} where true if successful, otherwise false.
     */
    @NotNull CompletableFuture<Boolean> update();

    /**
     * Refreshes the contents of what is displayed in the GUI.
     * @return A {@link CompletableFuture} containing a {@link Boolean} where true if successful, otherwise false.
     */
    @NotNull CompletableFuture<Boolean> refresh();

    /**
     * Used to define how an {@link InventoryCloseEvent} should be handled.
     * @param inventoryCloseEvent An {@link InventoryCloseEvent}
     */
    void handleClose(@NotNull InventoryCloseEvent inventoryCloseEvent);

    /**
     * Used to define how an {@link InventoryDragEvent} should be handled for the top half of the {@link InventoryView}.
     * @param inventoryDragEvent An {@link InventoryDragEvent}
     */
    void handleTopDrag(@NotNull InventoryDragEvent inventoryDragEvent);

    /**
     * Used to define how an {@link InventoryDragEvent} should be handled for the bottom half of the {@link InventoryView}.
     * @param inventoryDragEvent An {@link InventoryDragEvent}
     */
    void handleBottomDrag(@NotNull InventoryDragEvent inventoryDragEvent);

    /**
     * Used to define how an {@link InventoryDragEvent} should be handled for the top and bottom half of the {@link InventoryView}.
     * @param inventoryDragEvent An {@link InventoryDragEvent}
     */
    void handleGlobalDrag(@NotNull InventoryDragEvent inventoryDragEvent);

    /**
     * Used to define how an {@link InventoryClickEvent} should be handled for the top half of the {@link InventoryView}.
     * @param inventoryClickEvent An {@link InventoryClickEvent}
     */
    void handleTopClick(@NotNull InventoryClickEvent inventoryClickEvent);

    /**
     * Used to define how an {@link InventoryClickEvent} should be handled for the bottom half of the {@link InventoryView}.
     * @param inventoryClickEvent An {@link InventoryClickEvent}
     */
    void handleBottomClick(@NotNull InventoryClickEvent inventoryClickEvent);

    /**
     * Used to define how an {@link InventoryClickEvent} should be handled for the top and bottom half of the {@link InventoryView}.
     * @param inventoryClickEvent An {@link InventoryClickEvent}
     */
    void handleGlobalClick(@NotNull InventoryClickEvent inventoryClickEvent);
}
