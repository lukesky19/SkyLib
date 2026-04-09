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
package com.github.lukesky19.skylib.common.api.database.parameter.impl;

import com.github.lukesky19.skylib.common.api.database.parameter.Parameter;
import org.jspecify.annotations.NonNull;

/**
 * Takes a {@link String} and converts it to lowercase for storage in a database.
 * @param value The value.
 */
@SuppressWarnings("unused") // This class is used by other plugins.
public record StringParameter(@NonNull String value) implements Parameter<String> {
    /**
     * Stores a {@link String} that is made lowercase to later use to replace a parameter with.
     * @param value The {@link String} to make lowercase and store.
     */
    public StringParameter(@NonNull String value) {
        this.value = value.toLowerCase();
    }

    /**
     * Returns the {@link String} to use replace the parameter with.
     * @return A {@link String} to replace a parameter with.
     */
    @Override
    public @NonNull String getValue() {
        return value;
    }
}