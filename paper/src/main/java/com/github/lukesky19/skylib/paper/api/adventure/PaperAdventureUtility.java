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
package com.github.lukesky19.skylib.paper.api.adventure;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * A class containing utilities for formatting Strings into Components.
 */
public class PaperAdventureUtility {
    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public PaperAdventureUtility() {
        throw new UnsupportedOperationException("This class cannot be instanced. Use the static references to methods instead.");
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
    public static @NonNull Component deserialize(@NonNull Player player, @NonNull String message, @NonNull List<TagResolver.Single> placeholders) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .resolvers(placeholders)
                        .build())
                .build();

        return mm.deserialize(AdventureUtility.convertLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
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
    public static @NonNull Component deserialize(@NonNull OfflinePlayer player, @NonNull String message, @NonNull List<TagResolver.Single> placeholders) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .resolvers(placeholders)
                        .build())
                .build();

        return mm.deserialize(AdventureUtility.convertLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit Player
     * @param message A String
     * @return A modern Component
     */
    public static @NonNull Component deserialize(@NonNull Player player, @NonNull String message) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .build())
                .build();

        return mm.deserialize(AdventureUtility.convertLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles PlaceholderAPI placeholders.
     * Handles legacy color codes.
     * @param player A Bukkit OfflinePlayer
     * @param message A String
     * @return A modern Component
     */
    public static @NonNull Component deserialize(@NonNull OfflinePlayer player, @NonNull String message) {
        MiniMessage mm = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .build())
                .build();

        return mm.deserialize(AdventureUtility.convertLegacyCodes(message)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles legacy color codes.
     * @param message A String
     * @param placeholders A list of TagResolver.Single which can be created using Placeholder.parsed("STRING", REPLACEMENT)
     * @return A modern Component
     */
    public static @NonNull Component deserialize(@NonNull String message, @NonNull List<TagResolver.Single> placeholders) {
        return AdventureUtility.deserialize(message, placeholders);
    }

    /**
     * Converts a String to a modern Component using MiniMessage.
     * Handles legacy color codes.
     * @param message A String
     * @return A modern Component
     */
    public static @NonNull Component deserialize(@NonNull String message) {
        return AdventureUtility.deserialize(message);
    }

    /**
     * Converts a {@link Component} into a {@link String} with MiniMessage tags intact.
     * @param component The {@link Component} to serialize.
     * @return A non-null {@link String} of the Component with the color and formatting MiniMessage codes.
     */
    public static @NonNull String serialize(@NonNull Component component) {
        return AdventureUtility.serialize(component);
    }

    /**
     * Creates a {@link TagResolver} that parses PlaceholderAPI placeholders.
     * Also handles converting legacy color and formatting codes into MiniMessage tags.
     * The tag added is of the format {@literal <papi:[papi_placeholder]>}. For example, {@literal <papi:luckperms_prefix>}.
     * Based on work by mbaxter and the <a href="https://docs.advntr.dev/faq.html#how-can-i-use-bukkits-placeholderapi-in-minimessage-messages">Adventure Wiki</a>.
     * @param player the player
     * @return The {@link TagResolver}.
     */
    private static @NonNull TagResolver papiTag(final @NonNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, _) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player.
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            // Convert any legacy formatting codes to MiniMessage tags.
            final String cleanPlaceholder = AdventureUtility.convertLegacyCodes(parsedPlaceholder);

            // Then we serialize the unparsed placeholder above into the final Component
            final Component result = deserialize(cleanPlaceholder);

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
    private static @NonNull TagResolver papiTag(final @NonNull OfflinePlayer player) {
        return TagResolver.resolver("papi", (argumentQueue, _) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player.
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            // Convert any legacy formatting codes to MiniMessage tags.
            final String cleanPlaceholder = AdventureUtility.convertLegacyCodes(parsedPlaceholder);

            // Then we serialize the unparsed placeholder above into the final Component
            final Component result = deserialize(cleanPlaceholder);

            // Finally, return the tag instance to insert the placeholder!
            return Tag.selfClosingInserting(result);
        });
    }
}