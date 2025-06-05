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
