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
package com.github.lukesky19.skylib.api.common.abstracts.config;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.github.lukesky19.skylib.api.common.abstracts.data.HashMapDataManager;
import com.github.lukesky19.skylib.api.common.interfaces.config.IKeyValueConfigManager;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

/**
 * This class can be extended to create a configuration manager class.
 * @param <K> The key or configuration identifier.
 * @param <V> The value or configuration object.
 */
public abstract class KeyValueConfigManager<K, V> extends HashMapDataManager<K, V> implements IKeyValueConfigManager<K, V> {
    /**
     * The {@link SkyPlugin}.
     */
    protected final @NonNull SkyPlugin plugin;
    /**
     * The {@link ComponentLogger} of the plugin.
     */
    protected final @NonNull ComponentLogger logger;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin}.
     */
    public KeyValueConfigManager(@NonNull SkyPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getComponentLogger();
    }

    /**
     * Get the configuration for the {@link K} identifier.
     * @param identifier The identifier key.
     * @return The configuration or null.
     */
    @Override
    public @Nullable V getConfiguration(K identifier) {
        return getData(identifier);
    }

    /**
     * Load all configurations from the disk.
     */
    @Override
    public abstract void loadConfigurations();

    /**
     * A method to load the configuration.
     * @param identifier The key to store the loaded configuration under.
     * @param configClass The class of the configuration being loaded.
     * @param configurationPath The {@link Path} to load the configuration to from.
     */
    @Override
    public void loadConfiguration(@NonNull K identifier, @NonNull Class<V> configClass, @NonNull Path configurationPath) {
        @Nullable V configuration;

        YamlConfigurationLoader yamlConfigurationLoader = ConfigurationUtility.getYamlConfigurationLoader(configurationPath);
        try {
            configuration = yamlConfigurationLoader.load().get(configClass);
            if(configuration == null) {
                logger.warn(AdventureUtil.deserialize("Failed to load configuration. Class name: " + this.getClass().getName()));
                return;
            }

            // Migrate configuration
            @Nullable V migratedConfiguration = migrateConfiguration(configuration);
            // If migration failed, return
            if(migratedConfiguration == null) {
                logger.warn(AdventureUtil.deserialize("Migrated configuration is invalid. Class name: " + this.getClass().getName()));
                return;
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(configuration)) {
                logger.warn(AdventureUtil.deserialize("Configuration validation failed. Class name: " + this.getClass().getName()));
                return;
            }

            // Save the migrated configuration if different
            if(configuration != migratedConfiguration) {
                saveConfiguration(configClass, configurationPath, migratedConfiguration);
            }

            // Store the configuration
            setData(identifier, migratedConfiguration);
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtil.deserialize("Failed to load the configuration. Error: " + configurateException.getMessage()));
        }
    }

    /**
     * Save the configuration.
     * @param configClass The class of the configuration being saved.
     * @param configurationPath The {@link Path} to save the configuration to.
     * @param configuration The configuration to save.
     */
    @Override
    public void saveConfiguration(@NonNull Class<V> configClass, @NonNull Path configurationPath, @NonNull V configuration) {
        try {
            @NonNull YamlConfigurationLoader yamlConfigurationLoader = ConfigurationUtility.getYamlConfigurationLoader(configurationPath);

            ConfigurationNode node = yamlConfigurationLoader.createNode();

            node.set(configClass, configuration);

            yamlConfigurationLoader.save(node);
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtil.deserialize("Failed to save configuration. Error: " + configurateException.getMessage()));
        }
    }

    /**
     * Save the bundled configuration file(s).
     */
    protected abstract void saveBundledConfig();

    /**
     * Migrate the configuration.
     * @param configuration The configuration to migrate.
     * @return V the migrated configuration.
     */
    protected abstract @Nullable V migrateConfiguration(@NonNull V configuration);

    /**
     * Validate the configuration.
     * @param configuration The configuration to validate.
     * @return true if valid, or false.
     */
    protected abstract boolean validateConfiguration(@NonNull V configuration);
}
