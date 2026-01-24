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
package com.github.lukesky19.skylib.api.adventure;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class containing utilities for formatting Strings into Components.
 */
public class AdventureUtil {
    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public AdventureUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit Player
     * @param message A String
     * @param placeholders A list of TagResolver.Single which can be created using Placeholder.parsed("STRING", REPLACEMENT)
     * @return A modern Component
     */
    public static @NotNull Component deserialize(@NotNull Player player, @NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
    public static @NotNull Component deserialize(@NotNull OfflinePlayer player, @NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
    public static @NotNull Component deserialize(@NotNull Player player, @NotNull String message) {
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
    public static @NotNull Component deserialize(@NotNull OfflinePlayer player, @NotNull String message) {
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
    public static @NotNull Component deserialize(@NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
    public static @NotNull Component deserialize(@NotNull String message) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a {@link Component} into a {@link String} with MiniMessage tags intact.
     * @param component The {@link Component} to serialize.
     * @return A non-null {@link String} of the Component with the color and formatting MiniMessage codes.
     */
    public static @NotNull String serialize(@NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /**
     * A method to support legacy color codes and hex codes.
     * @param message A String that has legacy color codes to replace.
     * @return A String with clean MiniMessage tags.
     */
    public static @NotNull String handleLegacyCodes(@NotNull String message) {
        // Replace hex codes of the format &#FFFFFF
        Matcher hexMatcher = Pattern.compile("&#([0-9A-Fa-f]{6})").matcher(message);
        message = hexMatcher.replaceAll(match -> "<#" + match.group(1) + ">");

        // Handle §x or &x formats for hex colors
        Matcher customHexMatcher = Pattern.compile("(?:§x|&x)((?:§[0-9A-Fa-f]|&[0-9A-Fa-f]){6})").matcher(message);
        message = customHexMatcher.replaceAll(match -> {
            StringBuilder hexColor = new StringBuilder("#");
            String matchedGroup = match.group(1);

            // Extract hex characters from the matched group
            for (int i = 1; i < matchedGroup.length(); i += 2) {
                hexColor.append(matchedGroup.charAt(i));
            }
            return "<" + hexColor + ">";
        });

        // Replace legacy & and § color codes
        for (Map.Entry<String, String> codeEntry : codeConversion.entrySet()) {
            message = message.replaceAll("(?i)(" + Pattern.quote(codeEntry.getKey()) + ")", codeEntry.getValue());
        }

        return message;
    }

    /**
     * Creates a {@link TagResolver} that parses PlaceholderAPI placeholders.
     * Also handles converting legacy color and formatting codes into MiniMessage tags.
     * The tag added is of the format {@literal <papi:[papi_placeholder]>}. For example, {@literal <papi:luckperms_prefix>}.
     * Based on work by mbaxter and the <a href="https://docs.advntr.dev/faq.html#how-can-i-use-bukkits-placeholderapi-in-minimessage-messages">Adventure Wiki</a>.
     * @param player the player
     * @return The {@link TagResolver}.
     */
    public static @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player.
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            // Convert any legacy formatting codes to MiniMessage tags.
            final String cleanPlaceholder = handleLegacyCodes(parsedPlaceholder);

            // Then we serialize the unparsed placeholder above into the final Component
            final Component result = serialize(cleanPlaceholder);

            // Finally, return the tag instance to insert the placeholder!
            return Tag.selfClosingInserting(result);
        });
    }

    /**
     * Creates a {@link TagResolver} that parses PlaceholderAPI placeholders.
     * Also handles converting legacy color and formatting codes into MiniMessage tags.
     * The tag added is of the format {@literal <papi:[papi_placeholder]>}. For example, {@literal <papi:luckperms_prefix>}.
     * Based on work by mbaxter and the <a href="https://docs.advntr.dev/faq.html#how-can-i-use-bukkits-placeholderapi-in-minimessage-messages">Adventure Wiki</a>.
     * @param player the player
     * @return The {@link TagResolver}.
     */
    public static @NotNull TagResolver papiTag(final @NotNull OfflinePlayer player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player.
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            // Convert any legacy formatting codes to MiniMessage tags.
            final String cleanPlaceholder = handleLegacyCodes(parsedPlaceholder);

            // Then we serialize the unparsed placeholder above into the final Component
            final Component result = serialize(cleanPlaceholder);

            // Finally, return the tag instance to insert the placeholder!
            return Tag.selfClosingInserting(result);
        });
    }

    private static final @NotNull Map<String, String> codeConversion = Map.ofEntries(
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

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit Player
     * @param message A String
     * @param placeholders A list of TagResolver.Single which can be created using Placeholder.parsed("STRING", REPLACEMENT)
     * @return A modern Component
     * @deprecated This method was originally named incorrectly. Use {@link #deserialize(Player, String, List)} instead.
     */
    @Deprecated(since = "1.4.0.0", forRemoval = true)
    public static @NotNull Component serialize(@NotNull Player player, @NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
     * @deprecated This method was originally named incorrectly. Use {@link #deserialize(OfflinePlayer, String, List)} instead.
     */
    @Deprecated(since = "1.4.0.0", forRemoval = true)
    public static @NotNull Component serialize(@NotNull OfflinePlayer player, @NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
     * @deprecated This method was originally named incorrectly. Use {@link #deserialize(Player, String)} instead.
     */
    @Deprecated(since = "1.4.0.0", forRemoval = true)
    public static @NotNull Component serialize(@NotNull Player player, @NotNull String message) {
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
     * @deprecated This method was originally named incorrectly. Use {@link #deserialize(OfflinePlayer, String)} instead.
     */
    @Deprecated(since = "1.4.0.0", forRemoval = true)
    public static @NotNull Component serialize(@NotNull OfflinePlayer player, @NotNull String message) {
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
     * @deprecated This method was originally named incorrectly. Use {@link #deserialize(String, List)} instead.
     */
    @Deprecated(since = "1.4.0.0", forRemoval = true)
    public static @NotNull Component serialize(@NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
     * @deprecated This method was originally named incorrectly. Use {@link #deserialize(String)} instead.
     */
    @Deprecated(since = "1.4.0.0", forRemoval = true)
    public static @NotNull Component serialize(@NotNull String message) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .build())
                .build();

        return mm.deserialize(handleLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Serializes a {@link Component} into a {@link String}.
     * @param component The {@link Component} to serialize.
     * @return A non-null {@link String} of the Component with the color and formatting MiniMessage codes.
     * @deprecated This method was originally named incorrectly. Use {@link #serialize(Component)} instead.
     */
    @Deprecated(since = "1.4.0.0", forRemoval = true)
    public static @NotNull String deserialize(@NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }
}