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
package com.github.lukesky19.skylib.api.common.abstracts.data;

import com.github.lukesky19.skylib.api.common.interfaces.data.IKeyValueDataManager;
import com.google.common.collect.ImmutableMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be extended to create a data manager class. Data is stored in a {@link HashMap}.
 * @param <K> The key.
 * @param <V> The value.
 */
public abstract class HashMapDataManager<K, V> implements IKeyValueDataManager<K, V> {
    /**
     * The {@link Map} that stores the data using {@link K} as the key and {@link V} as the value.
     */
    protected final @NonNull Map<K, V> dataMap = new HashMap<>();

    /**
     * Constructor
     */
    public HashMapDataManager() {}

    /**
     * Get the {@link V} data from the {@link K} identifier.
     * @param identifier The {@link K} identifier.
     * @return The {@link V} data or null.
     */
    @Override
    public @Nullable V getData(@NonNull K identifier) {
        return dataMap.get(identifier);
    }

    /**
     * Get the {@link Map} mapping {@link K} to {@link V}.
     * @return A {@link Map} mapping {@link K} to {@link V}.
     */
    @Override
    public @NonNull Map<K, V> getAllData() {
        return ImmutableMap.copyOf(dataMap);
    }

    /**
     * Store the {@link V} data for the {@link K} identifier.
     * @param identifier The {@link K} identifier.
     * @param data The {@link V} data.
     */
    @Override
    public void setData(@NonNull K identifier, @NonNull V data) {
        dataMap.put(identifier, data);
    }

    /**
     * Remove the {@link V} data for the {@link K} identifier.
     * @param identifier The {@link K} identifier.
     */
    @Override
    public void removeDataByIdentifier(@NonNull K identifier) {
        dataMap.remove(identifier);
    }

    /**
     * Remove the {@link V} data provided from the map
     * @param data The data to remove.
     */
    @Override
    public void removeDataByData(@NonNull V data) {
        dataMap.values().removeIf(iteratorData -> iteratorData.equals(data));
    }

    /**
     * Clear all data.
     */
    @Override
    public void clearData() {
        dataMap.clear();
    }
}
