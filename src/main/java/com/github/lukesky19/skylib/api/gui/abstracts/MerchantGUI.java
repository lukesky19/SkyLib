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
package com.github.lukesky19.skylib.api.gui.abstracts;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.gui.AbstractGUIManager;
import com.github.lukesky19.skylib.api.gui.GUIType;
import com.github.lukesky19.skylib.api.gui.interfaces.TradeGUI;
import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.view.MerchantView;
import org.bukkit.inventory.view.builder.MerchantInventoryViewBuilder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class can be extended to create a merchant-style (i.e., Villagers) GUI. Provides some default functions to assist.
 */
public abstract class MerchantGUI implements TradeGUI {
    /**
     * The plugin who is extending the abstract class to create a GUI.
     */
    protected final @NotNull Plugin plugin;
    /**
     * The plugin's {@link ComponentLogger} to log warnings or errors with.
     */
    protected final @NotNull ComponentLogger logger;
    /**
     * A class extending {@link AbstractGUIManager} that the plugin is using to track open GUIs with.
     */
    protected final @NotNull AbstractGUIManager guiManager;

    /**
     * A {@link List} of {@link MerchantRecipe} that will be used to populate the {@link Merchant} associated with this GUI.
     */
    protected final @NotNull List<MerchantRecipe> trades = new ArrayList<>();
    /**
     * The {@link Player} to create the GUI for.
     */
    protected final @NotNull Player player;
    /**
     * The {@link UUID} of the {@link Player}.
     */
    protected final @NotNull UUID uuid;
    /**
     * The {@link InventoryView} associated with this GUI.
     */
    protected @Nullable InventoryView inventoryView;
    /**
     * The {@link Merchant} associated with this GUI.
     */
    protected @Nullable Merchant merchant;

    /**
     * Constructor.
     * @param plugin The {@link Plugin} creating the GUI.
     * @param guiManager An {@link AbstractGUIManager} that is used to track open GUIs.
     * @param player The {@link Player} associated with the created GUI.
     */
    public MerchantGUI(@NotNull Plugin plugin, @NotNull AbstractGUIManager guiManager, @NotNull Player player) {
        this.plugin = plugin;
        this.logger = plugin.getComponentLogger();
        this.guiManager = guiManager;
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    /**
     * Get the {@link InventoryView} associated with the GUI.
     * @return An {@link Optional} containing an {@link InventoryView}. If empty, that means {@link #create(String, List)} was not called.
     */
    @Override
    public @NotNull Optional<@NotNull InventoryView> getInventoryView() {
        return Optional.ofNullable(inventoryView);
    }

    /**
     * Creates the Inventory or InventoryView for the {@link GUIType} MERCHANT.
     * @param name The name of the Inventory.
     * @param placeholders A {@link List} of {@link TagResolver.Single} for any placeholders in the GUI name.
     * @return This always returns true as this method always succeeds.
     */
    @Override
    public boolean create(@NotNull String name, @NotNull List<TagResolver.Single> placeholders) {
        @NotNull MerchantInventoryViewBuilder<@NotNull MerchantView> inventoryViewBuilder = MenuType.MERCHANT.builder();

        merchant = plugin.getServer().createMerchant();

        inventoryViewBuilder.title(AdventureUtil.serialize(player, name, placeholders));
        inventoryViewBuilder.merchant(merchant);
        inventoryViewBuilder.checkReachable(false);

        inventoryView = inventoryViewBuilder.build(player);

        return true;
    }

    /**
     * Open the GUI for the {@link Player} the InventoryView was created with.
     * @return true if opened successfully, otherwise false.
     */
    @Override
    public boolean open() {
        if(inventoryView == null) {
            // If the InventoryView was not created, log a warning and return false.
            logger.warn(AdventureUtil.serialize("Unable to open the InventoryView as it was not created."));
            return false;
        }

        // Close the current Inventory the player has open (if any)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW), 1L);

        // Then 1 tick later, open the GUI and track that it is open for the player.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            inventoryView.open();

            guiManager.addOpenGUI(player.getUniqueId(), this);
        }, 2L);

        return true;
    }

    /**
     * Close the GUI with an UNLOADED {@link InventoryCloseEvent.Reason}.
     * You should use {@link #unload(boolean)} if the plugin is being disabled, and you are trying to close open GUIs.
     */
    @Override
    public void close() {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.closeInventory(InventoryCloseEvent.Reason.UNLOADED), 1L);
    }

    /**
     * Close the GUI with an UNLOADED {@link InventoryCloseEvent.Reason}.
     * If the plugin is being disabled, the scheduler won't be used as it is unavailable during server shutdown.
     * @param onDisable Is the plugin being disabled?
     */
    @Override
    public void unload(boolean onDisable) {
        if(!onDisable) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.closeInventory(InventoryCloseEvent.Reason.UNLOADED), 1L);
        } else {
            player.closeInventory(InventoryCloseEvent.Reason.UNLOADED);
        }
    }

    /**
     * Adds the {@link #trades} to the {@link Merchant} associated with this GUI.
     * This method by default completes synchronously.
     * @return A {@link CompletableFuture} containing a {@link Boolean}. true if successful, otherwise false
     */
    @Override
    public boolean update() {
        if(merchant == null) {
            // If the Merchant was not created, log a warning and return false.
            logger.warn(AdventureUtil.serialize("Unable to add the trades to the Merchant as it was not created."));
            return false;
        }

        merchant.setRecipes(trades);
        return true;
    }

    /**
     * Get a {@link List} of {@link MerchantRecipe}s that will be or was used to populate the {@link Merchant} associated with the GUI with trades.
     * This will not return the trades within the {@link Merchant} associated with the GUI that are updated as the player interacts with trades.
     * You can use {@link #getLiveTrades()} to get those.
     * @return A {@link List} of {@link MerchantRecipe}s.
     */
    @Override
    public @NotNull List<@NotNull MerchantRecipe> getTrades() {
        return trades;
    }

    /**
     * Get a {@link List} of {@link MerchantRecipe} that are within the {@link Merchant} associated with the GUI.
     * These are the trades that are updated as the player interacts with the trades.
     * To get the trades that is used or will be used to populate the {@link Merchant} associated with the GUI with
     * trades, use {@link #getTrades()}.
     * @return An {@link Optional} of {@link List} containing {@link MerchantRecipe}s.
     * The optional may be empty if there is no {@link Merchant} associated with the GUI.
     */
    @Override
    public @NotNull Optional<@NotNull List<@NotNull MerchantRecipe>> getLiveTrades() {
        if(merchant == null) {
            // If the Merchant was not created, log a warning and return false.
            logger.warn(AdventureUtil.serialize("Unable to add the trades to the Merchant as it was not created."));
            return Optional.empty();
        }

        return Optional.of(merchant.getRecipes());
    }

    /**
     * Adds a {@link MerchantRecipe} to the list of trades to add to the {@link Merchant} associated with the GUI.
     * @apiNote You must call {@link #update()} to actually add the trades to the {@link Merchant} associated with the GUI.
     * @param merchantRecipe The {@link MerchantRecipe} to add.
     */
    @Override
    public void addTrade(@NotNull MerchantRecipe merchantRecipe) {
        trades.add(merchantRecipe);
    }

    /**
     * Removes a {@link MerchantRecipe} from the list of trades to add to the {@link Merchant} associated with the GUI.
     * @apiNote You must call {@link #update()} to actually add the trades to the {@link Merchant} associated with the GUI.
     * @param merchantRecipe A {@link MerchantRecipe} to remove.
     */
    @Override
    public void removeTrade(@NotNull MerchantRecipe merchantRecipe) {
        trades.remove(merchantRecipe);
    }

    /**
     * Takes a {@link List} of {@link MerchantRecipe} and replaces the existing list.
     * @apiNote You must call {@link #update()} to actually add the trades to the {@link Merchant} associated with the GUI.
     * @param tradeList A {@link List} of {@link MerchantRecipe} to add to the Merchant.
     */
    @Override
    public void setTrades(@NotNull List<MerchantRecipe> tradeList) {
        trades.clear();

        trades.addAll(tradeList);
    }

    /**
     * Used to define how a {@link PlayerTradeEvent} should be handled.
     * @param playerTradeEvent A {@link PlayerTradeEvent}.
     */
    @Override
    public abstract void handlePlayerTrade(@NotNull PlayerTradeEvent playerTradeEvent);

    /**
     * Used to define how a {@link TradeSelectEvent} should be handled.
     * @param tradeSelectEvent A {@link TradeSelectEvent}.
     */
    @Override
    public abstract void handleTradeSelect(@NotNull TradeSelectEvent tradeSelectEvent);
}
