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
package com.github.lukesky19.skylib.common.api.plugin;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.nio.file.Path;

/**
 * This interface can be used to create platform-agnostic plugin.
 */
@SuppressWarnings("unused") // This interface is used by this and other plugins.
public interface ISkyPlugin {
    /**
     * Reload the plugin.
     */
    void reload();

    /**
     * Get the {@link ComponentLogger}.
     * @return The {@link ComponentLogger}.
     */
    @NonNull ComponentLogger getComponentLogger();

    /**
     * Get the {@link File} for the folder the plugin's files are stored in.
     * @apiNote The folder is not guaranteed to be created.
     * @return A {@link File}.
     */
    @NonNull File getDirectoryFile();

    /**
     * Get the {@link Path} for the folder the plugin's files are stored in.
     * @apiNote The folder is not guaranteed to be created.
     * @return A {@link Path}.
     */
    @NonNull Path getDirectoryPath();

    /**
     * Save the embeded resource.
     * @param resourcePath The resource path.
     * @param overwrite Whether to overwrite existing files or not.
     */
    default void saveResource(@NonNull String resourcePath, boolean overwrite) {
        ComponentLogger logger = getComponentLogger();

        // Open the input stream
        try(InputStream inputStream = ISkyPlugin.class.getResourceAsStream(resourcePath)) {
            // Display an error if the input stream is invalid.
            if(inputStream == null) {
                logger.error(AdventureUtility.plain("Failed to create the input stream. Resource Path: " + resourcePath));
                return;
            }

            // Create the output file
            File outputFile = new File(getDirectoryFile(), resourcePath);

            // Create the output directory
            int lastIndex = resourcePath.lastIndexOf('/');
            File outputDirectory = new File(getDirectoryFile(), resourcePath.substring(0, Math.max(lastIndex, 0)));

            // Create directories if they don't exist
            if(!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                logger.error(AdventureUtility.plain("Failed to create directories for data directory: " + outputDirectory));
                return;
            }

            // Prevent overwriting existing file
            if(!overwrite && outputFile.exists()) return;

            // Write the data to disk
            try(OutputStream out = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int length;

                while((length = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                logger.error(AdventureUtility.plain("Error saving default configuration " + outputFile.getName() + " to " + outputDirectory + ". Resource path: " + resourcePath + ". Error: " + e.getMessage()));
            }
        } catch (IOException e) {
            logger.error(AdventureUtility.plain("Error saving default configuration. Resource path: " + resourcePath + ". Error: " + e.getMessage()));
        }
    }
}