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
import com.github.lukesky19.skylib.api.gui.abstracts.ChestGUI;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This class is used to create a gui that uses trades.
 * You should only use this interface when creating custom implementations for {@link GUIType} of MERCHANT.
 * Otherwise, you should use {@link ChestGUI} for example.
 */
public interface TradeGUI extends BaseGUI {
    /**
     * Used to create the {@link InventoryView} for {@link GUIType} MERCHANT.
     * @param name The name to use for the GUI/{@link InventoryView}.
     * @return true if created successfully, otherwise false.
     */
    boolean create(@NotNull String name);

    /**
     * Refreshes the {@link ItemStack}s displayed in the GUI. By default, this just executes {@link #update()}.
     * @return A {@link CompletableFuture} containing true if successful, otherwise false.
     */
    @Override
    default @NotNull CompletableFuture<@NotNull Boolean> refresh() {
        return update();
    }

    /**
     * Get a {@link List} of {@link MerchantRecipe}s that will be or was used to populate the {@link Merchant} associated with the GUI with trades.
     * This will not return the trades within the {@link Merchant} associated with the GUI that are updated as the player interacts with trades.
     * You can use {@link #getLiveTrades()} to get those.
     * @return A {@link List} of {@link MerchantRecipe}s.
     */
    @NotNull List<@NotNull MerchantRecipe> getTrades();

    /**
     * Get a {@link List} of {@link MerchantRecipe} that are within the {@link Merchant} associated with the GUI.
     * These are the trades that are updated as the player interacts with the trades.
     * To get the trades that is used or will be used to populate the {@link Merchant} associated with the GUI with
     * trades, use {@link #getTrades()}.
     * @return An {@link Optional} of {@link List} containing {@link MerchantRecipe}s.
     * The optional may be empty if there is no {@link Merchant} associated with the GUI.
     */
    @NotNull Optional<@NotNull List<@NotNull MerchantRecipe>> getLiveTrades();
    
    /**
     * Adds a {@link MerchantRecipe} to the list of trades to add to the {@link Merchant} associated with the GUI.
     * @apiNote You must call {@link #update()} to actually add the trades to the {@link Merchant} associated with the GUI.
     * @param merchantRecipe The {@link MerchantRecipe} to add.
     */
    void addTrade(@NotNull MerchantRecipe merchantRecipe);

    /**
     * Removes a {@link MerchantRecipe} from the list of trades to add to the {@link Merchant} associated with the GUI.
     * @apiNote You must call {@link #update()} to actually add the trades to the {@link Merchant} associated with the GUI.
     * @param merchantRecipe A {@link MerchantRecipe} to remove.
     */
    void removeTrade(@NotNull MerchantRecipe merchantRecipe);

    /**
     * Takes a {@link List} of {@link MerchantRecipe} and replaces the existing list.
     * @apiNote You must call {@link #update()} to actually add the trades to the {@link Merchant} associated with the GUI.
     * @param tradeList A {@link List} of {@link MerchantRecipe} to add to the Merchant.
     */
    void setTrades(@NotNull List<@NotNull MerchantRecipe> tradeList);

    /**
     * Used to define how a {@link PlayerTradeEvent} should be handled.
     * @param playerTradeEvent A {@link PlayerTradeEvent}.
     */
    void handlePlayerTrade(@NotNull PlayerTradeEvent playerTradeEvent);

    /**
     * Used to define how a {@link TradeSelectEvent} should be handled.
     * @param tradeSelectEvent A {@link TradeSelectEvent}.
     */
    void handleTradeSelect(@NotNull TradeSelectEvent tradeSelectEvent);
}
