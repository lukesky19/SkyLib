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
package com.github.lukesky19.skylib.paper.api.gui.abstracts;

import com.github.lukesky19.skylib.common.api.data.abstracts.HashMapDataManager;
import com.github.lukesky19.skylib.paper.api.gui.interfaces.BaseGUI;
import com.github.lukesky19.skylib.paper.api.gui.interfaces.IGUIManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class can be extended to provide a ready-to-use template for storing open GUIs.
 * @param <I> The identifier for the GUIs.
 */
public abstract class AbstractGUIManager<I> extends HashMapDataManager<I, BaseGUI<I>> implements IGUIManager<I> {
    /**
     * Constructor
     */
    public AbstractGUIManager() {}

    @Override
    public void addOpenGUI(@NonNull I identifier, @NonNull BaseGUI<I> data) {
        setData(identifier, data);
    }

    @Override
    public void removeOpenGUI(@NonNull I identifier) {
        removeDataByIdentifier(identifier);
    }

    @Override
    public @Nullable BaseGUI<I> getOpenGUI(@NonNull I identifier) {
        return getData(identifier);
    }

    @Override
    public void refreshGUIs(@NonNull I identifier) {
        dataMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getKey().equals(identifier))
                .forEach(entry -> {
                    BaseGUI<I> gui = entry.getValue();

                    if(gui != null) {
                        gui.refresh();
                    }
                });
    }

    @Override
    public void closeOpenGUIs(boolean onDisable) {
        dataMap.entrySet().iterator().forEachRemaining(entry -> {
            BaseGUI<I> baseGUI = entry.getValue();
            if(baseGUI != null) baseGUI.unload(onDisable);
        });
    }
}