package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Takes a {@link UUID} and converts it to {@link String} for storage in a database.
 */
public class UUIDParameter implements Parameter<String> {
    private final String value;

    /**
     * Takes a {@link UUID} and converts it to a {@link String} to later use to replace a parameter with.
     * @param uuid The {@link UUID} to convert to a {@link String}.
     */
    public UUIDParameter(@NotNull UUID uuid) {
        value = uuid.toString();
    }

    /**
     * Returns the {@link String} representing a {@link UUID} to replace a parameter with.
     * @return A {@link String} representing a {@link UUID} to replace a parameter with.
     */
    @Override
    public @NotNull String getValue() {
        return value;
    }
}
