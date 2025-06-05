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
package com.github.lukesky19.skylib.configurate;

import java.nio.file.Path;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
     * Get a YamlConfigurationLoader object for a given path
     * @param path A Path
     * @return A YamlConfigurationLoader for the given Path
     */
    @Contract("_ -> new")
    @NotNull
    public static YamlConfigurationLoader getYamlConfigurationLoader(@NotNull Path path) {
        return YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .path(path)
                .indent(4)
                .build();
    }
}
