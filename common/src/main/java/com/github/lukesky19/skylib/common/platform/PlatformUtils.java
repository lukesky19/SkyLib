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
package com.github.lukesky19.skylib.common.platform;

import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * This class contains data that is platform dependent.
 */
public class PlatformUtils {
    private static @NonNull TypeSerializerCollection collection = TypeSerializerCollection.builder().build();

    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public PlatformUtils() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Initialize the {@link TypeSerializerCollection} for the platform.
     * @param collection The {@link TypeSerializerCollection}
     */
    public static void init(@NonNull TypeSerializerCollection collection) {
        PlatformUtils.collection = collection;
    }

    /**
     * Get the {@link TypeSerializerCollection} for the platform.
     * @return The {@link TypeSerializerCollection}.
     */
    public static @NonNull TypeSerializerCollection getSerializers() {
        return collection;
    }
}