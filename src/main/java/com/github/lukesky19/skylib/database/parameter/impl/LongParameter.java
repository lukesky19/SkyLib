package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;

/**
 * Takes an {@link Long} to store inside a database. No processing is done to the {@link Long}.
 */
public class LongParameter implements Parameter<Long> {
    private final Long value;

    /**
     * Stores an {@link Long} to later use to replace a parameter with.
     * @param number The number to replace a parameter with.
     */
    public LongParameter(Long number) {
        value = number;
    }

    /**
     * Returns the {@link Long} to use replace the parameter with.
     * @return An {@link Long} to replace a parameter with.
     */
    @Override
    public Long getValue() {
        return value;
    }
}
