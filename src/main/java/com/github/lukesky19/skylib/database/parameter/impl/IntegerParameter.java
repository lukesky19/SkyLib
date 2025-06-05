package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;
import org.jetbrains.annotations.NotNull;

/**
 * Takes an {@link Integer} to store inside a database. No processing is done to the {@link Integer}.
 */
public class IntegerParameter implements Parameter<Integer> {
    private final int value;

    /**
     * Stores an {@link Integer} to later use to replace a parameter with.
     * @param number The number to replace a parameter with.
     */
    public IntegerParameter(@NotNull Integer number) {
        value = number;
    }

    /**
     * Returns the {@link Integer} to use replace the parameter with.
     * @return An {@link Integer} to replace a parameter with.
     */
    @Override
    public @NotNull Integer getValue() {
        return value;
    }
}
