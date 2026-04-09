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
package com.github.lukesky19.skylib.paper;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.version.VersionUtil;
import com.github.lukesky19.skylib.common.settings.Settings;
import com.github.lukesky19.skylib.common.settings.SettingsManager;
import com.github.lukesky19.skylib.common.threading.ThreadPoolManager;
import com.github.lukesky19.skylib.paper.api.player.PlayerUtil;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import com.github.lukesky19.skylib.paper.command.SkyLibCommand;
import io.papermc.paper.ServerBuildInfo;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Entry point to the plugin.
 */
public final class SkyLibPaper extends SkyPlugin {
    /**
     * This is the entry point to the plugin.
     */
    public SkyLibPaper() {}

    @Override
    public @NonNull ComponentLogger getComponentLogger() {
        return super.getComponentLogger();
    }

    @Override
    public void onEnable() {
        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Get ServerBuildInfo and Minecraft version
        ServerBuildInfo build = ServerBuildInfo.buildInfo();
        String minecraftVersionId = build.minecraftVersionId();

        // Parse Minecraft Version
        VersionUtil.parseVersion(minecraftVersionId);

        // Ensure SkyLib is running on Minecraft version 1.21.4 or newer.
        if(VersionUtil.isLegacy()) {
            if(VersionUtil.getMajorVersion() < 21 || VersionUtil.getMinorVersion() < 4) {
                this.getComponentLogger().error(AdventureUtility.plain("SkyLib version 1.3.0.0 and newer only works on Minecraft Version 1.21.4 and newer."));
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        // Load plugin settings and disable SkyLib if plugin settings fail to load.
        SettingsManager settingsManager = new SettingsManager(this);
        settingsManager.loadConfiguration();
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ThreadPoolManager.initializeThreadPool(settings);

        PlayerUtil.init(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands ->
                        commands.registrar().register(new SkyLibCommand().createCommand(),
                                "Command to manage and use the SkyLib plugin.",
                                List.of("library", "lib")));
    }

    /**
     * Reload the plugin.
     */
    @Override
    public void reload() {}

    @Override
    public void onDisable() {
        ThreadPoolManager.shutdownExecutorService();
    }
}