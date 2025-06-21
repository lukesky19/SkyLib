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
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.plugin.SkyLib;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages SkyLib's {@link Settings}.
 */
public class SettingsManager {
    private final @NotNull SkyLib skyLib;
    private @Nullable Settings settings;

    /**
     * Constructor
     * @param skyLib The plugin's main instance.
     */
    public SettingsManager(@NotNull SkyLib skyLib) {
        this.skyLib = skyLib;
    }

    /**
     * Get SkyLib's {@link Settings}
     * @return A {@link Settings} object.
     */
    public @Nullable Settings getSettings() {
        return settings;
    }

    /**
     * Loads SkyLib's setting.yml from the disk.
     * @return true if successful, otherwise false.
     */
    public boolean loadSettings() {
        settings = null;

        saveDefaultSettings();

        Path path = Path.of(skyLib.getDataFolder() + File.separator + "settings.yml");
        @NotNull YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            settings = loader.load().get(Settings.class);
        } catch (ConfigurateException e) {
            skyLib.getComponentLogger().error(AdventureUtil.serialize("Failed to load SkyLib's plugin settings. " + e.getMessage()));
            return false;
        }

        return validateConfig();
    }

    /**
     * Saves the provided {@link Settings} object to the settings.yml file on the disk.
     * @param settings The {@link Settings} to save.
     * @return true if successful, otherwise false.
     */
    public boolean saveSettings(@NotNull Settings settings) {
        Path path = Path.of(skyLib.getDataFolder() + File.separator + "settings.yml");
        @NotNull YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            CommentedConfigurationNode node = loader.createNode();
            node.set(Settings.class, settings);
            loader.save(node);

            this.settings = settings;
            return true;
        } catch (ConfigurateException e) {
            skyLib.getComponentLogger().error(AdventureUtil.serialize("Failed to save SkyLib's plugin settings. " + e.getMessage()));
            return false;
        }
    }

    /**
     * Saves the default settings bundled with the  plugin to the disk. Will not overwrite any existing files.
     */
    public void saveDefaultSettings() {
        Path path = Path.of(skyLib.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skyLib.saveResource("settings.yml", false);
        }
    }

    /**
     * Validates the loaded {@link Settings}. Will set {@link #settings} to null if the loaded {@link Settings} are invalid.
     * @return true if valid, otherwise false.
     */
    public boolean validateConfig() {
        if(settings == null) return false;
        ComponentLogger logger = skyLib.getComponentLogger();

        if(settings.corePoolSize() < 0) {
            logger.error(AdventureUtil.serialize("The core pool size must be greater than or equal to 0."));
            return false;
        }

        if(settings.maxPoolSize() < 0) {
            logger.error(AdventureUtil.serialize("The max pool size must be greater than or equal to 0."));
            return false;
        }

        if(settings.timeoutTimeSeconds() < 0) {
            logger.error(AdventureUtil.serialize("The timeout time must be greater than or equal to 0."));
            return false;
        }

        return true;
    }
}
