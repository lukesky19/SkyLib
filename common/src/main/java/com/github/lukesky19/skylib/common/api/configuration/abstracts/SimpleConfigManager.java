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
package com.github.lukesky19.skylib.common.api.configuration.abstracts;

import com.github.lukesky19.skylib.common.api.configuration.interfaces.ISimpleConfigManager;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.common.platform.PlatformUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;

/**
 * This class can be extended to create a configuration manager class.
 * @apiNote This class use {@link YamlConfigurationLoader} by default.
 * @param <C> The configuration object.
 */
@SuppressWarnings("unused") // This class is used by this and other plugins.
public abstract class SimpleConfigManager<C> implements ISimpleConfigManager<C> {
    /**
     * The {@link ISkyPlugin}.
     */
    protected final @NonNull ISkyPlugin plugin;
    /**
     * The {@link ComponentLogger} of the plugin.
     */
    protected final @NonNull ComponentLogger logger;
    /**
     * The {@link Path} the configuration is loaded from and saved to.
     */
    protected @Nullable Path configurationPath;
    /**
     * The class of the configuration being loaded.
     */
    protected final @NonNull Class<C> configClass;

    /**
     * The configuration object.
     */
    protected @Nullable C configuration;

    /**
     * Constructor
     * @param plugin A {@link ISkyPlugin}.
     * @param configurationPath The {@link Path} to the configuration.
     * @param configClass The {@link Class} of the {@link C} configuration object.
     */
    public SimpleConfigManager(
            @NonNull ISkyPlugin plugin,
            @NonNull Path configurationPath,
            @NonNull Class<C> configClass) {
        this.plugin = plugin;
        this.logger = plugin.getComponentLogger();
        this.configurationPath = configurationPath;
        this.configClass = configClass;
    }

    /**
     * Constructor
     * @param plugin A {@link ISkyPlugin}.
     * @param configClass The {@link Class} of the {@link C} configuration object.
     */
    public SimpleConfigManager(
            @NonNull ISkyPlugin plugin,
            @NonNull Class<C> configClass) {
        this.plugin = plugin;
        this.logger = plugin.getComponentLogger();
        this.configClass = configClass;
    }

    /**
     * Set the configuration path to save and load the configuration.
     * @param configurationPath A {@link Path}.
     */
    @Override
    public void setConfigurationPath(@NonNull Path configurationPath) {
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
            logger.error("Unable to load configuration because the configuration path was not set.");
            return;
        }

        if(!configurationPath.toFile().exists()) {
            saveDefaultConfiguration();
        }

        YamlConfigurationLoader loader = createLoader(configurationPath);
        try {
            configuration = loader.load().get(configClass);
            if(configuration == null) {
                logger.warn(Component.text("Failed to load configuration. Class name: " + this.getClass().getName()));
                return;
            }
            C preMigrationConfiguration = configuration;

            // Migrate configuration
            configuration = migrateConfiguration(configuration);
            // If migration failed, return
            if(configuration == null) {
                logger.warn(Component.text("Migrated configuration is invalid. Class name: " + this.getClass().getName()));
                return;
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(configuration)) {
                logger.warn(Component.text("Configuration validation failed. Class name: " + this.getClass().getName()));
                configuration = null;
                return;
            }

            // Save the migrated configuration if different
            if(configuration != preMigrationConfiguration) {
                saveConfiguration(configuration);
            }
        } catch (ConfigurateException configurateException) {
            logger.error(Component.text("Failed to load configuration for configuration file " + configurationPath + ". Error: " + configurateException.getMessage()));
        }
    }

    /**
     * Save the configuration.
     */
    @Override
    public void saveConfiguration(@NonNull C configuration) {
        if(configurationPath == null) {
            logger.error(Component.text("Unable to save the configuration because the configuration path was not set."));
            return;
        }

        try {
            YamlConfigurationLoader loader = createLoader(configurationPath);

            ConfigurationNode node = loader.createNode();

            node.set(configClass, configuration);

            loader.save(node);
        } catch (ConfigurateException e) {
            logger.error(Component.text("Failed to save configuration. Error: " + e.getMessage()));
        }
    }

    /**
     * Save the default configuration.
     */
    @Override
    public abstract void saveDefaultConfiguration();

    /**
     * Migrate the configuration.
     * @param configuration The configuration to migrate.
     * @return The migrated configuration.
     */
    @Override
    public abstract @Nullable C migrateConfiguration(@NonNull C configuration);

    /**
     * Validate the configuration in the class.
     * This is a convenience method and calls {@link #validateConfiguration(Object)} which should be favored instead.
     * @return true if valid, or false.
     */
    @Override
    public boolean validateConfiguration() {
        return validateConfiguration(configuration);
    }

    /**
     * Validate the configuration.
     * @param configuration The configuration to validate.
     * @return true if valid, or false.
     */
    @Override
    public abstract boolean validateConfiguration(@Nullable C configuration);

    /**
     * Create the {@link YamlConfigurationLoader} for the path provided.
     * @apiNote {@link PlatformUtils#getSerializers()} are included by default.
     * @param path The {@link Path}.
     * @return The {@link YamlConfigurationLoader}.
     */
    protected @NonNull YamlConfigurationLoader createLoader(@NonNull Path path) {
        return YamlConfigurationLoader.builder()
                .path(path)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(4)
                .defaultOptions(configurationOptions ->
                        configurationOptions.serializers(builder ->
                                builder.registerAll(PlatformUtils.getSerializers())))
                .build();
    }

    /**
     * Create the {@link YamlConfigurationLoader} for the path provided.
     * @param path The {@link Path}.
     * @param collection The {@link TypeSerializerCollection}.
     * @return The {@link YamlConfigurationLoader}.
     */
    protected @NonNull YamlConfigurationLoader createLoader(@NonNull Path path, @NonNull TypeSerializerCollection collection) {
        return YamlConfigurationLoader.builder()
                .path(path)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(4)
                .defaultOptions(configurationOptions ->
                        configurationOptions.serializers(builder -> {
                            builder.registerAll(PlatformUtils.getSerializers());
                            builder.registerAll(collection);
                        }))
                .build();
    }
}