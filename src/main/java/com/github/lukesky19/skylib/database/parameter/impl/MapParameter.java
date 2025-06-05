package com.github.lukesky19.skylib.database.parameter.impl;

import com.github.lukesky19.skylib.database.parameter.Parameter;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Takes a {@link Map} and converts it to a json {@link String} for storage in a database.
 */
public class MapParameter implements Parameter<String>  {
    private final String value;

    /**
     * Stores a {@link Map} as a json {@link String} to later use to replace a parameter with.
     * @param map The {@link Map} to convert to a json {@link String}.
     */
    public MapParameter(@NotNull Map<?, ?> map) {
        Gson gson = new Gson();
        value = gson.toJson(map);
    }

    /**
     * Returns the {@link String} representing a {@link Map} as json to replace a parameter with.
     * @return A {@link String} representing a {@link Map} as json to replace a parameter with.
     */
    @Override
    public @NotNull String getValue() {
        return value;
    }
}
