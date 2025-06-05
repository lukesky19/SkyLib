package com.github.lukesky19.skylib.format;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class contains methods useful for formatting different Objects, Enums, and or NamespacedKeys into user-friendly {@link String}s for use in messages.
 */
public class FormatUtil {
    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public FormatUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Formats a {@link Material} name to all lowercase and replaces any underscores with spaces.
     * @param material The {@link Material} to format the name for.
     * @return A formatted {@link String} representing thee provided {@link Material}'s name.
     */
    public static @NotNull String formatMaterialName(@NotNull Material material) {
        return formatEnumName(material.name());
    }

    /**
     * Formats an {@link BlockType} name to all lowercase and replaces any underscores with spaces.
     * @param blockType The {@link BlockType} to format the name for.
     * @return A formatted {@link String} representing the provided {@link BlockType}'s name.
     */
    public static @NotNull String formatBlockTypeName(@NotNull BlockType blockType) {
        return formatKey(blockType.getKey());
    }

    /**
     * Formats an {@link ItemType} name to all lowercase and replaces any underscores with spaces.
     * @param itemType The {@link ItemType} to format the name for.
     * @return A formatted {@link String} representing the provided {@link ItemType}'s name.
     */
    public static @NotNull String formatItemTypeName(@NotNull ItemType itemType) {
        return formatKey(itemType.getKey());
    }

    /**
     * Formats a {@link PotionType} name to all lowercase and replaces any underscores with spaces.
     * @param potionType The {@link PotionType} to format the name for.
     * @return A formatted {@link String} representing the provided {@link PotionType}'s name.
     */
    public static @NotNull String formatPotionTypeName(@NotNull PotionType potionType) {
        return formatEnumName(potionType.name());
    }

    /**
     * Formats a {@link EntityType} name to all lowercase and replaces any underscores with spaces.
     * @param entityType The {@link EntityType} to format the name for.
     * @return A formatted {@link String} representing the provided {@link EntityType}'s name.
     */
    public static @NotNull String formatEntityName(@NotNull EntityType entityType) {
        return formatEnumName(entityType.name());
    }

    /**
     * Takes an {@link Enchantment} and enchantment level and format the name and level to a single {@link String}.
     * @param enchantment The {@link Enchantment} to format.
     * @param enchantmentLevel The enchantment level to format.
     * @return A {@link String} representing the enchantment name and level in a single {@link String}.
     */
    public static @NotNull String getEnchantmentAsString(@NotNull Enchantment enchantment, int enchantmentLevel) {
        return formatKey(enchantment.getKey()) + " " + enchantmentLevel;
    }

    /**
     * Takes a {@link Map} of {@link Enchantment}s and enchantment levels, a delimiter, and final delimiter to format the enchantment names and enchantment levels to a single {@link String}.
     * @param enchantments The {@link Map} of {@link Enchantment}s and enchantment levels.
     * @param delimiter The delimiter to use.
     * @param finalDelimiter The final delimiter to use.
     * @return A {@link String} representing the enchantment names and enchantment levels in a single {@link String}.
     */
    public static @NotNull String getEnchantmentsListedAsString(@NotNull Map<Enchantment, Integer> enchantments, @NotNull String delimiter, @NotNull String finalDelimiter) {
        final int size = enchantments.size();
        if(size == 0) return "";

        List<String> formattedEnchantments = enchantments.entrySet().stream()
                .map(entry -> getEnchantmentAsString(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        if (size == 1) {
            return formattedEnchantments.getFirst();
        } else if (size == 2) {
            return String.join(finalDelimiter, formattedEnchantments);
        } else {
            StringBuilder result = new StringBuilder();
            for(int i = 0; i < formattedEnchantments.size() - 1; i++) {
                result.append(formattedEnchantments.get(i));

                if(i < formattedEnchantments.size() - 2) {
                    result.append(delimiter);
                }
            }

            result.append(finalDelimiter).append(formattedEnchantments.getLast());
            return result.toString();
        }
    }

    /**
     * Formats a {@link String} that represents the name of an enum and formats it to all lowercase and replaces any underscores with spaces.
     * @param enumName The name of the enum to format.
     * @return A formatted {@link String}.
     */
    private static @NotNull String formatEnumName(String enumName) {
        String[] words = enumName.split("_");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(word.toLowerCase()).append(" ");
            }
        }

        return formatted.toString().trim();
    }

    /**
     * Formats a {@link NamespacedKey} to a {@link String} and formats it to all lowercase and replaces any underscores with spaces.
     * @param key The {@link NamespacedKey} to format.
     * @return A formatted {@link String}.
     */
    private static @NotNull String formatKey(@NotNull NamespacedKey key) {
        String name = key.getKey();
        String[] words = name.split("_");
        StringBuilder formatted = new StringBuilder();

        for(String word : words) {
            if(!word.isEmpty()) {
                formatted.append(word.toLowerCase()).append(" ");
            }
        }

        return formatted.toString().trim();
    }
}
