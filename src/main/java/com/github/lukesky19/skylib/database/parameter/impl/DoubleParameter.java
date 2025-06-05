package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;
import org.jetbrains.annotations.NotNull;

/**
 * Takes an {@link Double} to store inside a database. No processing is done to the {@link Double}.
 */
public class DoubleParameter implements Parameter<Double> {
    private final double value;

    /**
     * Stores a {@link Double} to later use to replace a parameter with.
     * @param number The number to replace a parameter with.
     */
    public DoubleParameter(@NotNull Double number) {
        value = number;
    }

    /**
     * Returns the {@link Double} to use replace the parameter with.
     * @return A {@link Double} to replace a parameter with.
     */
    @Override
    public @NotNull Double getValue() {
        return value;
    }
}
