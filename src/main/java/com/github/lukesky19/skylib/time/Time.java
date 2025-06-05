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
package com.github.lukesky19.skylib.time;

/**
 * Stores the years, months, weeks, days, hours, minutes, seconds, and milliseconds for a point in time.
 * You should parse System.currentTimeMillis() to populate data.
 * See {@link com.github.lukesky19.skylib.time.TimeUtil#millisToTime(long)}
 * and {@link com.github.lukesky19.skylib.time.TimeUtil#stringToMillis(String)}
 * @param years The years as an int
 * @param months The months as an int
 * @param weeks The weeks as an int
 * @param days The days as an int
 * @param hours The hours as an int
 * @param minutes The minutes as an int
 * @param seconds The seconds as an int
 * @param milliseconds The milliseconds as an int
 */
public record Time(
        int years,
        int months,
        int weeks,
        int days,
        int hours,
        int minutes,
        int seconds,
        int milliseconds) {}
