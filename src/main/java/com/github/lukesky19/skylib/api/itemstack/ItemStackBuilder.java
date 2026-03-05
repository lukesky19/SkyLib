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

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.format.FormatUtil;
import com.github.lukesky19.skylib.api.registry.RegistryUtil;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.DecoratedPot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.tag.DamageTypeTags;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * This class is used to create an {@link ItemStack} from a {@link ItemStackConfig}.
 * It can also be used independently by using setter methods.
 */
public class ItemStackBuilder {
    private final ComponentLogger logger;

    // ItemStack - This is not the final ItemStack, but a base ItemStack that can be modified by the builder.
    private @Nullable ItemStack baseItemStack;
    // ItemType - Only used if a base ItemStack above is not provided.
    private @Nullable ItemType itemType;
    // ItemStack amount
    private int amount = 1;
    // Max stack size
    private @Nullable Integer maxStackSize;
    // Item Name
    private @Nullable Component name;
    // Item lore
    private @NonNull List<Component> lore = new ArrayList<>();
    // Enchantments
    private @NonNull Map<Enchantment, Integer> enchantments = new HashMap<>();
    // EntityType - Only used for Spawners.
    private @Nullable EntityType entityType;
    // Potion Type - Only used for Potions
    private @Nullable PotionType potionType;
    // PotionEffects - Used for Potions and Suspicious Stew
    private @NonNull List<PotionEffect> potionEffects = new ArrayList<>();
    // Color - Used for dyeable items.
    private @Nullable Color color;
    // Item Model Key
    private @Nullable NamespacedKey model;
    // Item Flags
    private @NonNull List<ItemFlag> itemFlags = new ArrayList<>();
    // Decorated Pot Sherds
    private @Nullable Material frontSherd;
    private @Nullable Material leftSherd;
    private @Nullable Material rightSherd;
    private @Nullable Material backSherd;
    // Armor Trim
    private @Nullable ArmorTrim armorTrim;
    // Instrument
    private @Nullable MusicInstrument instrument;
    // Attributes
    private @NonNull Map<Attribute, AttributeModifier> attributes = new HashMap<>();
    // OfflinePlayer - Used for player skulls
    private @Nullable OfflinePlayer offlinePlayer;

    // Options
    private @Nullable Boolean enchantmentGlint;
    private @Nullable Boolean unbreakable;
    private @Nullable Boolean fireResistant;
    private @Nullable Boolean hideToolTip;
    private @Nullable Boolean glider;

    /**
     * This Constructor should not be used. See {@link ItemStackBuilder#ItemStackBuilder(ComponentLogger)}
     * @throws RuntimeException This Constructor should not be used. See {@link ItemStackBuilder#ItemStackBuilder(ComponentLogger)}
     * @deprecated This Constructor should not be used. See {@link ItemStackBuilder#ItemStackBuilder(ComponentLogger)}
     */
    @Deprecated
    public ItemStackBuilder() {
        throw new RuntimeException("Use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param logger The plugin's ComponentLogger for displaying any errors.
     */
    public ItemStackBuilder(@NonNull ComponentLogger logger) {
        this.logger = logger;
    }

    /**
     * Takes an {@link ItemStackConfig} record to parse and populate required information.
     * @param config The {@link ItemStackConfig} to parse.
     * @param player An optional {@link Player} to use when parsing PlaceholderAPI placeholders when formatting an item's name and lore.
     * @param offlinePlayer An optional {@link OfflinePlayer} to use when parsing PlaceholderAPI placeholders when formatting an item's name and lore.
     * @param placeholders A list of placeholders to replace when formatting an item's name and lore.
     * @return The current {@link ItemStackBuilder}.
     * @deprecated Use {@link #fromItemStackConfig(ItemStackConfig, OfflinePlayer, List)} instead. This method just calls that method.
     */
    @Deprecated(forRemoval = true)
    public @NonNull ItemStackBuilder fromItemStackConfig(
            @NonNull ItemStackConfig config,
            @Nullable Player player,
            @Nullable OfflinePlayer offlinePlayer,
            @NonNull List<TagResolver.Single> placeholders) {
        if(player != null) {
            return fromItemStackConfig(config, player, placeholders);
        } else if(offlinePlayer != null) {
            return fromItemStackConfig(config, offlinePlayer, placeholders);
        } else {
            return fromItemStackConfig(config, null, placeholders);
        }
    }

    /**
     * Takes an {@link ItemStackConfig} record to parse and populate required information.
     * @param config The {@link ItemStackConfig} to parse.
     * @param offlinePlayer The {@link OfflinePlayer} to use when parsing PlaceholderAPI placeholders when formatting an item's name and lore. May be null
     * @param placeholders A list of placeholders to replace when formatting an item's name and lore.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder fromItemStackConfig(
            @NonNull ItemStackConfig config,
            @Nullable OfflinePlayer offlinePlayer,
            @NonNull List<TagResolver.Single> placeholders) {
        if(config.itemType() == null) {
            logger.error(AdventureUtil.deserialize("Unable to parse ItemStackConfig due to the ItemType being null."));
            return this;
        }

        // Get the ItemType from the registry.
        this.itemType = config.itemType();

        // Check if the max stack size is configured.
        // If not, use the default ItemType's max stack size.
        if(config.maxStackSize() != null) {
            // If configured, check if it is inside the valid bounds.
            // If not, display a warning and use the default ItemType's max stack size.
            if(config.maxStackSize() >= 1 && config.maxStackSize() <= 99) {
                maxStackSize = config.maxStackSize();
            } else {
                logger.warn(AdventureUtil.deserialize("Max stack size is limited to greater than or equal to 1 and less than or equal to 99."));
                logger.warn(AdventureUtil.deserialize("The default max stack size will be used instead."));
                maxStackSize = itemType.getMaxStackSize();
            }
        } else {
            maxStackSize = itemType.getMaxStackSize();
        }

        // Check if the configured amount is valid
        if(config.amount() != null) {
            this.amount = config.amount();
        }

        // Format the name and lore.
        // The Offline Player is used to parse any Adventure/MiniMessage placeholders or PlaceholderAPI placeholders.
        if(offlinePlayer != null) {
            // If a name is configured, format the name.
            if(config.name() != null) name = AdventureUtil.deserialize(offlinePlayer, config.name(), placeholders);
            // Format the lore.
            lore = config.lore().stream().map(line -> AdventureUtil.deserialize(offlinePlayer, line, placeholders)).toList();

            this.offlinePlayer = offlinePlayer;
        } else {
            // If a name is configured, format the name.
            if(config.name() != null) name = AdventureUtil.deserialize(config.name(), placeholders);
            // Format the lore.
            lore = config.lore().stream().map(line -> AdventureUtil.deserialize(line, placeholders)).toList();
        }

        if(config.entityType() != null) {
            this.entityType = config.entityType();
        }

        // Validate and apply configured enchantments
        for(ItemStackConfig.EnchantmentConfig enchantmentConfig : config.enchantments()) {
            if(enchantmentConfig.enchantment() == null) {
                logger.warn(AdventureUtil.deserialize("Unable to process enchantment due to a null enchantment."));
                continue;
            }

            if(enchantmentConfig.level() == null) {
                logger.warn(AdventureUtil.deserialize("Unable to process enchantment due to a missing enchantment level for enchantment: " + FormatUtil.formatKey(enchantmentConfig.enchantment().getKey()) + "."));
                continue;
            }

            enchantments.put(enchantmentConfig.enchantment(), enchantmentConfig.level());
        }

        // Parse the PotionConfig
        ItemStackConfig.PotionConfig potionConfig = config.potionConfig();
        if(potionConfig.potionType() != null) {
            this.potionType = potionConfig.potionType();
        }

        // Validate and apply any extra potion effects.
        for(ItemStackConfig.PotionEffectConfig potionEffectConfig : potionConfig.potionEffects()) {
            // Send a warning if the potion effect type name is null.
            if(potionEffectConfig.type() == null) {
                logger.warn(AdventureUtil.deserialize("Unable to parse potion effect type due to an invalid potion effect type name."));
                continue;
            }

            // Send a warning if the potion effect durationSeconds is null.
            if(potionEffectConfig.durationSeconds() == null || potionEffectConfig.durationSeconds() <= 0) {
                logger.warn(AdventureUtil.deserialize("The duration seconds for potion effect type: " + potionEffectConfig.type() + " is null or less than or equal to 0."));
                continue;
            }

            // Send a warning if the potion effect amplifier is null.
            if(potionEffectConfig.amplifier() == null || potionEffectConfig.amplifier() < 0) {
                logger.warn(AdventureUtil.deserialize("The amplifier for potion effect type: " + potionEffectConfig.type() + "is null or less than 0."));
                continue;
            }

            // Calculate the duration in ticks
            double durationInTicks = (potionEffectConfig.durationSeconds() * 20);
            // Convert the durationInTicks to an int
            int ticks = (int) durationInTicks;
            // Create the PotionEffect
            PotionEffect potionEffect = potionEffectConfig.type().createEffect(ticks, potionEffectConfig.amplifier());
            // Add the created PotionEffect to the list of PotionEffects.
            potionEffects.add(potionEffect);
        }

        // Create the Color object to be applied to armor that can be dyed (i.e., leather).
        if(config.color().random()) {
            Random random = new Random();

            // If configured to use a random color, generate the random color.
            color = Color.fromRGB(random.nextInt(0, 255), random.nextInt(0, 255), random.nextInt(0, 255));
        } else {
            Integer red = config.color().red();
            Integer green = config.color().green();
            Integer blue = config.color().blue();

            // If all color values are valid, create the Color object from the red, green, and blue values.
            if(isValidColorValue(red, "red") && isValidColorValue(green, "green") && isValidColorValue(blue, "blue")) {
                color = Color.fromRGB(red, green, blue);
            }
        }

        // If a model name is configured, attempt to create the NamespacedKey for the model.
        if(config.modelName() != null) {
            // Create the NamespacedKey for the item model.
            RegistryUtil.createNamespacedKey(config.modelName()).ifPresent(key -> model = key);
        }

        // If any item flags are configured, parse the flag name to an ItemFlag and add it to the list of item flags.
        // Otherwise, display a warning for any invalid flag names.
        for(String flagName : config.itemFlags()) {
            try {
                itemFlags.add(ItemFlag.valueOf(flagName));
            } catch(IllegalArgumentException ignored) {
                logger.warn(AdventureUtil.deserialize("Invalid item flag name: " + flagName + "."));
            }
        }

        // If any sherds are configured, parse the Material names.
        ItemStackConfig.DecoratedPotConfig decoratedPot = config.decoratedPot();
        if(decoratedPot.frontSherd() != null) {
            this.frontSherd = decoratedPot.frontSherd();
        }
        if(decoratedPot.leftSherd() != null) {
            leftSherd = decoratedPot.leftSherd();
        }
        if(decoratedPot.rightSherd() != null) {
            rightSherd = decoratedPot.rightSherd();
        }
        if(decoratedPot.backSherd() != null) {
            backSherd = decoratedPot.backSherd();
        }

        // If an armor trim pattern and material are configured, attempt to create the ArmorTrim
        ItemStackConfig.ArmorTrimConfig armorTrimConfig = config.armorTrim();
        if(armorTrimConfig.trimPattern() != null && armorTrimConfig.trimMaterial() != null) {
            armorTrim = new ArmorTrim(armorTrimConfig.trimMaterial(), armorTrimConfig.trimPattern());
        } else if(armorTrimConfig.trimPattern() == null && armorTrimConfig.trimMaterial() != null) {
            logger.warn(AdventureUtil.deserialize("No armor trim pattern configured, but an armor trim material was configured."));
        } else if(armorTrimConfig.trimPattern() != null) {
            logger.warn(AdventureUtil.deserialize("No armor trim material configured, but an armor trim pattern was configured."));
        }

        // Instrument
        if(config.instrument() != null) {
            this.instrument = config.instrument();
        }

        // If any attributes are configured, parse the attribute config and add it to the attribute map.
        // Displays a warning for any invalid configuration or parsing errors.
        for(ItemStackConfig.AttributeConfig attributeConfig : config.attributes()) {
            // If the attribute name is null, display a warning and process the next object in the list.
            if(attributeConfig.attribute() == null) {
                logger.warn(AdventureUtil.deserialize("Invalid attribute."));
                continue;
            }

            // If the attribute amount is null, display a warning and process the next object in the list.
            if(attributeConfig.amount() == null) {
                logger.warn(AdventureUtil.deserialize("Invalid attribute value."));
                continue;
            }

            // If the attribute operation is null, display a warning and process the next object in the list.
            if(attributeConfig.operation() == null) {
                logger.warn(AdventureUtil.deserialize("Invalid attribute operation."));
                continue;
            }

            // Create the AttributeModifier.
            AttributeModifier attributeModifier;
            // If the EquipmentSlot is valid, create the modifier using that. Otherwise, create the modifier without one.
            if(attributeConfig.equipmentSlot() != null) {
                attributeModifier = new AttributeModifier(attributeConfig.attribute().getKey(), amount, attributeConfig.operation(), attributeConfig.equipmentSlot().getGroup());
            } else {
                attributeModifier = new AttributeModifier(attributeConfig.attribute().getKey(), amount, attributeConfig.operation());
            }

            // Add the attribute and attribute modifier to the attributes map.
            attributes.put(attributeConfig.attribute(), attributeModifier);
        }

        // Apply any other configuration options.
        enchantmentGlint = config.options().enchantmentGlint();
        unbreakable = config.options().unbreakable();
        fireResistant = config.options().fireResistant();
        hideToolTip = config.options().hideToolTip();
        glider = config.options().glider();

        return this;
    }

    /**
     * Creates an ItemStack based on the provided data inside the {@link ItemStackBuilder}.
     * @return an {@link Optional} containing the created {@link ItemStack} if the data is valid,
     * or an empty {@link Optional} if the data is invalid.
     */
    public @NonNull Optional<@NonNull ItemStack> buildItemStack() {
        // Create a copy of the base ItemStack or create a new ItemStack using the ItemType. Handle any errors as needed.
        ItemStack itemStack;
        if(baseItemStack == null && itemType == null) {
            logger.error(AdventureUtil.deserialize("Unable to create an ItemStack due to an invalid base ItemStack or ItemType."));
            return Optional.empty();
        } else if(itemType == null) {
            logger.error(AdventureUtil.deserialize("Unable to create an ItemStack due to an invalid ItemType."));
            return Optional.empty();
        } else {
            itemStack = Objects.requireNonNullElseGet(baseItemStack, () -> itemType.createItemStack(amount));
        }

        // If the created ItemStack's type is AIR, return the item stack as-is.
        if(itemStack.getType().equals(Material.AIR)) {
            return Optional.of(itemStack);
        }

        // Get itemStack's ItemMeta.
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set the max stack size.
        itemMeta.setMaxStackSize(maxStackSize);
        // Set the display name.
        itemMeta.customName(name);
        // Set the lore.
        itemMeta.lore(lore);
        // If the enchantment glint is configured, set that here.
        if(enchantmentGlint != null) itemMeta.setEnchantmentGlintOverride(enchantmentGlint);
        // If the unbreakable option is configured, set that here.
        if(unbreakable != null) itemMeta.setUnbreakable(unbreakable);
        // If the fire-resistant option is configured, set that here.
        if(fireResistant != null) {
            if(fireResistant) itemMeta.setDamageResistant(DamageTypeTags.IS_FIRE);
        }
        // If the hide tool tips option is configured, set that here.
        if(hideToolTip != null) itemMeta.setHideTooltip(hideToolTip);
        // If the glider option is configured, set that here.
        if(glider != null) itemMeta.setGlider(glider);

        // If a custom model is configured, set that here.
        if(model != null) itemMeta.setItemModel(model);
        // If any item flags were configured, set them here.
        itemFlags.forEach(itemMeta::addItemFlags);
        // If any attributes were configured, set them here.
        attributes.forEach(itemMeta::addAttributeModifier);

        // Apply data that is specific to certain items.
        switch(itemMeta) {
            case PotionMeta potionMeta -> {
                if(potionType != null) potionMeta.setBasePotionType(potionType);

                potionEffects.forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, true));

                applyEnchantments(potionMeta);

                itemStack.setItemMeta(potionMeta);
            }

            case SuspiciousStewMeta suspiciousStewMeta -> {
                potionEffects.forEach(potionEffect -> suspiciousStewMeta.addCustomEffect(SuspiciousEffectEntry.create(potionEffect.getType(), potionEffect.getDuration()), true));

                applyEnchantments(suspiciousStewMeta);

                itemStack.setItemMeta(suspiciousStewMeta);
            }

            case ColorableArmorMeta colorableArmorMeta -> {
                if(color != null) colorableArmorMeta.setColor(color);

                if(armorTrim != null) colorableArmorMeta.setTrim(armorTrim);

                applyEnchantments(colorableArmorMeta);

                setItemMeta(itemStack, colorableArmorMeta);
            }

            case ArmorMeta armorMeta -> {
                if(armorTrim != null) armorMeta.setTrim(armorTrim);

                applyEnchantments(armorMeta);

                setItemMeta(itemStack, armorMeta);
            }

            case LeatherArmorMeta leatherArmorMeta -> {
                if(color != null) leatherArmorMeta.setColor(color);

                applyEnchantments(leatherArmorMeta);

                setItemMeta(itemStack, leatherArmorMeta);
            }

            case EnchantmentStorageMeta enchantmentStorageMeta -> {
                applyEnchantments(enchantmentStorageMeta);

                setItemMeta(itemStack, enchantmentStorageMeta);
            }

            case BlockStateMeta blockStateMeta -> {
                BlockState state = blockStateMeta.getBlockState();
                if(state instanceof CreatureSpawner creatureSpawner) {
                    if(entityType != null) {
                        creatureSpawner.setSpawnedType(entityType);
                        blockStateMeta.setBlockState(creatureSpawner);
                    }
                } else if(state instanceof DecoratedPot decoratedPot) {
                    if(frontSherd != null) decoratedPot.setSherd(DecoratedPot.Side.FRONT, frontSherd);
                    if(leftSherd != null) decoratedPot.setSherd(DecoratedPot.Side.LEFT, leftSherd);
                    if(rightSherd != null) decoratedPot.setSherd(DecoratedPot.Side.RIGHT, rightSherd);
                    if(backSherd != null) decoratedPot.setSherd(DecoratedPot.Side.BACK, backSherd);
                }

                applyEnchantments(blockStateMeta);

                setItemMeta(itemStack, blockStateMeta);
            }

            case SkullMeta skullMeta -> {
                skullMeta.setOwningPlayer(offlinePlayer);

                applyEnchantments(skullMeta);

                setItemMeta(itemStack, skullMeta);
            }

            case MusicInstrumentMeta musicInstrumentMeta -> {
                if(instrument != null) musicInstrumentMeta.setInstrument(instrument);

                applyEnchantments(musicInstrumentMeta);

                setItemMeta(itemStack, musicInstrumentMeta);
            }

            default -> {
                applyEnchantments(itemMeta);

                setItemMeta(itemStack, itemMeta);
            }
        }

        return Optional.of(itemStack);
    }

    /**
     * Checks if the value is non-null and greater than or equal to 0 and less than or equal to 255.
     * @param value The {@link Integer} value to check.
     * @param colorName The name of the color value being checked. For logging purposes, either red, green, or blue.
     * @return true if valid, false if not.
     */
    private boolean isValidColorValue(@Nullable Integer value, @NonNull String colorName) {
        if(value == null) return false;

        if (value < 0 || value > 255) {
            logger.warn(AdventureUtil.deserialize("The " + colorName + " color value must be greater than or equal to 0 and less than or equal to 255."));
            return false;
        }

        return true;
    }

    /**
     * Applies {@link Enchantment}s to the provided {@link ItemMeta}.
     * @param itemMeta The {@link ItemMeta} to apply {@link Enchantment}s to.
     * @return The current {@link ItemStackBuilder}.
     */
    private @NonNull ItemStackBuilder applyEnchantments(@NonNull ItemMeta itemMeta) {
        if(itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            enchantments.forEach((enchantment, level) -> enchantmentStorageMeta.addStoredEnchant(enchantment, level, true));
        } else {
            enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
        }

        return this;
    }

    /**
     * Sets the {@link ItemStack}'s {@link ItemMeta}.
     * @param itemStack The {@link ItemStack}
     * @param itemMeta The {@link ItemMeta}.
     * @return The current {@link ItemStackBuilder}.
     */
    private @NonNull ItemStackBuilder setItemMeta(@NonNull ItemStack itemStack, @NonNull ItemMeta itemMeta) {
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    /**
     * Sets the base {@link ItemStack} to use in the final ItemStack creation.
     * @param itemStack A {@link ItemStack} to use as a base.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setBaseItemStack(@NonNull ItemStack itemStack) {
        this.baseItemStack = itemStack;
        return this;
    }

    /**
     * Sets the {@link ItemType} to use when creating the {@link ItemStack}.
     * The {@link ItemType} is ignored if a base {@link ItemStack} is supplied.
     * @param itemType A {@link ItemType} to use to create the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setItemType(@NonNull ItemType itemType) {
        this.itemType = itemType;
        return this;
    }

    /**
     * Set the amount of items in the {@link ItemStack}.
     * @param amount The amount of items in the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setAmount(int amount) {
        this.amount = amount;

        return this;
    }

    /**
     * Sets the maximum amount of items the {@link ItemStack} can contain.
     * The amount must be {@literal >}= 1 or {@literal <}= 99.
     * @param amount The maximum amount of items the {@link ItemStack} can contain.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setMaxStackSize(int amount) {
        if(amount >= 1 && amount <= 99) {
            maxStackSize = amount;
        } else {
            logger.warn(AdventureUtil.deserialize("Max stack size is limited to greater than or equal to 1 and less than or equal to 99."));
        }

        return this;
    }

    /**
     * Sets the name to give the {@link ItemStack}.
     * @param name The name to give the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setName(@NonNull Component name) {
        this.name = name;

        return this;
    }

    /**
     * Sets the lore to give the {@link ItemStack}.
     * @param lore The lore to give the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setLore(@NonNull List<Component> lore) {
        this.lore = lore;

        return this;
    }

    /**
     * Sets the {@link EntityType} the {@link ItemStack} associated with the {@link ItemStack}.
     * This is exclusively used for spawners.
     * @param entityType The {@link EntityType} to give the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setEntityType(@NonNull EntityType entityType) {
        this.entityType = entityType;

        return this;
    }

    /**
     * Sets the enchantments the {@link ItemStack} should have.
     * This will replace the existing {@link #enchantments} map with the one provided.
     * @param enchantments A {@link Map} containing the mapping of {@link Enchantment}s and enchantment levels to give the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setEnchantments(@NonNull Map<@NonNull Enchantment, @NonNull Integer> enchantments) {
        this.enchantments = enchantments;

        return this;
    }

    /**
     * Adds an {@link Enchantment} and enchantment level to the existing {@link #enchantments} map.
     * @param enchantment The {@link Enchantment} to add.
     * @param level The enchantment level to add.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder addEnchantment(@NonNull Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);

        return this;
    }

    /**
     * Sets the {@link PotionType} the {@link ItemStack} should have.
     * This is only used for {@link ItemStack}s that can have potions types.
     * @param potionType The {@link PotionType} to give the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setPotionType(@NonNull PotionType potionType) {
        this.potionType = potionType;

        return this;
    }

    /**
     * Sets a {@link List} of {@link PotionEffect}s the {@link ItemStack} should have.
     * This is only used for {@link ItemStack}s that can have potions effects.
     * @param potionEffects The {@link List} of {@link PotionEffect}s to give the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setPotionEffects(@NonNull List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;

        return this;
    }

    /**
     * Adds a {@link PotionEffect}s to the existing list of potion effects the {@link ItemStack} should have.
     * This is only used for {@link ItemStack}s that can have potions effects.
     * @param potionEffect The {@link PotionEffect} to give the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder addPotionEffect(@NonNull PotionEffect potionEffect) {
        potionEffects.add(potionEffect);

        return this;
    }

    /**
     * Sets the {@link Color} to apply to any dyeable {@link ItemStack}s.
     * @param color The {@link Color} to apply to the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setColor(@NonNull Color color) {
        this.color = color;

        return this;
    }

    /**
     * Sets the {@link NamespacedKey} of the item model to apply to the {@link ItemStack}.
     * @param key The {@link NamespacedKey} of the item model to apply to the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setModel(@NonNull NamespacedKey key) {
        this.model = key;

        return this;
    }

    /**
     * Sets the {@link List} of {@link ItemFlag}s that will be hidden for the {@link ItemStack}.
     * This replaces the {@link #itemFlags} list.
     * @param itemFlags The {@link List} of {@link ItemFlag}s that will be hidden for the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setItemFlags(@NonNull List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;

        return this;
    }

    /**
     * Adds an {@link ItemFlag} to the {@link #itemFlags} list.
     * @param itemFlag The {@link ItemFlag} to add to the {@link #itemFlags} list.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder addItemFlag(@NonNull ItemFlag itemFlag) {
        itemFlags.add(itemFlag);

        return this;
    }

    /**
     * Removes an {@link ItemFlag} from the {@link #itemFlags} list.
     * @param itemFlag The {@link ItemFlag} to remove from the {@link #itemFlags} list.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder removeItemFlag(@NonNull ItemFlag itemFlag) {
        itemFlags.remove(itemFlag);

        return this;
    }

    /**
     * Sets the {@link Material} to use for the sherd on the front of a decorated pot.
     * @param frontSherd The {@link Material} to use for the sherd on the front of a decorated pot.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setFrontSherd(@NonNull Material frontSherd) {
        this.frontSherd = frontSherd;

        return this;
    }

    /**
     * Sets the {@link Material} to use for the sherd on the left of a decorated pot.
     * @param leftSherd The {@link Material} to use for the sherd on the left of a decorated pot.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setLeftSherd(@NonNull Material leftSherd) {
        this.leftSherd = leftSherd;

        return this;
    }

    /**
     * Sets the {@link Material} to use for the sherd on the right of a decorated pot.
     * @param rightSherd The {@link Material} to use for the sherd on the right of a decorated pot.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setRightSherd(@NonNull Material rightSherd) {
        this.rightSherd = rightSherd;

        return this;
    }

    /**
     * Sets the {@link Material} to use for the sherd on the back of a decorated pot.
     * @param backSherd The {@link Material} to use for the sherd on the back of a decorated pot.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setBackSherd(@NonNull Material backSherd) {
        this.backSherd = backSherd;

        return this;
    }

    /**
     * Sets the {@link ArmorTrim} to apply to armor {@link ItemStack}s.
     * @param armorTrim The {@link ArmorTrim} to apply to armor {@link ItemStack}s.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setArmorTrim(@NonNull ArmorTrim armorTrim) {
        this.armorTrim = armorTrim;

        return this;
    }

    /**
     * Creates an {@link ArmorTrim} to apply to armor {@link ItemStack}s.
     * @param trimPattern The {@link TrimPattern}.
     * @param trimMaterial The {@link TrimMaterial}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setArmorTrim(@NonNull TrimPattern trimPattern, @NonNull TrimMaterial trimMaterial) {
        armorTrim = new ArmorTrim(trimMaterial, trimPattern);

        return this;
    }

    /**
     * Sets the {@link MusicInstrument} to apply to goat horn {@link ItemStack}s.
     * @param instrument The {@link MusicInstrument} to apply to goat horn {@link ItemStack}s.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setInstrument(@NonNull MusicInstrument instrument) {
        this.instrument = instrument;

        return this;
    }

    /**
     * Sets the {@link Map} of {@link Attribute}s that will be applied to the {@link ItemStack}.
     * This replaces the {@link #attributes} map.
     * @param attributes The {@link List} of {@link Attribute}s that will be applied to the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setAttributes(@NonNull Map<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;

        return this;
    }

    /**
     * Adds an {@link Attribute} and {@link AttributeModifier} to the {@link #attributes} map.
     * @param attribute The {@link Attribute}
     * @param attributeModifier The {@link AttributeModifier}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder addAttribute(@NonNull Attribute attribute, @NonNull AttributeModifier attributeModifier) {
        attributes.put(attribute, attributeModifier);

        return this;
    }

    /**
     * Removes an {@link Attribute} from the {@link #attributes} map.
     * @param attribute The {@link Attribute}
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder removeAttribute(@NonNull Attribute attribute) {
        attributes.remove(attribute);

        return this;
    }

    /**
     * Set the {@link OfflinePlayer} to associated with the {@link ItemStack} if it is a player head.
     * @param offlinePlayer The {@link OfflinePlayer}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setPlayer(@NonNull OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;

        return this;
    }

    /**
     * Set the {@link #enchantmentGlint} option.
     * @param enchantmentGlint true means the {@link ItemStack} will have glint, false means the {@link ItemStack} will never have a glint, null will use the default value of the {@link ItemStack}.
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setEnchantmentGlint(@Nullable Boolean enchantmentGlint) {
        this.enchantmentGlint = enchantmentGlint;

        return this;
    }

    /**
     * Set the {@link #fireResistant} option.
     * @param fireResistant true means the {@link ItemStack} will be fire-resistant, false means the {@link ItemStack} will never be fire-resistant, and null will use the default value of the {@link ItemStack}
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setFireResistant(@Nullable Boolean fireResistant) {
        this.fireResistant = fireResistant;

        return this;
    }

    /**
     * Set the {@link #fireResistant} option.
     * @param glider true means the {@link ItemStack} will act like an Elytra, false means the {@link ItemStack} will never act like an Elytra, and null will use the default value of the {@link ItemStack}
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setGlider(@Nullable Boolean glider) {
        this.glider = glider;

        return this;
    }

    /**
     * Set the {@link #hideToolTip} option.
     * @param hideToolTip true means the {@link ItemStack} will hide tool tips, false means the {@link ItemStack} will never hide tool tips, and null will use the default value of the {@link ItemStack}
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setHideToolTip(@Nullable Boolean hideToolTip) {
        this.hideToolTip = hideToolTip;

        return this;
    }

    /**
     * Set the {@link #unbreakable} option.
     * @param unbreakable true means the {@link ItemStack} will never lose durability, false means the {@link ItemStack} will lose durability, and null will use the default value of the {@link ItemStack}
     * @return The current {@link ItemStackBuilder}.
     */
    public @NonNull ItemStackBuilder setUnbreakable(@Nullable Boolean unbreakable) {
        this.unbreakable = unbreakable;

        return this;
    }

    /**
     * Get the current {@link #baseItemStack} that may be used to create the final {@link ItemStack}. May be null.
     * @return An {@link ItemStack} or null.
     */
    public @Nullable ItemStack getBaseItemStack() {
        return baseItemStack;
    }

    /**
     * Get the current {@link #itemType} that may be used once the {@link ItemStack} is created. May be null.
     * @return An {@link ItemType} or null.
     */
    public @Nullable ItemType getItemType() {
        return itemType;
    }

    /**
     * Get the {@link #amount} of items that will be in the {@link ItemStack} once created.
     * @return The amount of items that will be in the {@link ItemStack}.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Get the {@link #maxStackSize} that will be applied to the {@link ItemStack} once created. May be null.
     * @return The max stack size as an Integer or null.
     */
    public @Nullable Integer getMaxStackSize() {
        return maxStackSize;
    }

    /**
     * Get the {@link #name} that will be applied to the {@link ItemStack} once created. May be null.
     * @return A {@link Component} representing the name or null.
     */
    public @Nullable Component getName() {
        return name;
    }

    /**
     * Get the {@link #lore} that will be applied to the {@link ItemStack} once created. Never null, but may be an empty list.
     * @return A {@link List} of {@link Component}. Never null, but the list may be empty.
     */
    public @NonNull List<Component> getLore() {
        return lore;
    }

    /**
     * Get the {@link #enchantments} that will be applied to the {@link ItemStack} once created. Never null, but may be an empty map.
     * @return A {@link Map} of {@link Enchantment}s to levels. Never null, but the map may be empty.
     */
    public @NonNull Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    /**
     * Get the current {@link #entityType} that may be used once the {@link ItemStack} is created. May be null.
     * @return An {@link EntityType} or null.
     */
    public @Nullable EntityType getEntityType() {
        return entityType;
    }

    /**
     * Get the current {@link #potionType} that may be used once the {@link ItemStack} is created. May be null.
     * @return An {@link PotionType} or null.
     */
    public @Nullable PotionType getPotionType() {
        return potionType;
    }

    /**
     * Get the {@link #potionEffects} that may be applied to the {@link ItemStack} once created. Never null, but may be an empty list.
     * @return A {@link List} of {@link PotionEffect}. Never null, but the list may be empty.
     */
    public @NonNull List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    /**
     * Get the current {@link #color} that may be used once the {@link ItemStack} is created. May be null.
     * @return A {@link Color} or null.
     */
    public @Nullable Color getColor() {
        return color;
    }

    /**
     * Get the current {@link #model} that may be used once the {@link ItemStack} is created. May be null.
     * @return A {@link NamespacedKey} or null.
     */
    public @Nullable NamespacedKey getModel() {
        return model;
    }

    /**
     * Get the {@link #itemFlags} that will be applied to the {@link ItemStack} once created. Never null, but may be an empty list.
     * @return A {@link List} of {@link ItemFlag}. Never null, but the list may be empty.
     */
    public @NonNull List<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    /**
     * Get the {@link #attributes} that will be applied to the {@link ItemStack} once created. Never null, but may be an empty map.
     * @return A {@link Map} of {@link Attribute}s to {@link AttributeModifier}s. Never null, but the map may be empty.
     */
    public @NonNull Map<Attribute, AttributeModifier> getAttributes() {
        return attributes;
    }

    /**
     * Get the {@link #offlinePlayer} that may be applied to the {@link ItemStack} once created. May be null.
     * @return A {@link OfflinePlayer} or null.
     */
    public @Nullable OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    /**
     * Get the {@link #enchantmentGlint} option. May be null.
     * @return A {@link Boolean} or null.
     */
    public @Nullable Boolean getEnchantmentGlint() {
        return enchantmentGlint;
    }

    /**
     * Get the {@link #unbreakable} option. May be null.
     * @return A {@link Boolean} or null.
     */
    public @Nullable Boolean getUnbreakable() {
        return unbreakable;
    }

    /**
     * Get the {@link #fireResistant} option. May be null.
     * @return A {@link Boolean} or null.
     */
    public @Nullable Boolean getFireResistant() {
        return fireResistant;
    }

    /**
     * Get the {@link #hideToolTip} option. May be null.
     * @return A {@link Boolean} or null.
     */
    public @Nullable Boolean getHideToolTip() {
        return hideToolTip;
    }

    /**
     * Get the {@link #glider} option. May be null.
     * @return A {@link Boolean} or null.
     */
    public @Nullable Boolean getGlider() {
        return glider;
    }
}
