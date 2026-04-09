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
package com.github.lukesky19.skylib.paper;

import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import xyz.jpenilla.gremlin.runtime.DependencyCache;
import xyz.jpenilla.gremlin.runtime.DependencyResolver;
import xyz.jpenilla.gremlin.runtime.DependencySet;
import xyz.jpenilla.gremlin.runtime.logging.Slf4jGremlinLogger;

import java.nio.file.Path;
import java.util.Set;

/**
 * The plugin loader for SkyLib downloads and adds dependencies to the plugin's classpath at runtime.
 */
@SuppressWarnings("unused") // Entry point for the paper plugin.
public class SkyLibLoader implements PluginLoader {
    /**
     * Constructor
     */
    public SkyLibLoader() {}

    /**
     * Downloads and adds the plugin's runtime dependencies to the classpath.
     * @param pluginClasspathBuilder a mutable classpath builder that may be used to register custom runtime dependencies for the plugin the loader was registered for.
     */
    @Override
    public void classloader(@NonNull PluginClasspathBuilder pluginClasspathBuilder) {
        PluginProviderContext context = pluginClasspathBuilder.getContext();
        ComponentLogger logger = context.getLogger();

        DependencySet deps = DependencySet.readDefault(this.getClass().getClassLoader());
        DependencyCache cache = new DependencyCache(context.getDataDirectory().resolve("libraries"));

        try(DependencyResolver downloader = new DependencyResolver(new Slf4jGremlinLogger(logger))) {
            Set<Path> jars = downloader.resolve(deps, cache).jarFiles();
            jars.forEach(path -> pluginClasspathBuilder.addLibrary(new JarLibrary(path)));
        }

        cache.cleanup();
    }
}