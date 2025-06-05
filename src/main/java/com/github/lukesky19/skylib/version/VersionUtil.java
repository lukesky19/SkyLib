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
