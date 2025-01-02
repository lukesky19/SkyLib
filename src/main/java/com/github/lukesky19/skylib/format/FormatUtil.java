/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skylib.format;

import com.github.lukesky19.skylib.record.Time;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class containing utilities for formatting Strings into Components and Milliseconds to readable time formats.
 */
public class FormatUtil {
    static final Map<String, String> codeConversion = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("§0", "<black>"),
            new AbstractMap.SimpleEntry<>("§1", "<dark_blue>"),
            new AbstractMap.SimpleEntry<>("§2", "<dark_green>"),
            new AbstractMap.SimpleEntry<>("§3", "<dark_aqua>"),
            new AbstractMap.SimpleEntry<>("§4", "<dark_red>"),
            new AbstractMap.SimpleEntry<>("§5", "<dark_purple>"),
            new AbstractMap.SimpleEntry<>("§6", "<gold>"),
            new AbstractMap.SimpleEntry<>("§7", "<gray>"),
            new AbstractMap.SimpleEntry<>("§8", "<dark_gray>"),
            new AbstractMap.SimpleEntry<>("§9", "<blue>"),
            new AbstractMap.SimpleEntry<>("§a", "<green>"),
            new AbstractMap.SimpleEntry<>("§b", "<aqua>"),
            new AbstractMap.SimpleEntry<>("§c", "<red>"),
            new AbstractMap.SimpleEntry<>("§d", "<light_purple>"),
            new AbstractMap.SimpleEntry<>("§e", "<yellow>"),
            new AbstractMap.SimpleEntry<>("§f", "<white>"),
            new AbstractMap.SimpleEntry<>("§k", "<obfuscated>"),
            new AbstractMap.SimpleEntry<>("§l", "<bold>"),
            new AbstractMap.SimpleEntry<>("§m", "<strikethrough>"),
            new AbstractMap.SimpleEntry<>("§n", "<underlined>"),
            new AbstractMap.SimpleEntry<>("§o", "<italic>"),
            new AbstractMap.SimpleEntry<>("§r", "<reset>"),
            new AbstractMap.SimpleEntry<>("&0", "<black>"),
            new AbstractMap.SimpleEntry<>("&1", "<dark_blue>"),
            new AbstractMap.SimpleEntry<>("&2", "<dark_green>"),
            new AbstractMap.SimpleEntry<>("&3", "<dark_aqua>"),
            new AbstractMap.SimpleEntry<>("&4", "<dark_red>"),
            new AbstractMap.SimpleEntry<>("&5", "<dark_purple>"),
            new AbstractMap.SimpleEntry<>("&6", "<gold>"),
            new AbstractMap.SimpleEntry<>("&7", "<gray>"),
            new AbstractMap.SimpleEntry<>("&8", "<dark_gray>"),
            new AbstractMap.SimpleEntry<>("&9", "<blue>"),
            new AbstractMap.SimpleEntry<>("&a", "<green>"),
            new AbstractMap.SimpleEntry<>("&b", "<aqua>"),
            new AbstractMap.SimpleEntry<>("&c", "<red>"),
            new AbstractMap.SimpleEntry<>("&d", "<light_purple>"),
            new AbstractMap.SimpleEntry<>("&e", "<yellow>"),
            new AbstractMap.SimpleEntry<>("&f", "<white>"),
            new AbstractMap.SimpleEntry<>("&k", "<obfuscated>"),
            new AbstractMap.SimpleEntry<>("&l", "<bold>"),
            new AbstractMap.SimpleEntry<>("&m", "<strikethrough>"),
            new AbstractMap.SimpleEntry<>("&n", "<underlined>"),
            new AbstractMap.SimpleEntry<>("&o", "<italic>"),
            new AbstractMap.SimpleEntry<>("&r", "<reset>")
    );

    private static final Pattern pattern = Pattern.compile("([0-9]+)([smhdwMy])");
    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long WEEK = 7 * DAY;
    private static final long MONTH = 30 * DAY;
    private static final long YEAR = 365 * DAY;

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit Player
     * @param message A String
     * @param placeholders A list of TagResolver.Single which can be created using Placeholder.parsed("STRING", REPLACEMENT)
     * @return A modern Component
     */
    public static Component format(Player player, String message, List<TagResolver.Single> placeholders) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .resolvers(placeholders)
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit OfflinePlayer
     * @param message A String
     * @param placeholders A list of TagResolver.Single which can be created using Placeholder.parsed("STRING", REPLACEMENT)
     * @return A modern Component
     */
    public static Component format(OfflinePlayer player, String message, List<TagResolver.Single> placeholders) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .resolvers(placeholders)
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit Player
     * @param message A String
     * @return A modern Component
     */
    public static Component format(Player player, String message) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit OfflinePlayer
     * @param message A String
     * @return A modern Component
     */
    public static Component format(OfflinePlayer player, String message) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles legacy color codes.
     * @param message A String
     * @param placeholders A list of TagResolver.Single which can be created using Placeholder.parsed("STRING", REPLACEMENT)
     * @return A modern Component
     */
    public static Component format(String message, List<TagResolver.Single> placeholders) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolvers(placeholders)
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles legacy color codes.
     * @param message A String
     * @return A modern Component
     */
    public static Component format(String message) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
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
     * A hacky way to support legacy color codes.
     * @param message A String that has legacy color codes to replace
     * @return A String with clean MiniMessage tags
     */
    private static String handleLegacyCodes(String message) {
        StringBuilder builder = new StringBuilder(message);

        for(Map.Entry<String, String> codeEntry : codeConversion.entrySet()) {
            String target = codeEntry.getKey();
            String replacement = codeEntry.getValue();

            while(builder.toString().contains(target)) {
                int startIndex = builder.toString().indexOf(target);
                int stopIndex = startIndex + target.length();

                builder.replace(startIndex, stopIndex, replacement);
            }
        }

        return builder.toString();
    }

    /**
     * Credit to mbaxter and the <a href="https://docs.advntr.dev/faq.html#how-can-i-use-bukkits-placeholderapi-in-minimessage-messages">Adventure Wiki</a>.
     * Creates a tag resolver capable of resolving PlaceholderAPI tags for a given player.
     * The tag added is of the format <papi:[papi_placeholder]>. For example, <papi:luckperms_prefix>.
     * @param player the player
     * @return the tag resolver
     */
    private static @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player.
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            // We need to turn this ugly legacy string into a nice component.
            final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);

            // Finally, return the tag instance to insert the placeholder!
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }

    /**
     * Credit to mbaxter and the <a href="https://docs.advntr.dev/faq.html#how-can-i-use-bukkits-placeholderapi-in-minimessage-messages">Adventure Wiki</a>.
     * Creates a tag resolver capable of resolving PlaceholderAPI tags for a given player.
     * The tag added is of the format <papi:[papi_placeholder]>. For example, <papi:luckperms_prefix>.
     * @param player the player
     * @return the tag resolver
     */
    private static @NotNull TagResolver papiTag(final @NotNull OfflinePlayer player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player.
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            // We need to turn this ugly legacy string into a nice component.
            final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);

            // Finally, return the tag instance to insert the placeholder!
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }
}

