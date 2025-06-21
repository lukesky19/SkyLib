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
package com.github.lukesky19.skylib.api.configurate;

import java.nio.file.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

/**
 * Class that contains utilities for using Configurate to load configuration files.
 */
public class ConfigurationUtility {
    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public ConfigurationUtility() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Get a {@link YamlConfigurationLoader} object for the given {@link Path}.
     * @param path A {@link Path} to a file.
     * @return A {@link YamlConfigurationLoader} for the given {@link Path}
     */
    @Contract("_ -> new")
    public static @NotNull YamlConfigurationLoader getYamlConfigurationLoader(@NotNull Path path) {
        return YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(path)
                .indent(4)
                .build();
    }

    /**
     * Get a {@link GsonConfigurationLoader} object for the given {@link Path}.
     * @param path A {@link Path} to a file.
     * @return A {@link GsonConfigurationLoader} for the given {@link Path}
     */
    @Contract("_ -> new")
    public static @NotNull GsonConfigurationLoader getGsonConfigurationLoader(@NotNull Path path) {
        return GsonConfigurationLoader.builder()
                .path(path)
                .indent(4)
                .build();
    }
}
