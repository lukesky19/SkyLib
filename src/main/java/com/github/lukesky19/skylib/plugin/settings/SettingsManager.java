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
package com.github.lukesky19.skylib.plugin.settings;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.config.SimpleConfigManager;
import com.github.lukesky19.skylib.plugin.SkyLib;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages SkyLib's {@link Settings}.
 */
public class SettingsManager extends SimpleConfigManager<Settings> {
    /**
     * Constructor
     * @param skyLib The plugin's main instance.
     */
    public SettingsManager(@NotNull SkyLib skyLib) {
        super(skyLib, Path.of(skyLib.getDataFolder() + File.separator + "settings.yml"), Settings.class);
    }

    @Override
    public void saveBundledConfig() {
        plugin.saveResource("settings.yml", false);
    }

    @Override
    public @NotNull Settings migrateConfiguration(@NotNull Settings configuration) {
        return configuration;
    }

    /**
     * Validates the provided {@link Settings}.
     * @param settings The {@link Settings} to validate.
     * @return true if valid, otherwise false.
     */
    @Override
    public boolean validateConfiguration(@Nullable Settings settings) {
        if(settings == null) return false;

        if(settings.corePoolSize() < 0) {
            logger.error(AdventureUtil.deserialize("The core pool size must be greater than or equal to 0."));
            return false;
        }

        if(settings.maxPoolSize() < 0) {
            logger.error(AdventureUtil.deserialize("The max pool size must be greater than or equal to 0."));
            return false;
        }

        if(settings.timeoutTimeSeconds() < 0) {
            logger.error(AdventureUtil.deserialize("The timeout time must be greater than or equal to 0."));
            return false;
        }

        return true;
    }
}
