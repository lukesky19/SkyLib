package com.github.lukesky19.skylib.gui.interfaces;

import com.github.lukesky19.skylib.format.FormatUtil;



import com.github.lukesky19.skylib.version.VersionUtil;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.view.MerchantView;
import org.bukkit.inventory.view.builder.MerchantInventoryViewBuilder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * This class is used to create a gui that uses trades.
 * This interface should only be used for MenuType of MERCHANT and nothing else.
 * See {@link ButtonGUI} for all other MenuTypes.
 */
public interface TradeGUI extends BaseGUI {
    /**
     * The Merchant associated with this GUI.
     * @return A Bukkit Merchant.
     */
    Merchant getMerchant();

    /**
     * Sets the Merchant associated with this GUI.
     * @param merchant A Bukkit Merchant.
     */
    void setMerchant(@NotNull Merchant merchant);

    /**
     * Sets the InventoryView associated with this GUI.
     * @param view An InventoryView.
     */
    void setInventoryView(@NotNull InventoryView view);

    /**
     * Creates the Inventory or InventoryView for the {@link MenuType} MERCHANT.
     * @param player The Player to create the GUI for.
     * @param name The name of the Inventory.
     * @param merchant The merchant associated with this GUI. If null, a virtual merchant will be created.
     * @param reachable If the merchant should be checked if it is reachable or not. Only useful for non-virtual merchants in the world.
     */
    default void createInventory(Player player, @NotNull String name, @Nullable Merchant merchant, boolean reachable) {
        if(VersionUtil.getMajorVersion() > 21 || (VersionUtil.getMajorVersion() == 21 && VersionUtil.getMinorVersion() >= 4)) {
            @NotNull MerchantInventoryViewBuilder<@NotNull MerchantView> builder = MenuType.MERCHANT.builder();

            Merchant validMerchant = Objects.requireNonNullElseGet(merchant, () -> Bukkit.getServer().createMerchant());
            setMerchant(validMerchant);

            builder.title(FormatUtil.format(name));
            builder.merchant(validMerchant);
            builder.checkReachable(reachable);

            setInventoryView(builder.build(player));
        } else {
            // This deprecation warning can be ignored, it is to keep support for version <= 1.21.3.
            // noinspection deprecation
            setMerchant(Bukkit.getServer().createMerchant(FormatUtil.format(name)));
        }
    }

    /**
     * Adds a MerchantRecipe to the list of trades to add to the Merchant.
     * @param merchantRecipe The MerchantRecipe to add.
     */
    void addTrade(MerchantRecipe merchantRecipe);

    /**
     * Takes a List of MerchantRecipes and replaces the existing list.
     * @param tradeList A List the MerchantRecipes to add to the Merchant.
     */
    void setTrades(List<MerchantRecipe> tradeList);

    List<MerchantRecipe> getTrades();

    /**
     * Default handling of when any trade is completed by the Player.
     * You should override this if you want to do something with the event.
     * @param playerTradeEvent A PlayerTradeEvent
     */
    default void handlePlayerTrade(PlayerTradeEvent playerTradeEvent) {}

    /**
     * Default handling of when any trade is selected by the Player.
     * @param tradeSelectEvent A TradeSelectEvent
     */
    default void handleTradeSelect(TradeSelectEvent tradeSelectEvent) {}

    @Override
    default void openInventory(@NotNull Plugin plugin, @NotNull Player player) {
        if(VersionUtil.getMajorVersion() > 21 || (VersionUtil.getMajorVersion() == 21 && VersionUtil.getMinorVersion() >= 4)) {
            if(getInventoryView() != null) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                        player.openInventory(getInventoryView()), 1L);
            } else {
                throw new RuntimeException("Unable to open this GUI due to a null InventoryView. At least one is needed.");
            }
        } else {
            // This deprecation warning can be ignored, it is to keep support for version <= 1.21.3.
            // noinspection deprecation
            InventoryView view = player.openMerchant(getMerchant(), false);
            if (view != null) {
                setInventoryView(view);
            }
        }
    }
}
