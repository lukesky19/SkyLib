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
package com.github.lukesky19.skylib.api.version;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that contains data about the server's Minecraft Version.
 */
public class VersionUtil {
    private static String minecraftVersion;

    private static boolean isLegacy;

    private static int year;
    private static int release;
    private static int patch;

    private static int major;
    private static int minor;

    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public VersionUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Parse the minecraft version.
     * @param minecraftVersion The Minecraft version as a {@link String}.
     */
    public static void parseVersion(@NonNull String minecraftVersion) {
        // Reset values
        year = 0;
        release = 0;
        patch = 0;
        major = 0;
        minor = 0;

        VersionUtil.minecraftVersion = minecraftVersion;

        Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
        Matcher matcher = pattern.matcher(minecraftVersion);
        if(matcher.find()) {
            int first = stringToInt(matcher.group(1));
            int second = stringToInt(matcher.group(2));
            int third = stringToInt(matcher.group(3));

            // If the first match equal 1, assume this is from before Mojang changed the version scheme
            if(first == 1) {
                isLegacy = true;
                major = second;
                minor = third;
            } else {
                isLegacy = false;
                year = first;
                release = second;
                patch = third;

                major = first;
                minor = second;
            }
        }
    }

    /**
     * Get the Minecraft version the server is running. I.e., 1.21.3.
     * @return The server's Minecraft version.
     */
    public static String getMinecraftVersion() {
        return minecraftVersion;
    }

    /**
     * Is the version the minecraft server the legacy version format?
     * @apiNote If true, you should exclusively used {@link #getMajorVersion()} and {@link #getMinorVersion()}
     * as {@link #getYear()}, {@link #getRelease()}, and {@link #getPatch()} will all be 0.
     * @return true if the legacy format, false if not.
     */
    public static boolean isLegacy() {
        return isLegacy;
    }

    /**
     * Get the year portion of the Minecraft version the server is running. I.e., 26 from 26.1
     * @apiNote Will be 0 if {@link #isLegacy()} is true.
     * @return The two digit year number. I.e., 26 for 2026.
     */
    public static int getYear() {
        return year;
    }

    /**
     * Get the release number portion of the Minecraft version the server is running. I.e., 1 from 26.1
     * @apiNote Will be 0 if {@link #isLegacy()} is true.
     * @return The release number.
     */
    public static int getRelease() {
        return release;
    }

    /**
     * Get the patch number potion of the Minecraft version the server is running. I.e., 3 from 26.1.3 (Note: made up version as of writing)
     * @apiNote Will be 0 if {@link #isLegacy()} is true.
     * @return The patch number.
     */
    public static int getPatch() {
        return patch;
    }

    /**
     * Gets the major version of the Minecraft version.
     * @apiNote This varies depending on the version. For the legacy 1.21.11 scheme, this is 21. For the 26.1.X scheme, this is 26.
     * @return The server's major version of the Minecraft version.
     */
    public static int getMajorVersion() {
        return major;
    }

    /**
     * Gets the minor version of the Minecraft version.
     * @apiNote This varies depending on the version. For the legacy 1.21.11 scheme, this is 11. For the 26.1.X scheme, this is 1.
     * @return The server's minor version of the Minecraft version.
     */
    public static int getMinorVersion() {
        return minor;
    }

    /**
     * Convert the {@link String} to an int.
     * @param string The {@link String}. May be null.
     * @return The int or 0.
     */
    private static int stringToInt(@Nullable String string) {
        if(string == null || string.isEmpty()) return 0;

        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}