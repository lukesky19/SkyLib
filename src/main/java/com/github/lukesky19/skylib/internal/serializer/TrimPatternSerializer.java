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
package com.github.lukesky19.skylib.internal.serializer;

import com.github.lukesky19.skylib.api.registry.RegistryUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Serializes and deserializes {@link TrimPattern}.
 */
public class TrimPatternSerializer implements TypeSerializer<TrimPattern> {
    /**
     * Constructor
     */
    public TrimPatternSerializer() {}

    /**
     * Deserializes the {@link String} representing the {@link NamespacedKey} to a {@link TrimPattern}.
     * @param type The {@link Type}.
     * @param node The {@link ConfigurationNode} to deserialize.
     * @return The {@link TrimPattern} or null.
     * @throws SerializationException If serialization fails.
     */
    @Override
    public @Nullable TrimPattern deserialize(@NonNull Type type, @NonNull ConfigurationNode node) throws SerializationException {
        @Nullable String key = node.getString();
        if(key == null) return null;

        return RegistryUtil.getTrimPattern(key).orElse(null);
    }

    /**
     * Serializes the {@link TrimPattern} to it's {@link NamespacedKey} as a {@link String}.
     * @param type The {@link Type}.
     * @param trimPattern The {@link TrimPattern}.
     * @param node The {@link ConfigurationNode} to write to.
     * @throws SerializationException If serialization fails.
     */
    @Override
    public void serialize(@NonNull Type type, @Nullable TrimPattern trimPattern, @NonNull ConfigurationNode node) throws SerializationException {
        if(trimPattern == null) {
            node.raw(null);
            return;
        }
        @Nullable NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKey(trimPattern);
        if(key == null) {
            node.raw(null);
            return;
        }

        node.set(String.class, key.toString());
    }
}