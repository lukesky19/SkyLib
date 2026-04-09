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
package com.github.lukesky19.skylib.paper.api.gui.interfaces;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This interface can be used to create a GUI manager class.
 * @param <I> The key or identifier.
 */
@SuppressWarnings("unused") // This interface is used by this and other plugins.
public interface IGUIManager<I> {
    /**
     * Add the gui as open for the identifier provided.
     * @param identifier The identifier.
     * @param gui The gui.
     */
    void addOpenGUI(@NonNull I identifier, @NonNull BaseGUI<I> gui);

    /**
     * Remove the gui as open for the identifier provided.
     * @param identifier The identifier.
     */
    void removeOpenGUI(@NonNull I identifier);

    /**
     * Get the open gui for the identifier provided.
     * @param identifier The identifier to get the open GUI for.
     * @return The gui or null.
     */
    @Nullable BaseGUI<I> getOpenGUI(@NonNull I identifier);

    /**
     * Refresh the open guis with the identifier provided.
     * @param identifier The identifier.
     */
    void refreshGUIs(@NonNull I identifier);

    /**
     * Close the open guis.
     * @param onDisable Is this on plugin disable?
     */
    void closeOpenGUIs(boolean onDisable);
}