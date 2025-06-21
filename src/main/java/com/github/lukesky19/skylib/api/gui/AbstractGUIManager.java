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
package com.github.lukesky19.skylib.api.gui;

import com.github.lukesky19.skylib.api.gui.interfaces.BaseGUI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class can be extended to provide a ready-to-use template for storing open GUIs.
 */
public abstract class AbstractGUIManager {
    private final @NotNull Plugin plugin;
    private final @NotNull HashMap<UUID, BaseGUI> activeGUIs = new HashMap<>();

    /**
     * Constructor
     * @param plugin The plugin extending this {@link AbstractGUIManager} class.
     */
    public AbstractGUIManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the {@link BaseGUI} that is currently open for the provided {@link UUID}.
     * @param uuid The {@link UUID} of the player.
     * @return An {@link Optional} containing either the {@link BaseGUI} or empty if that player doesn't have a GUI open.
     */
    public @NotNull Optional<@NotNull BaseGUI> getOpenGUI(@NotNull UUID uuid) {
        return Optional.ofNullable(activeGUIs.get(uuid));
    }

    /**
     * Store the {@link BaseGUI} that the player with the provided {@link UUID} has opened.
     * @param uuid The {@link UUID} of the player.
     * @param baseGUI The {@link BaseGUI} that they opened.
     */
    public void addOpenGUI(@NotNull UUID uuid, @NotNull BaseGUI baseGUI) {
        activeGUIs.put(uuid, baseGUI);
    }

    /**
     * Remove any {@link BaseGUI} mapped to the player's {@link UUID}.
     * @param uuid The {@link UUID} of the player.
     */
    public void removeOpenGUI(@NotNull UUID uuid) {
        activeGUIs.remove(uuid);
    }

    /**
     * Closes any open {@link BaseGUI}s for all players.
     * @param onDisable Whether the GUIs are being closed during plugin disable or not.
     */
    public void closeOpenGUIs(boolean onDisable) {
        @NotNull Map<UUID, BaseGUI> guiMap = new HashMap<>(activeGUIs);

        guiMap.forEach((uuid, baseGUI) -> {
            Player player = plugin.getServer().getPlayer(uuid);
            if(player != null && player.isOnline() && player.isConnected()) {
                baseGUI.unload(onDisable);
            }
        });
    }
}
