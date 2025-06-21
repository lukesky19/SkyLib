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
package com.github.lukesky19.skylib.api.database.connection;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This abstract class provides a default implementation to obtain connections to a database using HikariCP.
 */
public abstract class AbstractConnectionManager {
    private final @NotNull HikariDataSource hikariDataSource;

    /**
     * Constructor that takes a {@link Plugin}.
     * @param plugin The {@link Plugin} making use of this class.
     * @throws RuntimeException If the plugin's data folder failed to be created (if it doesn't already exist).
     */
    public AbstractConnectionManager(@NotNull Plugin plugin) {
        // Ensure the plugin's data folder exists.
        if(!plugin.getDataFolder().exists()) {
            boolean result = plugin.getDataFolder().mkdirs();
            if(!result) {
                throw new RuntimeException("Failed to create plugin's folder.");
            }
        }

        hikariDataSource = createHikariDataSource(plugin);
    }

    /**
     * Gets a new {@link Connection} to the database.
     * @return A new {@link Connection} to access the database.
     * @throws RuntimeException If a new {@link Connection} is unable to be obtained.
     */
    @NotNull
    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes any active connections to the database.
     */
    public void closeConnections() {
        hikariDataSource.close();
    }

    /**
     * Allows implementations to define how to create their own HikariDataSource with any necessary configurations.
     * @param plugin The {@link Plugin} implementing and making use of this class.
     * @return A new {@link HikariDataSource} that is used to create new {@link Connection}s to a database.
     */
    @NotNull
    protected abstract HikariDataSource createHikariDataSource(@NotNull Plugin plugin);
}
