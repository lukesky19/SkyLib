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
package com.github.lukesky19.skylib.plugin;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.github.lukesky19.skylib.api.version.VersionUtil;
import com.github.lukesky19.skylib.internal.ThreadPoolManager;
import com.github.lukesky19.skylib.plugin.command.SkyLibCommand;
import com.github.lukesky19.skylib.plugin.listener.LoginListener;
import com.github.lukesky19.skylib.plugin.settings.Settings;
import com.github.lukesky19.skylib.plugin.settings.SettingsManager;
import io.papermc.paper.ServerBuildInfo;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entry point to the plugin.
 */
public final class SkyLib extends SkyPlugin {
    /**
     * This is the entry point to the plugin.
     */
    public SkyLib() {}

    /**
     * Reload the plugin.
     */
    @Override
    public void reload() {}

    @Override
    public void onEnable() {
        ComponentLogger logger = this.getComponentLogger();

        // Get ServerBuildInfo and Minecraft version
        ServerBuildInfo build = ServerBuildInfo.buildInfo();
        String minecraftVersionId = build.minecraftVersionId();

        // Store Minecraft Version
        VersionUtil.setMinecraftVersion(minecraftVersionId);

        // Attempt to parse the Minecraft version
        Pattern pattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(?:-.*)?$");
        Matcher matcher = pattern.matcher(minecraftVersionId);
        if(matcher.find()) {
            String major = matcher.group(2);
            String minor = matcher.group(3);

            // Store Minecraft Major and Minor Versions
            VersionUtil.setMajorVersion(Integer.parseInt(major));
            VersionUtil.setMinorVersion(Integer.parseInt(minor));
        } else {
            logger.error(AdventureUtil.deserialize("SkyLib was unable to parse your server's Minecraft version. Unrecognized version pattern: " + minecraftVersionId + "."));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Ensure SkyLib is running on Minecraft version 1.21.4 or newer.
        if(VersionUtil.getMajorVersion() < 21 || VersionUtil.getMinorVersion() < 4) {
            this.getComponentLogger().error(AdventureUtil.deserialize("SkyLib version 1.3.0.0 and newer only works on Minecraft Version 1.21.4 and newer."));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands ->
                        commands.registrar().register(new SkyLibCommand().createCommand(),
                                "Command to manage and use the SkyLib plugin.",
                                List.of("library", "lib")));

        // Register Listener(s)
        this.getServer().getPluginManager().registerEvents(new LoginListener(), this);

        // Load plugin settings and disable SkyLib if plugin settings fail to load.
        SettingsManager settingsManager = new SettingsManager(this);
        settingsManager.loadConfiguration();
        @Nullable Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize the ScheduledThreadPoolExecutor in ThreadPoolManager
        ThreadPoolManager.initializeThreadPool(settings);
    }

    @Override
    public void onDisable() {
        ThreadPoolManager.shutdownExecutorService();
    }
}
