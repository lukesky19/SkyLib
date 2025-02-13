package com.github.lukesky19.skylib.gui.interfaces;

import com.github.lukesky19.skylib.gui.GUIType;
import com.github.lukesky19.skylib.gui.GUIButton;
import com.github.lukesky19.skylib.gui.abstracts.ChestGUI;
import com.github.lukesky19.skylib.gui.abstracts.MerchantGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
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
     * This hashmap maps slots (as an Integer) to a GUIButton.
     */
    HashMap<Integer, GUIButton> slotButtons = new HashMap<>();

    /**
     * Creates a GUI for the given inventory type.
     * On versions newer or equal to 1.21.4, the MenuType API will be used.
     * On version older than or equal to 1.21.3, legacy methods will be used.
     * @param player The player to create this Gui for.
     * @param type The type of Gui.
     * @param name The name of the Gui.
     * @param location Optional. The location of the Container to create an InventoryView for. NOTE: Only available on version >= 1.21.4
     */
    void createInventory(@NotNull Player player, GUIType type, @NotNull String name, @Nullable Location location);

    /**
     * Populates the Inventory or InventoryView with the GUI's mapping of Buttons.
     * Will place the ItemStack associated with a GUIButton in the corresponding slot.
     * @throws RuntimeException If the button mapping has a GUIButton outside the inventory bounds. Usually if you forget to run clearButtons() inside your override of this method.
     */
    default void update() {
        int guiSize = getInventory().getSize();
        int guiSizeZeroIndexed = guiSize - 1;
        
        if(getInventoryView() != null) {
            InventoryView view = getInventoryView();
            slotButtons.forEach((slot, button) -> {
                if(slot < 0 || slot > guiSizeZeroIndexed) throw new RuntimeException("Button Mapping has a button for a slot outside of inventory bounds. Slot must be greater than 0 and less than " + guiSize);

                view.setItem(slot, button.itemStack());
            });
        } else {
            Inventory inventory = getInventory();
            slotButtons.forEach((slot, button) -> {
                if(slot < 0 || slot > guiSizeZeroIndexed) throw new RuntimeException("Button Mapping has a button for a slot outside of inventory bounds. Slot must be greater than 0 and less than " + guiSize);

                inventory.setItem(slot, button.itemStack());
            });
        }
    }

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

    default void handleTopDrag(@NotNull InventoryDragEvent inventoryDragEvent) {
        inventoryDragEvent.setCancelled(true);
    }

    /**
     * Get a HashMap of all slots mapped to buttons.
     * @return A HashMap of all slots mapped to buttons.
     */
    @NotNull
    default Map<Integer, GUIButton> getButtonMapping() {
        return ButtonGUI.slotButtons;
    }

    // Method 'getButtinMapping()' always returns 'com.github.lukesky19.skylib.gui.interfaces.ButtonGUI.slotButtons'

    /**
     * Maps a GUIButton to a slot.
     * To actually add the buttons to the Inventory, you must call {@link #update()}
     * @param slot The slot to map the GUIButton to.
     * @param button The GUIButton for the given slot.
     * @throws RuntimeException if you try to map a button to a slot outside the Inventory bounds.
     */
    default void setButton(int slot, @NotNull GUIButton button) {
        Inventory inventory = getInventory();

        if(slot < 0 || slot >= inventory.getSize()) {
            throw new RuntimeException("Provided slot is outside of inventory bounds. Slot must be greater than 0 and less than " + inventory.getSize());
        }

        slotButtons.put(slot, button);
    }

    /**
     * Takes a HashMap of representing a collection of slots and GUIButtons and replaces the existing mapping.
     * @param buttonMap A HashMap containing a mapping of slots to GUIButtons.
     */
    default void setButtons(HashMap<Integer, GUIButton> buttonMap) {
        clearButtons();

        slotButtons.putAll(buttonMap);
    }

    /**
     * Removes all buttons and ItemStacks for buttons from the Inventory.
     */
    default void clearButtons() {
        clearInventoryButtonsOnly();
        
        slotButtons.clear();
    }

    /**
     * Clears the Inventory or InventoryView of all ItemStacks where the slot has an associated GUIButton.
     */
    private void clearInventoryButtonsOnly() {
        ItemStack clearStack = new ItemStack(Material.AIR);

        if(getInventoryView() != null) {
            InventoryView view = getInventoryView();

            int guiSize = getInventory().getSize() - 1;
            for(int slot = 0; slot <= guiSize; slot++) {
                if(slotButtons.containsKey(slot)) {
                    view.setItem(slot, clearStack);
                }
            }
        } else {
            Inventory inventory = getInventory();
            int guiSize = getInventory().getSize() - 1;
            for(int slot = 0; slot <= guiSize; slot++) {
                if(slotButtons.containsKey(slot)) {
                    inventory.setItem(slot, clearStack);
                }
            }
        }
    }

    /**
     * Clears the Inventory or InventoryView of all ItemStacks, regardless if that slot has an associated GUIButton.
     */
    default void clearInventory() {
        ItemStack clearStack = new ItemStack(Material.AIR);
        int guiSize = getInventory().getSize() - 1;
        
        if(getInventoryView() != null) {
            InventoryView view = getInventoryView();
            
            for(int slot = 0; slot <= guiSize; slot++) {
                view.setItem(slot, clearStack);
            }
        } else {
            Inventory inventory = getInventory();
            
            for(int slot = 0; slot <= guiSize; slot++) {
                inventory.setItem(slot, clearStack);
            }
        }
    }
}
