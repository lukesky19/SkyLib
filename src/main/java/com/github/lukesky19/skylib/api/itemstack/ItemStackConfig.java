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
package com.github.lukesky19.skylib.api.itemstack;

import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

/**
 * This record contains the data necessary to create an {@link ItemStack} using {@link ItemStackBuilder}.
 * @param itemType The {@link NamespacedKey} for an {@link ItemType} as a {@link String}. The format can be either {@code namespace:key} or just {@code key}.
 * @param amount The amount of items in the {@link ItemStack}.
 * @param maxStackSize The maximium amount of items that can be in the {@link ItemStack}. This must be at greater than or equal to 1 and less than or equal to 99.
 * @param name The name to give the {@link ItemStack} as a {@link String} with MiniMessage tags for formatting.
 * @param lore The lore to give the {@link ItemStack} as a {@link List} of {@link String}s with MiniMessage tags for formatting.
 * @param entityType The {@link NamespacedKey} for an {@link EntityType} as a {@link String} that may be associated with the {@link ItemStack}. Currently only applies to Spawners. The format can be either {@code namespace:key} or just {@code key}.
 * @param instrument The {@link NamespacedKey} for a {@link MusicInstrument} as a {@link String} that may be associated with the {@link ItemStack}. Currently only applies to goat horns. The format can be either {@code namespace:key} or just {@code key}.
 * @param enchantments A {@link List} of {@link EnchantmentConfig}s that may be applied to the {@link ItemStack}.
 * @param potionConfig The {@link PotionConfig}.
 * @param color The {@link ColorConfig}.
 * @param modelName The {@link NamespacedKey} as a {@link String} that may be associated with the {@link ItemStack}. Currently only used to apply item models.
 * @param itemFlags A {@link List} of {@link ItemFlag}s to apply to the {@link ItemStack}.
 * @param decoratedPot The {@link DecoratedPotConfig}.
 * @param armorTrim The {@link ArmorTrimConfig}.
 * @param attributes The {@link AttributeConfig}.
 * @param options The {@link OptionsConfig}.
 */
@ConfigSerializable
public record ItemStackConfig(
        @Nullable String itemType,
        @Nullable Integer amount,
        @Nullable Integer maxStackSize,
        @Nullable String name,
        @NotNull List<String> lore,
        @Nullable String entityType,
        @Nullable String instrument,
        @NotNull List<EnchantmentConfig> enchantments,
        @NotNull PotionConfig potionConfig,
        @NotNull ItemStackConfig.ColorConfig color,
        @Nullable String modelName,
        @NotNull List<String> itemFlags,
        @NotNull DecoratedPotConfig decoratedPot,
        @NotNull ArmorTrimConfig armorTrim,
        @NotNull List<AttributeConfig> attributes,
        @NotNull OptionsConfig options) {
    /**
     * This record contains the information to create a potion {@link ItemStack}.
     * @param potionType The {@link NamespacedKey} for a {@link PotionType} as a {@link String} that may be associated with the {@link ItemStack}. The format can be either {@code namespace:key} or just {@code key}.
     * @param potionEffects A {@link List} of {@link PotionEffectConfig}s that may additionally be associated with the {@link ItemStack}.
     */
    @ConfigSerializable
    public record PotionConfig(
            @Nullable String potionType,
            @NotNull List<PotionEffectConfig> potionEffects) {}

    /**
     * This record contains the information to create a {@link PotionEffect} to apply to a potion {@link ItemStack}
     * @param type The {@link NamespacedKey} for a {@link PotionEffectType} as a {@link String}. The format can be either {@code namespace:key} or just {@code key}.
     * @param durationSeconds The durationSeconds of the {@link PotionEffect} as an {@link Integer}.
     * @param amplifier The amplifier of the {@link PotionEffect} as an {@link Integer}.
     */
    @ConfigSerializable
    public record PotionEffectConfig(
            @Nullable String type,
            @Nullable Double durationSeconds,
            @Nullable Integer amplifier) {}

    /**
     * This record contains the information to create an {@link Enchantment} to apply to an {@link ItemStack}.
     * @param enchantment The {@link NamespacedKey} for an {@link Enchantment} as a {@link String}. The format can be either {@code namespace:key} or just {@code key}.
     * @param level The level of the {@link Enchantment} as an {@link Integer}.
     */
    @ConfigSerializable
    public record EnchantmentConfig(
            @Nullable String enchantment,
            @Nullable Integer level) {}

    /**
     * This record contains the information to apply a color to an {@link ItemStack} that can be dyed.
     * @param random A boolean on whether to randomly generate a color or not. If this is true, {@link #red}, {@link #green}, {@link #blue} are ignored.
     * @param red A value of 0 to 255.
     * @param green A value of 0 to 255.
     * @param blue A value of 0 to 255.
     */
    @ConfigSerializable
    public record ColorConfig(
            boolean random,
            @Nullable Integer red,
            @Nullable Integer green,
            @Nullable Integer blue) {}

    /**
     * This record contains the information to apply sherds to a decorated pot.
     * The use of {@link Material} may be replaced with {@link ItemType} in the future.
     * The format can should be {@code name} to ensure forward compatibility as in the future the format {@code namespace:key} or just {@code key} will be used.
     * @param frontSherd The {@link Material} name of the sherd.
     * @param leftSherd The {@link Material} name of the sherd.
     * @param rightSherd The {@link Material} name of the sherd.
     * @param backSherd The {@link Material} name of the sherd.
     * @apiNote Although there should be no action necessary from the potential change from {@link Material} names to {@link ItemType}s and {@link NamespacedKey}s, this is still marked as experimental due to such a change.
     */
    @ApiStatus.Experimental
    @ConfigSerializable
    public record DecoratedPotConfig(
            @Nullable String frontSherd,
            @Nullable String leftSherd,
            @Nullable String rightSherd,
            @Nullable String backSherd) {}

    /**
     * This record contains the information for an {@link ArmorTrim} that may be applied to an {@link ItemStack}. Currently only used for armors.
     * @param trimMaterial The {@link NamespacedKey} for a {@link TrimMaterial} as a {@link String}. The format can be either {@code namespace:key} or just {@code key}.
     * @param trimPattern The {@link NamespacedKey} for a {@link TrimPattern} as a {@link String}. The format can be either {@code namespace:key} or just {@code key}.
     */
    @ConfigSerializable
    public record ArmorTrimConfig(
            @Nullable String trimMaterial,
            @Nullable String trimPattern) {}

    /**
     * This record contains the information to apply an {@link Attribute} to an {@link ItemStack}.
     * @param attribute The {@link NamespacedKey} for an {@link Attribute} as a {@link String}. The format can be either {@code namespace:key} or just {@code key}.
     * @param amount The amount associated with the {@link Attribute}.
     * @param operation The {@link org.bukkit.attribute.AttributeModifier.Operation} as a {@link String} associated with the {@link Attribute}.
     * @param equipmentSlot An optional {@link EquipmentSlot} as a {@link String} associated with the {@link Attribute}.
     * @apiNote This is marked as experimental as this has been untested and may break when Minecraft updates.
     */
    @ApiStatus.Experimental
    @ConfigSerializable
    public record AttributeConfig(
            @Nullable String attribute,
            @Nullable Double amount,
            @Nullable String operation,
            @Nullable String equipmentSlot) {}

    /**
     * Extra options that may be applied to an {@link ItemStack}.
     * @param enchantmentGlint Whether the {@link ItemStack} should have an enchantment glint. Null values don't modify the default setting.
     * @param unbreakable Whether the {@link ItemStack} should have infinite durability. Null values don't modify the default setting.
     * @param fireResistant Whether the {@link ItemStack} should be fire-resistant (like Netherite). Null values don't modify the default setting.
     * @param hideToolTip Whether the {@link ItemStack} tool tips should be hidden. Null values don't modify the default setting.
     * @param glider Whether the {@link ItemStack} should act like an Elytra. Null values don't modify the default setting.
     */
    @ConfigSerializable
    public record OptionsConfig(
            @Nullable Boolean enchantmentGlint,
            @Nullable Boolean unbreakable,
            @Nullable Boolean fireResistant,
            @Nullable Boolean hideToolTip,
            @Nullable Boolean glider) {}
}
