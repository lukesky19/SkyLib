/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skylib;

import com.github.lukesky19.skylib.listener.LoginListener;
import com.github.lukesky19.skylib.version.VersionUtil;
import io.papermc.paper.ServerBuildInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point to the plugin.
 */
public final class SkyLib extends JavaPlugin {
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

        this.getServer().getPluginManager().registerEvents(new LoginListener(), this);
    }
}
