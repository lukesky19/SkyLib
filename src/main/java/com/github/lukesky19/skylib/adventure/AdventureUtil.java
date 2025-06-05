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
package com.github.lukesky19.skylib.adventure;

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
    public static Component serialize(@NotNull Player player, @NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
    public static Component serialize(@NotNull OfflinePlayer player, @NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
    public static Component serialize(@NotNull Player player, @NotNull String message) {
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
    public static Component serialize(@NotNull OfflinePlayer player, @NotNull String message) {
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
    public static Component serialize(@NotNull String message, @NotNull List<TagResolver.Single> placeholders) {
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
    public static Component serialize(@NotNull String message) {
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
     */
    @NotNull
    public static String deserialize(@NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
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

    private static final Map<String, String> codeConversion = Map.ofEntries(
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
}

