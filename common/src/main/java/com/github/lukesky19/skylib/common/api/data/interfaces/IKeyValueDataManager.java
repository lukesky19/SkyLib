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
package com.github.lukesky19.skylib.common.api.data.interfaces;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * This interface can be used to create a key value data manager.
 * @param <K> The key or identifier.
 * @param <V> The value or data object.
 */
@SuppressWarnings("unused") // This interface is used by this and other plugins.
public interface IKeyValueDataManager<K, V> {
    /**
     * Get the {@link V} data from the {@link K} identifier.
     * @param identifier The {@link K} identifier.
     * @return The {@link V} data or null.
     */
    @Nullable V getData(@NonNull K identifier);

    /**
     * Get the {@link Map} mapping {@link K} to {@link V}.
     * @return A {@link Map} mapping {@link K} to {@link V}.
     */
    @NonNull Map<K, V> getAllData();

    /**
     * Store the {@link V} data for the {@link K} identifier.
     * @param identifier The {@link K} identifier.
     * @param data The {@link V} data.
     */
    void setData(@NonNull K identifier, @NonNull V data);

    /**
     * Remove the {@link V} data for the {@link K} identifier.
     * @param identifier The {@link K} identifier.
     */
    void removeDataByIdentifier(@NonNull K identifier);

    /**
     * Remove the {@link V} data provided from the map
     * @param data The data to remove.
     */
    void removeDataByData(@NonNull V data);

    /**
     * Clear all data.
     */
    void clearData();
}
