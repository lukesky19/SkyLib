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
package com.github.lukesky19.skylib.api.time;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class containing utilities for converting {@link String}s to milliseconds and milliseconds to readable time formats.
 */
public class TimeUtil {
    private static final Pattern pattern = Pattern.compile("([0-9]+)([smhdwMy])");
    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;
    private static final long MONTH = 30 * DAY;
    private static final long YEAR = 365 * DAY;

    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public TimeUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Takes String representing an amount of time (i.e., 1d2h30m) and converts it to Milliseconds.
     * @param time A String with a time to parse
     * @return Milliseconds
     */
    public static long stringToMillis(@NotNull String time) {
        Matcher matcher = pattern.matcher(time);
        long millis = 0;

        while(matcher.find()) {
            int num = Integer.parseInt(matcher.group(1));
            String type = matcher.group(2);
            switch (type) {
                case "s" -> millis = (num * 1000L) + millis;

                case "m" -> millis = (num * 60L * 1000L) + millis;

                case "h" -> millis = (num * 60L * 60L * 1000L) + millis;

                case "d" -> millis = (num * 24L * 60L * 60L * 1000L) + millis;

                case "w" -> millis = (num * 7L * 24L * 60L * 60L * 1000L) + millis;

                case "M" -> millis = (num * 30L * 24L * 60L * 60L * 1000L) + millis;

                case "y" -> millis = (num * 365L * 24L * 60L * 60L * 1000L) + millis;
            }
        }

        return millis;
    }

    /**
     * Takes a long representing milliseconds and returns a {@link Time} Record that holds the years, months, weeks, days, hours, minutes, and milliseconds.
     * @param millis The milliseconds to convert.
     * @return A {@link Time} Record that holds the years, months, weeks, days, hours, minutes, and milliseconds.
     */
    public static Time millisToTime(long millis) {
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if(millis >= YEAR) {
            years = (int) (millis / YEAR);
            millis %= YEAR;
        }

        if(millis >= MONTH) {
            months = (int) (millis / MONTH);
            millis %= MONTH;
        }

        if(millis >= WEEK) {
            weeks = (int) (millis / WEEK);
            millis %= WEEK;
        }

        if(millis >= DAY) {
            days = (int) (millis / DAY);
            millis %= DAY;
        }

        if(millis >= HOUR) {
            hours = (int) (millis / HOUR);
            millis %= HOUR;
        }

        if(millis >= MINUTE) {
            minutes = (int) (millis / MINUTE);
            millis %= MINUTE;
        }

        if(millis >= SECOND) {
            seconds = (int) (millis / SECOND);
            millis %= SECOND;
        }

        return new Time(years, months, weeks, days, hours, minutes, seconds, (int) millis);
    }

    /**
     * Converts a {@link Time} Record to milliseconds.
     * @param time A {@link Time} Record
     * @return A long representing the milliseconds.
     */
    public static long timeToMillis(@NotNull Time time) {
        long millis = 0;

        millis += time.years() * YEAR;
        millis += time.months() * MONTH;
        millis += time.weeks() * WEEK;
        millis += time.days() * DAY;
        millis += time.hours() * HOUR;
        millis += time.minutes() * MINUTE;
        millis += time.seconds() * SECOND;
        millis += time.milliseconds();

        return millis;
    }

    /**
     * Formats the number of milliseconds to a formatted timestamp.
     * @param millis The number of milliseconds to format.
     * @param zoneId The {@link ZoneId} to use for the {@link DateTimeFormatter}. I.e., ZoneId.of("America/New_York")
     * @param timestampFormat The timestamp format to use for the {@link DateTimeFormatter}. I.e., MM-dd-yyyy HH:mm:ss
     * @return A {@link String} representing the formatted timestamp.
     */
    public static String millisToTimeStamp(long millis, @NotNull ZoneId zoneId, @NotNull String timestampFormat) {
        Instant instant = Instant.ofEpochMilli(millis);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timestampFormat).withZone(zoneId);

        return dateTimeFormatter.format(instant);
    }
}
