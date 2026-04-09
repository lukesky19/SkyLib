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

import com.github.lukesky19.skylib.common.api.configuration.interfaces.IKeyValueConfigManager;
import com.github.lukesky19.skylib.common.api.data.abstracts.HashMapDataManager;
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
 * @param <K> The key or configuration identifier.
 * @param <V> The value or configuration object.
 */
@SuppressWarnings("unused") // This class is used by this and other plugins.
public abstract class KeyValueConfigManager<K, V> extends HashMapDataManager<K, V> implements IKeyValueConfigManager<K, V> {
    /**
     * The {@link ISkyPlugin}.
     */
    protected final @NonNull ISkyPlugin plugin;
    /**
     * The {@link ComponentLogger} of the plugin.
     */
    protected final @NonNull ComponentLogger logger;

    /**
     * Constructor
     * @param plugin An {@link ISkyPlugin}.
     */
    public KeyValueConfigManager(@NonNull ISkyPlugin plugin) {
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
        V configuration;

        YamlConfigurationLoader loader = createLoader(configurationPath);
        try {
            configuration = loader.load().get(configClass);
            if(configuration == null) {
                logger.warn(Component.text("Failed to load configuration. Class name: " + this.getClass().getName()));
                return;
            }

            // Migrate configuration
            V migratedConfiguration = migrateConfiguration(configuration);
            // If migration failed, return
            if(migratedConfiguration == null) {
                logger.warn(Component.text("Migrated configuration is invalid. Class name: " + this.getClass().getName()));
                return;
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(configuration)) {
                logger.warn(Component.text("Configuration validation failed. Class name: " + this.getClass().getName()));
                return;
            }

            // Save the migrated configuration if different
            if(configuration != migratedConfiguration) {
                saveConfiguration(configClass, configurationPath, migratedConfiguration);
            }

            // Store the configuration
            setData(identifier, migratedConfiguration);
        } catch (ConfigurateException configurateException) {
            logger.error(Component.text("Failed to load the configuration. Error: " + configurateException.getMessage()));
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
            YamlConfigurationLoader loader = createLoader(configurationPath);

            ConfigurationNode node = loader.createNode();

            node.set(configClass, configuration);

            loader.save(node);
        } catch (ConfigurateException configurateException) {
            logger.error(Component.text("Failed to save configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    public abstract void saveDefaultConfiguration();

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
     * @apiNote {@link PlatformUtils#getSerializers()} are included by default.
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