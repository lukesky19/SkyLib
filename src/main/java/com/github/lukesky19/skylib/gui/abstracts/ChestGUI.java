package com.github.lukesky19.skylib.gui.abstracts;

import com.github.lukesky19.skylib.gui.GUIButton;
import com.github.lukesky19.skylib.gui.GUIType;
import com.github.lukesky19.skylib.gui.interfaces.ButtonGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be extended to create a GUI based on the Chest UI.
 * Make sure to run {@link #create(Player, GUIType, String, Location)} and to override
 * methods like {@link #open(Plugin, Player)}, {@link #update()}, {@link #refresh()}, {@link #unload(Plugin, Player, boolean)},
 * {@link #handleClose(InventoryCloseEvent)}, and {@link #close(Plugin, Player)}.
 * Your plugins need to track open GUIs and handle passing all the events to the GUIs.
 * See any of my plugins like SkyShop or SkyMarket for example implementations.
 */
public abstract class ChestGUI implements ButtonGUI {
    private final HashMap<Integer, GUIButton> slotButtons = new HashMap<>();
    private InventoryView view; // >= 1.21.4 only
    private Inventory inventory; // All versions

    @Override
    public void setInventoryView(@NotNull InventoryView view) {
        this.view = view;
    }

    @Override
    public void setInventory(@NotNull Inventory inventory) {
        this.inventory = inventory;
    }

    @Nullable
    public InventoryView getInventoryView() {
        return view;
    }

    @NotNull
    public Inventory getInventory() {
        if(inventory != null) {
            return inventory;
        } else if(view != null) {
            return view.getTopInventory();
        } else {
            throw new RuntimeException("Unable to get Inventory due to a null Inventory or InventoryView. Did you run createInventory?");
        }
    }

    @Override
    public void clearButtons() {
        ItemStack clearStack = new ItemStack(Material.AIR);

        if(getInventoryView() != null) {
            InventoryView view = getInventoryView();

            int guiSize = getInventory().getSize() - 1;
            for(int slot = 0; slot <= guiSize; slot++) {
                if(getButtonMapping().containsKey(slot)) {
                    view.setItem(slot, clearStack);
                }
            }
        } else {
            Inventory inventory = getInventory();
            int guiSize = getInventory().getSize() - 1;
            for(int slot = 0; slot <= guiSize; slot++) {
                if(getButtonMapping().containsKey(slot)) {
                    inventory.setItem(slot, clearStack);
                }
            }
        }

        slotButtons.clear();
    }

    @Override
    public void clear()  {
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

        slotButtons.clear();
    }

    @Override
    @NotNull
    public Map<Integer, GUIButton> getButtonMapping() {
        return slotButtons;
    }

    @Override
    public void setButton(int slot, @NotNull GUIButton button) {
        Inventory inventory = getInventory();

        if(slot < 0 || slot >= inventory.getSize()) {
            throw new RuntimeException("Provided slot is outside of inventory bounds. Slot must be greater than 0 and less than " + inventory.getSize());
        }

        slotButtons.put(slot, button);
    }

    @Override
    public void setButtons(HashMap<Integer, GUIButton> buttonMap) {
        clearButtons();

        slotButtons.putAll(buttonMap);
    }
}
