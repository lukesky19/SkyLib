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
package com.github.lukesky19.skylib.velocity;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import com.github.lukesky19.skylib.common.api.version.VersionUtil;
import com.github.lukesky19.skylib.common.platform.PlatformUtils;
import com.github.lukesky19.skylib.common.settings.Settings;
import com.github.lukesky19.skylib.common.settings.SettingsManager;
import com.github.lukesky19.skylib.common.threading.ThreadPoolManager;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.ProxyVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import xyz.jpenilla.gremlin.runtime.DependencyCache;
import xyz.jpenilla.gremlin.runtime.DependencyResolver;
import xyz.jpenilla.gremlin.runtime.DependencySet;
import xyz.jpenilla.gremlin.runtime.logging.Slf4jGremlinLogger;
import xyz.jpenilla.gremlin.runtime.platformsupport.VelocityClasspathAppender;

import java.io.File;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

/**
 * The entry point to the velocity plugin.
 */
@Plugin(
        id = "skylib",
        name = "SkyLib",
        version = "2.0.0.0",
        authors = {"lukeskywlker19"}
)
public class SkyLibVelocity implements ISkyPlugin {
    private final @NonNull ProxyServer server;
    private final @NonNull ComponentLogger logger;
    private final @NonNull Path dataDirectory;

    /**
     * Constructor
     * @param server A {@link ProxyServer}.
     * @param logger A {@link ComponentLogger}.
     * @param dataDirectory The plugin's {@link Path} to the data directory.
     */
    @Inject
    public SkyLibVelocity(
            @NonNull ProxyServer server,
            @NonNull ComponentLogger logger,
            @DataDirectory @NonNull Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    /**
     * Listens for when the proxy is initialized to setup the plugin.
     * @param proxyInitializeEvent A {@link ProxyInitializeEvent}.
     */
    @Subscribe
    public void onProxyInitialized(ProxyInitializeEvent proxyInitializeEvent) {
        // Log a message that the plugin is being initialized
        logger.info(AdventureUtility.plain("Initializing SkyLib"));

        // Download dependencies and add to classpath.
        DependencySet deps = DependencySet.readDefault(this.getClass().getClassLoader());
        VelocityClasspathAppender loader = new VelocityClasspathAppender(server, this);
        DependencyCache cache = new DependencyCache(Path.of(this.getDirectoryFile() + File.separator + "libraries"));
        try(DependencyResolver downloader = new DependencyResolver(new Slf4jGremlinLogger(logger))) {
            Set<Path> jars = downloader.resolve(deps, cache).jarFiles();
            jars.forEach(loader::append);
        }
        cache.cleanup();

        // Register sqlite driver.
        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Initialize the serializers for the platform
        // This just builds an empty collection as Velocity has no platform serializers.
        PlatformUtils.init(TypeSerializerCollection.builder().build());

        // Get Minecraft version
        ProxyVersion proxyVersion = server.getVersion();
        String minecraftVersionId = proxyVersion.getVersion();

        // Parse Minecraft Version
        VersionUtil.parseVersion(minecraftVersionId);

        // Load plugin settings and disable SkyLib if plugin settings fail to load.
        SettingsManager settingsManager = new SettingsManager(this);
        settingsManager.loadConfiguration();
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(Component.text("Failed to load the plugin settings. The plugin will not be fully initialized."));
            return;
        }

        // Initialize thread pool
        ThreadPoolManager.initializeThreadPool(settings);
    }

    @Override
    public void reload() {}

    @Override
    public @NonNull ComponentLogger getComponentLogger() {
        return logger;
    }

    @Override
    public @NonNull File getDirectoryFile() {
        return dataDirectory.toFile();
    }

    @Override
    public @NonNull Path getDirectoryPath() {
        return dataDirectory;
    }
}