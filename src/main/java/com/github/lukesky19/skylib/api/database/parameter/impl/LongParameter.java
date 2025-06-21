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
package com.github.lukesky19.skylib.api.database.parameter.impl;

import com.github.lukesky19.skylib.api.database.parameter.Parameter;

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
