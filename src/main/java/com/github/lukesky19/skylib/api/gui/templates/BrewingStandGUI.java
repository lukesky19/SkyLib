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
package com.github.lukesky19.skylib.api.gui.templates;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.gui.abstracts.ButtonGUI;
import com.github.lukesky19.skylib.api.gui.GUIType;
import com.github.lukesky19.skylib.api.gui.interfaces.IGUIManager;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class can be extended to create a brewing stand-style GUI. Provides some default functions to assist.
 * @param <I> The identifier that this GUI is tied to. Used in conjunction with {@link IGUIManager}.
 */
public abstract class BrewingStandGUI<I> extends ButtonGUI<I> {
    /**
     * Constructor.
     * @param plugin The {@link JavaPlugin} creating the GUI.
     * @param guiManager An {@link IGUIManager} that is used to track open GUIs.
     * @param identifier The identifier that the GUI is tied to. Used in conjunction with {@link IGUIManager}.
     * @param player The {@link Player} associated with the created GUI.
     */
    public BrewingStandGUI(@NotNull JavaPlugin plugin, @NotNull IGUIManager<I> guiManager, @NotNull I identifier, @NotNull Player player) {
        super(plugin, guiManager, identifier, player);
    }

    /**
     * Create the {@link InventoryView} for this GUI.
     * @param guiType The {@link GUIType} for this GUI. Only {@link GUIType#BREWING_STAND} is allowed.
     * @param name The name of the GUI to display in the InventoryView.
     * @param placeholders A {@link List} of {@link TagResolver.Single} for any placeholders in the GUI name.
     * @return true if created successfully, otherwise false.
     */
    @Override
    public boolean create(@NotNull GUIType guiType, @NotNull String name, @NotNull List<TagResolver.Single> placeholders) {
        if(guiType != GUIType.BREWING_STAND) {
            logger.warn(AdventureUtil.deserialize("Unsupported GUIType provided."));
            return false;
        }

        return super.create(guiType, name, placeholders);
    }
}
