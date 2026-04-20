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
package com.github.lukesky19.skylib.paper.api.serializer;

import com.github.lukesky19.skylib.paper.api.registry.RegistryUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * Serializes and deserializes an {@link Enchantment}.
 */
public class EnchantmentSerializer implements TypeSerializer<Enchantment> {
    /**
     * Constructor
     */
    public EnchantmentSerializer() {}

    /**
     * Deserializes the {@link String} representing the {@link NamespacedKey} to a {@link Enchantment}.
     * @param type The {@link Type}.
     * @param node The {@link ConfigurationNode} to deserialize.
     * @return The {@link Enchantment} or null.
     */
    @Override
    public @Nullable Enchantment deserialize(@NonNull Type type, @NonNull ConfigurationNode node)  {
        String key = node.getString();
        if(key == null) return null;

        return RegistryUtil.getEnchantment(key).orElse(null);
    }

    /**
     * Serializes the {@link Enchantment} to it's {@link NamespacedKey} as a {@link String}.
     * @param type The {@link Type}.
     * @param enchantment The {@link Enchantment}.
     * @param node The {@link ConfigurationNode} to write to.
     * @throws SerializationException If serialization fails.
     */
    @Override
    public void serialize(@NonNull Type type, @Nullable Enchantment enchantment, @NonNull ConfigurationNode node) throws SerializationException {
        if(enchantment == null) {
            node.raw(null);
            return;
        }

        node.set(String.class, enchantment.getKey().toString());
    }
}