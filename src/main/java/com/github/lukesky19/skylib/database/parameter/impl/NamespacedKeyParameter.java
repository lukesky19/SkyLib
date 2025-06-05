package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Takes a {@link NamespacedKey} and converts to a string for storage in a database.
 */
public class NamespacedKeyParameter implements Parameter<String> {
    private final String value;

    /**
     * Takes a {@link NamespacedKey} and converts it to a {@link String} to later use to replace a parameter with.
     * @param namespacedKey The {@link NamespacedKey} to convert to a {@link String}.
     */
    public NamespacedKeyParameter(@NotNull NamespacedKey namespacedKey) {
        value = namespacedKey.toString();
    }

    /**
     * Returns the {@link String} representing a {@link NamespacedKey} to replace a parameter with.
     * @return A {@link String} representing a {@link NamespacedKey} to replace a parameter with.
     */
    @Override
    public @NotNull String getValue() {
        return value;
    }
}
