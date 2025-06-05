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
