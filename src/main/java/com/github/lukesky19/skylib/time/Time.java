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
