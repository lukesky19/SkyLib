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
package com.github.lukesky19.skylib;

import com.github.lukesky19.skylib.adventure.AdventureUtil;
import com.github.lukesky19.skylib.listener.LoginListener;
import com.github.lukesky19.skylib.version.VersionUtil;
import io.papermc.paper.ServerBuildInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point to the plugin.
 */
public final class SkyLib extends JavaPlugin {
    /**
     * This is the entry point to the plugin.
     */
    public SkyLib() {}

    @Override
    public void onEnable() {
        // Get ServerBuildInfo and Minecraft version
        final ServerBuildInfo build = ServerBuildInfo.buildInfo();
        final String minecraftVersionId = build.minecraftVersionId();

        // Store Minecraft Version
        VersionUtil.setMinecraftVersion(minecraftVersionId);

        // Parse Minecraft Version for major and minor
        final @NotNull String[] splitVersion = minecraftVersionId.split("\\.");

        // Store Minecraft Major and Minor Versions
        VersionUtil.setMajorVersion(Integer.parseInt(splitVersion[1]));
        VersionUtil.setMinorVersion(Integer.parseInt(splitVersion[2]));

        // Ensure SkyLib is running on Minecraft version 1.21.4 or newer.
        if(VersionUtil.getMajorVersion() < 21 || VersionUtil.getMinorVersion() < 4) {
            this.getComponentLogger().error(AdventureUtil.serialize("SkyLib version 1.3.0.0 and newer only works on Minecraft Version 1.21.4 and newer."));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register Listener(s)
        this.getServer().getPluginManager().registerEvents(new LoginListener(), this);
    }
}
