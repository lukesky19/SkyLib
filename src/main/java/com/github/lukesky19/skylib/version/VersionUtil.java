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
package com.github.lukesky19.skylib.version;

import org.jetbrains.annotations.NotNull;

/**
 * Class that contains data about the server's Minecraft Version.
 */
public class VersionUtil {
    private static String minecraftVersion;
    private static int majorVersion;
    private static int minorVersion;

    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public VersionUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Get the Minecraft version the server is running. I.e., 1.21.3.
     * @return The server's Minecraft version.
     */
    public static String getMinecraftVersion() {
        return minecraftVersion;
    }

    /**
     * Gets the major version of the Minecraft version. I.e., 21 in 1.21.3.
     * @return The server's major version of the Minecraft version.
     */
    public static int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Gets the minor version of the Minecraft version. I.e., 3 in 1.21.3.
     * @return The server's minor version of the Minecraft version.
     */
    public static int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Sets the Minecraft version the server is running.
     * @param minecraftVersion The server's Minecraft version.
     */
    public static void setMinecraftVersion(@NotNull String minecraftVersion) {
        VersionUtil.minecraftVersion = minecraftVersion;
    }

    /**
     * Sets the major version of the Minecraft version.
     * @param majorVersion The server's major Minecraft version. I.e., 21 in 1.21.3.
     */
    public static void setMajorVersion(int majorVersion) {
        VersionUtil.majorVersion = majorVersion;
    }

    /**
     * Sets the minor version of the Minecraft version.
     * @param minorVersion The server's minor Minecraft version. I.e., 3 in 1.21.3.
     */
    public static void setMinorVersion(int minorVersion) {
        VersionUtil.minorVersion = minorVersion;
    }
}
