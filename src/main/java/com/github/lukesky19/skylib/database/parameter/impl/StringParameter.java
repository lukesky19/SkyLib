package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;
import org.jetbrains.annotations.NotNull;

/**
 * Takes a {@link String} and converts it to lowercase for storage in a database.
 */
public class StringParameter implements Parameter<String> {
    private final String value;

    /**
     * Stores a {@link String} that is made lowercase to later use to replace a parameter with.
     * @param string The {@link String} to make lowercase and store.
     */
    public StringParameter(@NotNull String string) {
        value = string.toLowerCase();
    }

    /**
     * Returns the {@link String} to use replace the parameter with.
     * @return A {@link String} to replace a parameter with.
     */
    @Override
    public @NotNull String getValue() {
        return value;
    }
}
