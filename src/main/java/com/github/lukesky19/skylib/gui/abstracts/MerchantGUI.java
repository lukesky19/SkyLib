package com.github.lukesky19.skylib.gui.abstracts;

import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.gui.interfaces.TradeGUI;
import com.github.lukesky19.skylib.version.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.view.MerchantView;
import org.bukkit.inventory.view.builder.MerchantInventoryViewBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class can be extended to create a GUI based on the Villager trading (Merchant) UI.
 * Make sure to run {@link #createInventory(Player, String, Merchant, boolean)} and to override
 * methods like {@link #update()} to set the trades inside the GUI.
 * Your plugins need to track open GUIs and handle passing all the events to the GUIs.
 * See SkyMarket for an example implementation.
 */
public abstract class MerchantGUI implements TradeGUI {
    /**
     * This list contains the trades (MerchantRecipe) to trade.
     */
    List<MerchantRecipe> trades = new ArrayList<>();
    private InventoryView view; // Used on all versions
    private Merchant merchant; // Used on all versions

    @Override
    public void createInventory(Player player, @NotNull String name, @Nullable Merchant merchant, boolean reachable) {
        if(VersionUtil.getMajorVersion() > 21 || (VersionUtil.getMajorVersion() == 21 && VersionUtil.getMinorVersion() >= 4)) {
            @NotNull MerchantInventoryViewBuilder<@NotNull MerchantView> builder = MenuType.MERCHANT.builder();

            this.merchant = Objects.requireNonNullElseGet(merchant, () -> Bukkit.getServer().createMerchant());

            builder.title(FormatUtil.format(name));
            builder.merchant(this.merchant);
            builder.checkReachable(reachable);

            view = builder.build(player);
        } else {
            // This deprecation warning can be ignored, it is to keep support for version <= 1.21.3.
            this.merchant = Bukkit.getServer().createMerchant(FormatUtil.format(name));
        }
    }

    /**
     * This may be null if the InventoryView failed to open on 1.21.3.
     * It is never null on 1.21.4 or newer.
     * Use {@link #getInventory()} instead.
     * @return An InventoryView.
     */
    @CheckForNull
    public InventoryView getInventoryView() {
        return view;
    }

    @Override
    public @NotNull Inventory getInventory() {
        throw new RuntimeException("This method should not be used. Use getInventoryView() instead.");
    }

    @NotNull
    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(@NotNull Merchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public void setInventoryView(@NotNull InventoryView view) {
        this.view = view;
    }

    @Override
    public void update() {
        merchant.setRecipes(trades);
    }

    @Override
    public void refresh() {
        update();
    }

    /**
     * Takes a List of MerchantRecipes and replaces the existing list.
     * @param tradeList A List the MerchantRecipes to add to the Merchant.
     */
    public void setTrades(List<MerchantRecipe> tradeList) {
        trades.clear();

        trades.addAll(tradeList);
    }

    /**
     * Adds a MerchantRecipe to the list of trades to add to the Merchant.
     * To update the merchant's trades, you must call {@link #update()}
     * @param merchantRecipe The MerchantRecipe to add.
     */
    public void addTrade(MerchantRecipe merchantRecipe) {
        trades.add(merchantRecipe);
    }

    /**
     * Removes a MerchantRecipe to the list of trades to add to the Merchant.
     * To update the merchant's trades, you must call {@link #update()}
     * @param merchantRecipe The MerchantRecipe to add.
     */
    public void removeTrade(MerchantRecipe merchantRecipe) {
        trades.remove(merchantRecipe);
    }

    /**
     * Get the list of initial trades for this GUI.
     * NOTE: Will not return the live trades. Use {@link #getLiveTrades()} for those.
     * @return A List of MerchantRecipes
     */
    public List<MerchantRecipe> getTrades() {
        return trades;
    }

    /**
     * Get the list of live trades for this GUI/Merchant.
     * @return A List of MerchantRecipes
     */
    public List<MerchantRecipe> getLiveTrades() {
        return merchant.getRecipes();
    }
}
