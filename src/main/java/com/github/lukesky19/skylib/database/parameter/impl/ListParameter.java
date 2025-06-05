package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Takes a {@link List} and converts it to a json {@link String} for storage in a database.
 */
public class ListParameter implements Parameter<String> {
    private final String value;

    /**
     * Stores an {@link List} as a json {@link String} to later use to replace a parameter with.
     * @param list The {@link List} to convert to a json {@link String}.
     */
    public ListParameter(@NotNull List<?> list) {
        Gson gson = new Gson();
        value = gson.toJson(list);
    }

    /**
     * Returns the {@link String} representing a {@link List} as json to replace a parameter with.
     * @return A {@link String} representing a {@link List} as json to replace a parameter with.
     */
    @Override
    public @NotNull String getValue() {
        return value;
    }
}
