package com.github.lukesky19.skylib.gui.interfaces;

import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.gui.GUIType;
import com.github.lukesky19.skylib.gui.GUIButton;
import com.github.lukesky19.skylib.gui.abstracts.ChestGUI;
import com.github.lukesky19.skylib.gui.abstracts.MerchantGUI;
import com.github.lukesky19.skylib.version.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create a base to build other button-based GUI interfaces off of.
 * You should not use this directly unless creating your own GUI interfaces.
 * See {@link ChestGUI} and {@link MerchantGUI} for implementations to use.
 */
public interface ButtonGUI extends BaseGUI {
    /**
     * Creates a GUI for the given inventory type.
     * On versions newer or equal to 1.21.4, the MenuType API will be used.
     * On version older than or equal to 1.21.3, legacy methods will be used.
     * @param player The player to create this Gui for.
     * @param type The type of Gui.
     * @param name The name of the Gui.
     * @param location Optional. The location of the Container to create an InventoryView for.
     *                 NOTE: Only available on version >= 1.21.4 and for MenuTypes that use
     *                 {@link LocationInventoryViewBuilder}
     * @throws RuntimeException if the GUIType is not a valid type.
     */
    default void create(@NotNull Player player, GUIType type, @NotNull String name, @Nullable Location location) {
        if(VersionUtil.getMajorVersion() > 21 || (VersionUtil.getMajorVersion() == 21 && VersionUtil.getMinorVersion() >= 4)) {
            switch(type) {
                case CHEST_9, CHEST_18, CHEST_27, CHEST_36, CHEST_45, CHEST_54 -> {
                    if(type.getMenuType().typed().builder() instanceof LocationInventoryViewBuilder<@NotNull InventoryView> builder) {
                        builder.title(FormatUtil.format(name));

                        builder.checkReachable(false);

                        if(location != null) {
                            builder.location(location);
                        }

                        setInventoryView(builder.build(player));
                    } else {
                        InventoryViewBuilder<@NotNull InventoryView> builder = type.getMenuType().typed().builder();

                        builder.title(FormatUtil.format(name));

                        setInventoryView(builder.build(player));
                    }
                }

                case null, default -> throw new RuntimeException("Unsupported GUIType.");
            }
        } else {
            switch(type) {
                case CHEST_9 -> setInventory(Bukkit.createInventory(null, 9, FormatUtil.format(name)));

                case CHEST_18 -> setInventory(Bukkit.createInventory(null, 18, FormatUtil.format(name)));

                case CHEST_27 -> setInventory(Bukkit.createInventory(null, 27, FormatUtil.format(name)));

                case CHEST_36 -> setInventory(Bukkit.createInventory(null, 36, FormatUtil.format(name)));

                case CHEST_45 -> setInventory(Bukkit.createInventory(null, 45, FormatUtil.format(name)));

                case CHEST_54 -> setInventory(Bukkit.createInventory(null, 54, FormatUtil.format(name)));

                case null, default -> throw new RuntimeException("Unsupported GUIType.");
            }
        }
    }

    /**
     * Populates the Inventory or InventoryView with the GUI's mapping of Buttons.
     * Will place the ItemStack associated with a GUIButton in the corresponding slot.
     * @throws RuntimeException If the button mapping has a GUIButton outside the inventory bounds. Usually if you forget to run clearButtons() inside your override of this method.
     */
    @Override
    default void update() {
        int guiSize = getInventory().getSize();
        int guiSizeZeroIndexed = guiSize - 1;
        
        if(getInventoryView() != null) {
            InventoryView view = getInventoryView();
            getButtonMapping().forEach((slot, button) -> {
                if(slot < 0 || slot > guiSizeZeroIndexed) throw new RuntimeException("Button Mapping has a button for a slot outside of inventory bounds. Slot must be greater than 0 and less than " + guiSize);

                view.setItem(slot, button.itemStack());
            });
        } else {
            Inventory inventory = getInventory();
            getButtonMapping().forEach((slot, button) -> {
                if(slot < 0 || slot > guiSizeZeroIndexed) throw new RuntimeException("Button Mapping has a button for a slot outside of inventory bounds. Slot must be greater than 0 and less than " + guiSize);

                inventory.setItem(slot, button.itemStack());
            });
        }
    }

    @Override
    default void refresh() {
        update();
    }

    /**
     * Removes all buttons and ItemStacks for buttons from the Inventory.
     */
    void clearButtons();

    /**
     * Clears the Inventory or InventoryView of all ItemStacks, regardless if that slot has an associated GUIButton.
     */
    void clear();

    /**
     * Get a HashMap of all slots mapped to buttons.
     * @return A HashMap of all slots mapped to buttons.
     */
    @NotNull
    Map<Integer, GUIButton> getButtonMapping();

    /**
     * Maps a GUIButton to a slot.
     * To actually add the ItemStacks of the GUIButton to the Inventory, you must call {@link #update()}
     * @param slot The slot to map the GUIButton to.
     * @param button The GUIButton for the given slot.
     * @throws RuntimeException if you try to map a button to a slot outside the Inventory bounds.
     */
    void setButton(int slot, @NotNull GUIButton button);

    /**
     * Takes a HashMap of representing a collection of slots and GUIButtons and replaces the existing mapping.
     * To actually update the ItemStacks inside the Inventory, you must call {@link #update()}
     * @param buttonMap A HashMap containing a mapping of slots to GUIButtons.
     */
    void setButtons(HashMap<Integer, GUIButton> buttonMap);

    /**
     * Cancels the InventoryClickEvent and then runs the associated button's action.
     * @param event An InventoryClickEvent
     */
    @Override
    default void handleTopClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getSlot();

        GUIButton button = getButtonMapping().get(slot);
        if (button != null) {
            button.action().accept(event);
        }
    }

    /**
     * Cancels the InventoryDragEvent for all slots inside the GUI's Inventory or InventoryView.
     * @param inventoryDragEvent An InventoryDragEvent.
     */
    @Override
    default void handleTopDrag(@NotNull InventoryDragEvent inventoryDragEvent) {
        inventoryDragEvent.setCancelled(true);
    }
}
