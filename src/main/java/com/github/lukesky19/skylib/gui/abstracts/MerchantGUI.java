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
package com.github.lukesky19.skylib.gui.abstracts;

import com.github.lukesky19.skylib.gui.interfaces.TradeGUI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class can be extended to create a GUI based on the Villager trading (Merchant) UI.
 * Make sure to run {@link #create(Player, String, Merchant, boolean)} and to override
 * methods like {@link #update()} to set the trades inside the GUI.
 * Your plugins need to track open GUIs and handle passing all the events to the GUIs.
 * See SkyMarket for an example implementation.
 */
public abstract class MerchantGUI implements TradeGUI {
    /**
     * This list contains the trades (MerchantRecipe) to trade.
     */
    private final List<MerchantRecipe> trades = new ArrayList<>();
    private InventoryView view;
    private Merchant merchant;

    /**
     * Default Constructor.
     */
    public MerchantGUI() {}

    @Override
    public @NotNull InventoryView getInventoryView() {
        return view;
    }

    @Override
    public void setInventoryView(@NotNull InventoryView view) {
        this.view = view;
    }

    @Override
    public CompletableFuture<Void> update() {
        return CompletableFuture.runAsync(() -> merchant.setRecipes(trades));
    }

    @Override
    @NotNull
    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public void setMerchant(@NotNull Merchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public List<MerchantRecipe> getTrades() {
        return trades;
    }

    @Override
    public List<MerchantRecipe> getLiveTrades() {
        return merchant.getRecipes();
    }

    @Override
    public void setTrades(List<MerchantRecipe> tradeList) {
        trades.clear();

        trades.addAll(tradeList);
    }

    @Override
    public void addTrade(MerchantRecipe merchantRecipe) {
        trades.add(merchantRecipe);
    }

    @Override
    public void removeTrade(MerchantRecipe merchantRecipe) {
        trades.remove(merchantRecipe);
    }
}
