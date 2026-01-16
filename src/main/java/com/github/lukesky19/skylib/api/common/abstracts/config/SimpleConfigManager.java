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
import com.github.lukesky19.skylib.api.common.interfaces.config.ISimpleConfigManager;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

/**
 * This class can be extended to create a configuration manager class.
 * @param <C> The configuration object.
 */
public abstract class SimpleConfigManager<C> implements ISimpleConfigManager<C> {
    /**
     * The {@link SkyPlugin}.
     */
    protected final @NotNull SkyPlugin plugin;
    /**
     * The {@link ComponentLogger} of the plugin.
     */
    protected final @NotNull ComponentLogger logger;
    /**
     * The {@link Path} the configuration is loaded from and saved to.
     */
    protected @Nullable Path configurationPath;
    /**
     * The class of the configuration being loaded.
     */
    protected final @NotNull Class<C> configClass;

    /**
     * The configuration object.
     */
    protected @Nullable C configuration;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin}.
     * @param configurationPath The {@link Path} to the configuration.
     * @param configClass The {@link Class} of the {@link C} configuration object.
     */
    public SimpleConfigManager(
            @NotNull SkyPlugin plugin,
            @NotNull Path configurationPath,
            @NotNull Class<C> configClass) {
        this.plugin = plugin;
        this.logger = plugin.getComponentLogger();
        this.configurationPath = configurationPath;
        this.configClass = configClass;
    }

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin}.
     * @param configClass The {@link Class} of the {@link C} configuration object.
     */
    public SimpleConfigManager(
            @NotNull SkyPlugin plugin,
            @NotNull Class<C> configClass) {
        this.plugin = plugin;
        this.logger = plugin.getComponentLogger();
        this.configClass = configClass;
    }

    /**
     * Set the configuration path to save and load the configuration.
     * @param configurationPath A {@link Path}.
     */
    @Override
    public void setConfigurationPath(@NotNull Path configurationPath) {
        this.configurationPath = configurationPath;
    }

    /**
     * Get the configuration. May be null.
     * @return The configuration or null.
     */
    @Override
    public @Nullable C getConfiguration() {
        return configuration;
    }

    /**
     * A method to reload the configuration.
     */
    @Override
    public void loadConfiguration() {
        configuration = null;
        if(configurationPath == null) {
            logger.error(AdventureUtil.deserialize("Unable to load configuration because the configuration path was not set."));
            return;
        }

        if(!configurationPath.toFile().exists()) {
            saveBundledConfig();
        }

        YamlConfigurationLoader yamlConfigurationLoader = ConfigurationUtility.getYamlConfigurationLoader(configurationPath);
        try {
            configuration = yamlConfigurationLoader.load().get(configClass);
            if(configuration == null) {
                logger.warn(AdventureUtil.deserialize("Failed to load configuration. Class name: " + this.getClass().getName()));
                return;
            }
            @NotNull C preMigrationConfiguration = configuration;

            // Migrate configuration
            configuration = migrateConfiguration(configuration);
            // If migration failed, return
            if(configuration == null) {
                logger.warn(AdventureUtil.deserialize("Migrated configuration is invalid. Class name: " + this.getClass().getName()));
                return;
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(configuration)) {
                logger.warn(AdventureUtil.deserialize("Configuration validation failed. Class name: " + this.getClass().getName()));
                configuration = null;
                return;
            }

            // Save the migrated configuration if different
            if(configuration != preMigrationConfiguration) {
                saveConfiguration(configuration);
            }
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtil.deserialize("Failed to load configuration. Error: " + configurateException.getMessage()));
        }
    }

    /**
     * Save the configuration.
     */
    @Override
    public void saveConfiguration(@NotNull C configuration) {
        if(configurationPath == null) {
            logger.error(AdventureUtil.deserialize("Unable to save the configuration because the configuration path was not set."));
            return;
        }

        try {
            @NotNull YamlConfigurationLoader yamlConfigurationLoader = ConfigurationUtility.getYamlConfigurationLoader(configurationPath);

            ConfigurationNode node = yamlConfigurationLoader.createNode();

            node.set(configClass, configuration);

            yamlConfigurationLoader.save(node);
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.deserialize("Failed to save settings config file. Error: " + e.getMessage()));
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
    public abstract @Nullable C migrateConfiguration(@NotNull C configuration);

    /**
     * Validate the configuration in the class.
     * This is a convenience method and calls {@link #validateConfiguration(Object)} which should be favored instead.
     * @return true if valid, or false.
     */
    public boolean validateConfiguration() {
        return validateConfiguration(configuration);
    }

    /**
     * Validate the configuration.
     * @param configuration The configuration to validate.
     * @return true if valid, or false.
     */
    public abstract boolean validateConfiguration(@Nullable C configuration);
}
