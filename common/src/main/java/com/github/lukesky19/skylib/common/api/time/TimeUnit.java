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
package com.github.lukesky19.skylib.common.api.time;

/**
 * This enum contains units of time used for parsing and formatting time.
 * See {@link TimeUtil#millisToTime(long, TimeUnit)}
 */
public enum TimeUnit {
    /**
     * Milliseconds with a priority of 1.
     */
    MILLISECONDS(1),
    /**
     * Seconds with a priority of 2.
     */
    SECONDS(2),
    /**
     * Minutes with a priority of 3.
     */
    MINUTES(3),
    /**
     * Hours with a priority of 4.
     */
    HOURS(4),
    /**
     * Days with a priority of 5.
     */
    DAYS(5),
    /**
     * Weeks with a priority of 6.
     */
    WEEKS(6),
    /**
     * Months with a priority of 7.
     */
    MONTHS(7),
    /**
     * Years with a priority of 8.
     */
    YEARS(8);

    /**
     * The priority of the TimeUnit.
     */
    private final int priority;

    /**
     * Constructor
     * @param priority The priority.
     */
    TimeUnit(int priority) {
        this.priority = priority;
    }

    /**
     * Get the priority of the TimeUnit. Higher number is higher priority.
     * @return The TimeUnit's priority.
     */
    @SuppressWarnings("unused") // This method is used by other plugins.
    public int getPriority() {
        return priority;
    }
}