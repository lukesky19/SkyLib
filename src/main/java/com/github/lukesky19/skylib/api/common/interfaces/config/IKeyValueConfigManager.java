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
package com.github.lukesky19.skylib.api.common.interfaces.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * This interface can be used to create a key value config manager.
 * @param <K> The key or configuration identifier.
 * @param <V> The value or configuration object.
 */
public interface IKeyValueConfigManager<K, V> {
    /**
     * Get the configuration for the {@link K} identifier.
     * @param identifier The identifier key.
     * @return The configuration or null.
     */
    @Nullable V getConfiguration(K identifier);

    /**
     * Load all configurations from the disk.
     */
    void loadConfigurations();

    /**
     * A method to load the configuration.
     * @param identifier The key to store the loaded configuration under.
     * @param configClass The class of the configuration being loaded.
     * @param resourcePath The resource path of the default bundled configuration.
     * @param configurationPath The {@link Path} to load the configuration to from.
     */
    void loadConfiguration(@NotNull K identifier, @NotNull Class<V> configClass, @NotNull String resourcePath, @NotNull Path configurationPath);

    /**
     * Save the configuration.
     * @param configClass The class of the configuration being saved.
     * @param configurationPath The {@link Path} to save the configuration to.
     * @param configuration The configuration to save.
     */
    void saveConfiguration(@NotNull Class<V> configClass, @NotNull Path configurationPath, @NotNull V configuration);
}
