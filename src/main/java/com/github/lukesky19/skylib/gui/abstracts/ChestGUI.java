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

import com.github.lukesky19.skylib.gui.GUIButton;
import com.github.lukesky19.skylib.gui.GUIType;
import com.github.lukesky19.skylib.gui.interfaces.ButtonGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
    private InventoryView view;

    /**
     * Default Constructor.
     */
    public ChestGUI() {}

    @Override
    public void setInventoryView(@NotNull InventoryView view) {
        this.view = view;
    }

    public @NotNull InventoryView getInventoryView() {
        return view;
    }

    @Override
    public void clearButtons() {
        ItemStack clearStack = new ItemStack(Material.AIR);
        InventoryView view = getInventoryView();
        int guiSize = view.getTopInventory().getSize() - 1;

        for(int slot = 0; slot <= guiSize; slot++) {
            if(getButtonMapping().containsKey(slot)) {
                view.setItem(slot, clearStack);
            }
        }

        slotButtons.clear();
    }

    @Override
    public void clear()  {
        ItemStack clearStack = new ItemStack(Material.AIR);
        InventoryView view = getInventoryView();
        int guiSize = view.getTopInventory().getSize() - 1;

        for(int slot = 0; slot <= guiSize; slot++) {
            view.setItem(slot, clearStack);
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
        int guiSize = getInventoryView().getTopInventory().getSize();

        if(slot < 0 || slot >= guiSize) {
            throw new RuntimeException("Provided slot is outside of inventory bounds. Slot must be greater than 0 and less than " + guiSize);
        }

        slotButtons.put(slot, button);
    }

    @Override
    public void setButtons(HashMap<Integer, GUIButton> buttonMap) {
        clearButtons();

        slotButtons.putAll(buttonMap);
    }
}
