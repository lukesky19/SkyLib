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
package com.github.lukesky19.skylib.common.api.configuration.interfaces;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;

/**
 * This interface can be used to create a simple configuration manager class.
 * @param <C> The configuration object.
 */
@SuppressWarnings("unused") // This interface is used by this and other plugins.
public interface ISimpleConfigManager<C> {
    /**
     * Set the configuration path to save and load the configuration.
     * @param configurationPath A {@link Path}.
     */
    void setConfigurationPath(@NonNull Path configurationPath);

    /**
     * Get the configuration. May be null.
     * @return The configuration or null.
     */
    @Nullable C getConfiguration();

    /**
     * A method to reload the configuration.
     */
    void loadConfiguration();

    /**
     * Save the configuration.
     * @param configuration The configuration to save.
     */
    void saveConfiguration(@NonNull C configuration);

    /**
     * Save the default configuration.
     */
    void saveDefaultConfiguration();

    /**
     * Migrate the configuration.
     * @param configuration The configuration to migrate.
     * @return The migrated configuration or null.
     */
    @Nullable C migrateConfiguration(@NonNull C configuration);

    /**
     * Validate the configuration.
     * @return true if valid, false if not.
     */
    boolean validateConfiguration();

    /**
     * Validate the configuration.
     * @param configuration The configuration to validate.
     * @return true if valid, false if not.
     */
    boolean validateConfiguration(@Nullable C configuration);
}