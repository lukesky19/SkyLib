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
package com.github.lukesky19.skylib.itemstack;

import com.github.lukesky19.skylib.adventure.AdventureUtil;
import com.github.lukesky19.skylib.registry.RegistryUtil;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.tag.DamageTypeTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private @NotNull List<Component> lore = new ArrayList<>();
    // Enchantments
    private @NotNull Map<Enchantment, Integer> enchantments = new HashMap<>();
    // EntityType - Only used for Spawners.
    private @Nullable EntityType entityType;
    // Potion Type - Only used for Potions
    private @Nullable PotionType potionType;
    // PotionEffects - Used for Potions and Suspicious Stew
    private @NotNull List<PotionEffect> potionEffects = new ArrayList<>();
    // Color - Used for dyeable items.
    private @Nullable Color color;
    // Item Model Key
    private @Nullable NamespacedKey model;
    // Item Flags
    private @NotNull List<ItemFlag> itemFlags = new ArrayList<>();
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
    private @NotNull Map<Attribute, AttributeModifier> attributes = new HashMap<>();
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
    public ItemStackBuilder(@NotNull ComponentLogger logger) {
        this.logger = logger;
    }

    /**
     * Takes an {@link ItemStackConfig} record to parse and populate required information.
     * @param config The {@link ItemStackConfig} to parse.
     * @param player An optional {@link Player} to use when parsing PlaceholderAPI placeholders when formatting an item's name and lore.
     * @param offlinePlayer An optional {@link OfflinePlayer} to use when parsing PlaceholderAPI placeholders when formatting an item's name and lore.
     * @param placeholders A list of placeholders to replace when formatting an item's name and lore.
     */
    public void fromItemStackConfig(
            @NotNull ItemStackConfig config,
            @Nullable Player player,
            @Nullable OfflinePlayer offlinePlayer,
            @NotNull List<TagResolver.Single> placeholders) {
        if(config.itemType() == null) {
            logger.error(AdventureUtil.serialize("Unable to parse ItemStackConfig due to the ItemType being null."));
            return;
        }

        // Get the ItemType from the registry.
        Optional<ItemType> optionalItemType = RegistryUtil.getItemType(logger, config.itemType());
        // If the ItemType is present, set the class variable. Otherwise, display error and info messages.
        optionalItemType.ifPresentOrElse(
                itemType -> this.itemType = itemType,
                () -> logger.error(AdventureUtil.serialize("Unable to get a ItemType due to a configuration error.")));

        // If the ItemType is null, exit the method.
        if(itemType == null) return;

        // Check if the max stack size is configured.
        // If not, use the default ItemType's max stack size.
        if(config.maxStackSize() != null) {
            // If configured, check if it is inside the valid bounds.
            // If not, display a warning and use the default ItemType's max stack size.
            if(config.maxStackSize() >= 1 && config.maxStackSize() <= 99) {
                maxStackSize = config.maxStackSize();
            } else {
                logger.warn(AdventureUtil.serialize("Max stack size is limited to greater than or equal to 1 and less than or equal to 99."));
                logger.warn(AdventureUtil.serialize("The default max stack size will be used instead."));
                maxStackSize = itemType.getMaxStackSize();
            }
        } else {
            maxStackSize = itemType.getMaxStackSize();
        }

        if(config.amount() == null) {
            logger.error(AdventureUtil.serialize("There is no configured amount for the ItemStack."));
            return;
        }

        // Check if the configured amount is inside the valid bounds.
        // If not, clamp the amount to be always valid and display a warning message.
        if(config.amount() >= 1 && config.amount() <= maxStackSize) {
            amount = config.amount();
        } else {
            logger.warn(AdventureUtil.serialize("The amount is limited to greater than or equal to 1 and less than or equal to 99."));
            logger.warn(AdventureUtil.serialize("The amount will be clamped to it's min or max value."));
            amount = Math.max(1, Math.min(maxStackSize, config.amount()));
        }

        // Format the name and lore.
        // The Player or Offline Player is used to parse any Adventure/MiniMessage placeholders or PlaceholderAPI placeholders.
        if(player != null) {
            // If a name is configured, format the name.
            if(config.name() != null) name = AdventureUtil.serialize(player, config.name(), placeholders);
            // Format the lore.
            lore = config.lore().stream().map(line -> AdventureUtil.serialize(player, line, placeholders)).toList();

            this.offlinePlayer = player;
        } else if(offlinePlayer != null) {
            // If a name is configured, format the name.
            if(config.name() != null) name = AdventureUtil.serialize(offlinePlayer, config.name(), placeholders);
            // Format the lore.
            lore = config.lore().stream().map(line -> AdventureUtil.serialize(offlinePlayer, line, placeholders)).toList();

            this.offlinePlayer = offlinePlayer;
        } else {
            // If a name is configured, format the name.
            if(config.name() != null) name = AdventureUtil.serialize(config.name(), placeholders);
            // Format the lore.
            lore = config.lore().stream().map(line -> AdventureUtil.serialize(line, placeholders)).toList();
        }

        // Attempt to get the EntityType from the registry if an EntityType is configured.
        if(config.entityType() != null) {
            @NotNull Optional<@NotNull EntityType> optionalEntityType = RegistryUtil.getEntityType(logger, config.entityType());
            optionalEntityType.ifPresent(entityType -> this.entityType = entityType);
        }

        // Validate and apply configured enchantments
        for(ItemStackConfig.EnchantmentConfig enchantmentConfig : config.enchantments()) {
            if(enchantmentConfig.enchantment() == null) {
                logger.warn(AdventureUtil.serialize("Unable to process enchantment due to a null enchantment name."));
                continue;
            }

            if(enchantmentConfig.level() == null) {
                logger.warn(AdventureUtil.serialize("Unable to process enchantment due to a missing enchantment level for enchantment: " + enchantmentConfig.enchantment() + "."));
                continue;
            }

            @NotNull Optional<@NotNull Enchantment> optionalEnchantment = RegistryUtil.getEnchantment(logger, enchantmentConfig.enchantment());
            optionalEnchantment.ifPresent(enchantment -> enchantments.put(enchantment, enchantmentConfig.level()));
        }

        // Parse the PotionConfig
        ItemStackConfig.PotionConfig potionConfig = config.potionConfig();
        // If a PotionType name is configured, attempt to get the PotionType from the registry.
        if(potionConfig.potionType() != null) {
            @NotNull Optional<@NotNull PotionType> optionalPotionType = RegistryUtil.getPotionType(logger, potionConfig.potionType());
            optionalPotionType.ifPresentOrElse(
                    potionType -> this.potionType = potionType,
                    () -> logger.error(AdventureUtil.serialize("Unable to get a PotionType due to a configuration error.")));
        }

        // Validate and apply any extra potion effects.
        for(ItemStackConfig.PotionEffectConfig potionEffectConfig : potionConfig.potionEffects()) {
            // Send a warning if the potion effect type name is null.
            if(potionEffectConfig.type() == null) {
                logger.warn(AdventureUtil.serialize("Unable to parse potion effect type due to an invalid potion effect type name."));
                continue;
            }

            // Send a warning if the potion effect duration is null.
            if(potionEffectConfig.duration() == null) {
                logger.warn(AdventureUtil.serialize("Missing duration for potion effect type: " + potionEffectConfig.type() + "."));
                continue;
            }

            // Send a warning if the potion effect amplifier is null.
            if(potionEffectConfig.amplifier() == null) {
                logger.warn(AdventureUtil.serialize("Missing amplifier for potion effect type: " + potionEffectConfig.type() + "."));
                continue;
            }

            @NotNull Optional<@NotNull PotionEffectType> optionalPotionEffectType = RegistryUtil.getPotionEffectType(logger, potionEffectConfig.type());
            optionalPotionEffectType.ifPresentOrElse(potionEffectType -> {
                // Create the PotionEffect
                PotionEffect potionEffect = potionEffectType.createEffect(potionEffectConfig.duration(), potionEffectConfig.amplifier());
                // Add the created PotionEffect to the list of PotionEffects.
                potionEffects.add(potionEffect);
            }, () -> logger.error(AdventureUtil.serialize("Unable to get a PotionEffectType due to a configuration error.")));
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
                logger.warn(AdventureUtil.serialize("Invalid item flag name: " + flagName + "."));
            }
        }

        // If any sherds are configured, parse the Material names.
        ItemStackConfig.DecoratedPotConfig decoratedPot = config.decoratedPot();
        if(decoratedPot.frontSherd() != null) {
            frontSherd = Material.getMaterial(decoratedPot.frontSherd());

            if(frontSherd == null) logger.warn(AdventureUtil.serialize("No front sherd Material found for: " + decoratedPot.frontSherd() + "."));
        }
        if(decoratedPot.leftSherd() != null) {
            leftSherd = Material.getMaterial(decoratedPot.leftSherd());

            if(leftSherd == null) logger.warn(AdventureUtil.serialize("No left sherd Material found for: " + decoratedPot.leftSherd() + "."));
        }
        if(decoratedPot.rightSherd() != null) {
            rightSherd = Material.getMaterial(decoratedPot.rightSherd());

            if(rightSherd == null) logger.warn(AdventureUtil.serialize("No right sherd Material found for: " + decoratedPot.rightSherd() + "."));
        }
        if(decoratedPot.backSherd() != null) {
            backSherd = Material.getMaterial(decoratedPot.backSherd());

            if(backSherd == null) logger.warn(AdventureUtil.serialize("No back sherd Material found for: " + decoratedPot.backSherd() + "."));
        }

        // If an armor trim pattern and material are configured, attempt to create the ArmorTrim
        ItemStackConfig.ArmorTrimConfig armorTrimConfig = config.armorTrim();
        if(armorTrimConfig.trimPattern() != null && armorTrimConfig.trimMaterial() != null) {
            Optional<TrimPattern> optionalTrimPattern = RegistryUtil.getTrimPattern(logger, armorTrimConfig.trimPattern());
            Optional<TrimMaterial> optionalTrimMaterial = RegistryUtil.getTrimMaterial(logger, armorTrimConfig.trimMaterial());

            if(optionalTrimPattern.isPresent() && optionalTrimMaterial.isPresent()) {
                armorTrim = new ArmorTrim(optionalTrimMaterial.get(), optionalTrimPattern.get());
            } else if(optionalTrimPattern.isEmpty() && optionalTrimMaterial.isPresent()) {
                logger.warn(AdventureUtil.serialize("Failed to create ArmorTrim as no valid armor trim pattern was found for: " + armorTrimConfig.trimPattern() + "."));
            } else if(optionalTrimPattern.isPresent()) {
                logger.warn(AdventureUtil.serialize("Failed to create ArmorTrim as no valid armor trim material was found for: " + armorTrimConfig.trimMaterial() + "."));
            }
        } else if(armorTrimConfig.trimPattern() == null && armorTrimConfig.trimMaterial() != null) {
            logger.warn(AdventureUtil.serialize("No armor trim pattern configured, but an armor trim material was configured."));
        } else if(armorTrimConfig.trimPattern() != null) {
            logger.warn(AdventureUtil.serialize("No armor trim material configured, but an armor trim pattern was configured."));
        }

        // If an instrument is configured, attempt to get the MusicInstrument for that name.
        if(config.instrument() != null) {
            Optional<MusicInstrument> optionalMusicInstrument = RegistryUtil.getInstrument(logger, config.instrument());
            optionalMusicInstrument.ifPresentOrElse(
                    instrument -> this.instrument = instrument,
                    () -> logger.warn(AdventureUtil.serialize("Failed to find an Instrument for the name: " + config.instrument() + ".")));
        }

        // If any attributes are configured, parse the attribute config and add it to the attribute map.
        // Displays a warning for any invalid configuration or parsing errors.
        for(ItemStackConfig.AttributeConfig attributeConfig : config.attributes()) {
            // If the attribute name is null, display a warning and process the next object in the list.
            if(attributeConfig.attribute() == null) {
                logger.warn(AdventureUtil.serialize("Invalid attribute name."));
                continue;
            }

            // If the attribute amount is null, display a warning and process the next object in the list.
            if(attributeConfig.amount() == null) {
                logger.warn(AdventureUtil.serialize("Invalid attribute value."));
                continue;
            }

            // If the attribute operation is null, display a warning and process the next object in the list.
            if(attributeConfig.operation() == null) {
                logger.warn(AdventureUtil.serialize("Invalid attribute option name."));
                continue;
            }

            // Get the attribute from the registry.
            Attribute attribute = null;
            @NotNull Optional<Attribute> optionalAttribute = RegistryUtil.getAttribute(logger, attributeConfig.attribute());
            if(optionalAttribute.isPresent()) attribute = optionalAttribute.get();

            // If the attribute is null, display a warning and process the next object in the list.
            if(attribute == null) {
                logger.error(AdventureUtil.serialize("Unable to get an Attribute due to a configuration error."));
                continue;
            }

            // Attempt to get the operation for the attribute modifier.
            // Displays a warning and then skips to process the next object in the list on an error.
            AttributeModifier.Operation operation;
            try {
                operation = AttributeModifier.Operation.valueOf(attributeConfig.operation());
            } catch (IllegalArgumentException ignored) {
                logger.warn(AdventureUtil.serialize("Unable to find operation for operation name: " + attributeConfig.operation() + "."));
                continue;
            }

            // Attempt to get the equipment slot for the attribute modifier if configured.
            EquipmentSlot equipmentSlot = null;
            if(attributeConfig.equipmentSlot() != null) {
                // Attempt to get the equipment slot or display a warning on error and then skips to process the next object in the list.
                try {
                    equipmentSlot = EquipmentSlot.valueOf(attributeConfig.equipmentSlot());
                } catch (IllegalArgumentException ignored) {
                    logger.warn(AdventureUtil.serialize("Unable to find equipment slot for equipment slot name: " + attributeConfig.equipmentSlot() + "."));
                    continue;
                }
            }

            // Create the AttributeModifier.
            AttributeModifier attributeModifier;
            // If a valid EquipmentSlot was created, create the modifier using that.
            // Otherwise, create the modifier without one.
            if(equipmentSlot != null) {
                attributeModifier = new AttributeModifier(attribute.getKey(), amount, operation, equipmentSlot.getGroup());
            } else {
                attributeModifier = new AttributeModifier(attribute.getKey(), amount, operation);
            }

            // Add the attribute and attribute modifier to the attributes map.
            attributes.put(attribute, attributeModifier);
        }

        // Apply any other configuration options.
        enchantmentGlint = config.options().enchantmentGlint();
        unbreakable = config.options().unbreakable();
        fireResistant = config.options().fireResistant();
        hideToolTip = config.options().hideToolTip();
        glider = config.options().glider();
    }

    /**
     * Creates an ItemStack based on the provided data inside the {@link ItemStackBuilder}.
     * @return an {@link Optional} containing the created {@link ItemStack} if the data is valid,
     *         or an empty {@link Optional} if the data is invalid.
     */
    public Optional<@NotNull ItemStack> buildItemStack() {
        // Create a copy of the base ItemStack or create a new ItemStack using the ItemType. Handle any errors as needed.
        ItemStack itemStack;
        if(baseItemStack == null && itemType == null) {
            logger.error(AdventureUtil.serialize("Unable to create an ItemStack due to an invalid base ItemStack or ItemType."));
            return Optional.empty();
        } else if(itemType == null) {
            logger.error(AdventureUtil.serialize("Unable to create an ItemStack due to an invalid ItemType."));
            return Optional.empty();
        } else {
            itemStack = Objects.requireNonNullElseGet(baseItemStack, () -> itemType.createItemStack(amount));
        }

        // Add any enchantments to the ItemStack.
        itemStack.addEnchantments(enchantments);

        // Get itemStack's ItemMeta.
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set the max stack size.
        itemMeta.setMaxStackSize(maxStackSize);
        // Set the display name.
        itemMeta.displayName(name);
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
    private boolean isValidColorValue(@Nullable Integer value, @NotNull String colorName) {
        if(value == null) return false;

        if (value < 0 || value > 255) {
            logger.warn(AdventureUtil.serialize("The " + colorName + " color value must be greater than or equal to 0 and less than or equal to 255."));
            return false;
        }

        return true;
    }

    private void applyEnchantments(@NotNull ItemMeta itemMeta) {
        enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
    }

    private void setItemMeta(@NotNull ItemStack itemStack, @NotNull ItemMeta itemMeta) {
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Sets the base {@link ItemStack} to use in the final ItemStack creation.
     * @param itemStack A {@link ItemStack} to use as a base.
     */
    public void setBaseItemStack(@NotNull ItemStack itemStack) {
        this.baseItemStack = itemStack;
    }

    /**
     * Sets the {@link ItemType} to use when creating the {@link ItemStack}.
     * The {@link ItemType} is ignored if a base {@link ItemStack} is supplied.
     * @param itemType A {@link ItemType} to use to create the {@link ItemStack}.
     */
    public void setItemType(@NotNull ItemType itemType) {
        this.itemType = itemType;
    }

    /**
     * Set the amount of items in the {@link ItemStack}.
     * The amount provided is validated to ensure it is {@literal >}= 1 and {@literal <}= the max stack size.
     * The max stack size is selected in one of three ways:<br>
     * 1. Against the {@link #maxStackSize} if configured.<br>
     * 2. If not, it is validated against the {@link #itemType}'s default max stack size if configured.<br>
     * 3. Otherwise, the value is validated against a maximum of 99. NOTE: This may change to log an error instead.<br>
     * @param amount The amount of items in the {@link ItemStack}.
     */
    public void setAmount(int amount) {
        int maxSize;

        if(maxStackSize != null) {
            maxSize = maxStackSize;
        } else if(itemType != null) {
            maxSize = itemType.getMaxStackSize();
        } else {
            maxSize = 99;
        }

        if(amount >= 1 && amount <= maxSize) {
            this.amount = amount;
        } else {
            logger.warn(AdventureUtil.serialize("Amount is limited to greater than or equal to 1 and less than or equal to " + maxSize + "."));
        }
    }

    /**
     * Sets the maximum amount of items the {@link ItemStack} can contain.
     * The amount must be {@literal >}= 1 or {@literal <}= 99.
     * @param amount The maximum amount of items the {@link ItemStack} can contain.
     */
    public void setMaxStackSize(int amount) {
        if(amount >= 1 && amount <= 99) {
            maxStackSize = amount;
        } else {
            logger.warn(AdventureUtil.serialize("Max stack size is limited to greater than or equal to 1 and less than or equal to 99."));
        }
    }

    /**
     * Sets the name to give the {@link ItemStack}.
     * @param name The name to give the {@link ItemStack}.
     */
    public void setName(@NotNull Component name) {
        this.name = name;
    }

    /**
     * Sets the lore to give the {@link ItemStack}.
     * @param lore The lore to give the {@link ItemStack}.
     */
    public void setLore(@NotNull List<Component> lore) {
        this.lore = lore;
    }

    /**
     * Sets the {@link EntityType} the {@link ItemStack} associated with the {@link ItemStack}.
     * This is exclusively used for spawners.
     * @param entityType The {@link EntityType} to give the {@link ItemStack}.
     */
    public void setEntityType(@NotNull EntityType entityType) {
        this.entityType = entityType;
    }

    /**
     * Sets the enchantments the {@link ItemStack} should have.
     * This will replace the existing {@link #enchantments} map with the one provided.
     * @param enchantments A {@link Map} containing the mapping of {@link Enchantment}s and enchantment levels to give the {@link ItemStack}.
     */
    public void setEnchantments(@NotNull Map<@NotNull Enchantment, @NotNull Integer> enchantments) {
        this.enchantments = enchantments;
    }

    /**
     * Adds an {@link Enchantment} and enchantment level to the existing {@link #enchantments} map.
     * @param enchantment The {@link Enchantment} to add.
     * @param level The enchantment level to add.
     */
    public void addEnchantment(@NotNull Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
    }

    /**
     * Sets the {@link PotionType} the {@link ItemStack} should have.
     * This is only used for {@link ItemStack}s that can have potions types.
     * @param potionType The {@link PotionType} to give the {@link ItemStack}.
     */
    public void setPotionType(@NotNull PotionType potionType) {
        this.potionType = potionType;
    }

    /**
     * Sets a {@link List} of {@link PotionEffect}s the {@link ItemStack} should have.
     * This is only used for {@link ItemStack}s that can have potions effects.
     * @param potionEffects The {@link List} of {@link PotionEffect}s to give the {@link ItemStack}.
     */
    public void setPotionEffects(@NotNull List<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    /**
     * Adds a {@link PotionEffect}s to the existing list of potion effects the {@link ItemStack} should have.
     * This is only used for {@link ItemStack}s that can have potions effects.
     * @param potionEffect The {@link PotionEffect} to give the {@link ItemStack}.
     */
    public void addPotionEffect(@NotNull PotionEffect potionEffect) {
        potionEffects.add(potionEffect);
    }

    /**
     * Sets the {@link Color} to apply to any dyeable {@link ItemStack}s.
     * @param color The {@link Color} to apply to the {@link ItemStack}.
     */
    public void setColor(@NotNull Color color) {
        this.color = color;
    }

    /**
     * Sets the {@link NamespacedKey} of the item model to apply to the {@link ItemStack}.
     * @param key The {@link NamespacedKey} of the item model to apply to the {@link ItemStack}.
     */
    public void setModel(@NotNull NamespacedKey key) {
        this.model = key;
    }

    /**
     * Sets the {@link List} of {@link ItemFlag}s that will be hidden for the {@link ItemStack}.
     * This replaces the {@link #itemFlags} list.
     * @param itemFlags The {@link List} of {@link ItemFlag}s that will be hidden for the {@link ItemStack}.
     */
    public void setItemFlags(@NotNull List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
    }

    /**
     * Adds an {@link ItemFlag} to the {@link #itemFlags} list.
     * @param itemFlag The {@link ItemFlag} to add to the {@link #itemFlags} list.
     */
    public void addItemFlag(@NotNull ItemFlag itemFlag) {
        itemFlags.add(itemFlag);
    }

    /**
     * Removes an {@link ItemFlag} from the {@link #itemFlags} list.
     * @param itemFlag The {@link ItemFlag} to remove from the {@link #itemFlags} list.
     */
    public void removeItemFlag(@NotNull ItemFlag itemFlag) {
        itemFlags.remove(itemFlag);
    }

    /**
     * Sets the {@link Material} to use for the sherd on the front of a decorated pot.
     * @param frontSherd The {@link Material} to use for the sherd on the front of a decorated pot.
     */
    public void setFrontSherd(@NotNull Material frontSherd) {
        this.frontSherd = frontSherd;
    }

    /**
     * Sets the {@link Material} to use for the sherd on the left of a decorated pot.
     * @param leftSherd The {@link Material} to use for the sherd on the left of a decorated pot.
     */
    public void setLeftSherd(@NotNull Material leftSherd) {
        this.leftSherd = leftSherd;
    }

    /**
     * Sets the {@link Material} to use for the sherd on the right of a decorated pot.
     * @param rightSherd The {@link Material} to use for the sherd on the right of a decorated pot.
     */
    public void setRightSherd(@NotNull Material rightSherd) {
        this.rightSherd = rightSherd;
    }

    /**
     * Sets the {@link Material} to use for the sherd on the back of a decorated pot.
     * @param backSherd The {@link Material} to use for the sherd on the back of a decorated pot.
     */
    public void setBackSherd(@NotNull Material backSherd) {
        this.backSherd = backSherd;
    }

    /**
     * Sets the {@link ArmorTrim} to apply to armor {@link ItemStack}s.
     * @param armorTrim The {@link ArmorTrim} to apply to armor {@link ItemStack}s.
     */
    public void setArmorTrim(@NotNull ArmorTrim armorTrim) {
        this.armorTrim = armorTrim;
    }

    /**
     * Creates an {@link ArmorTrim} to apply to armor {@link ItemStack}s.
     * @param trimPattern The {@link TrimPattern}.
     * @param trimMaterial The {@link TrimMaterial}.
     */
    public void setArmorTrim(@NotNull TrimPattern trimPattern, @NotNull TrimMaterial trimMaterial) {
        armorTrim = new ArmorTrim(trimMaterial, trimPattern);
    }

    /**
     * Sets the {@link MusicInstrument} to apply to goat horn {@link ItemStack}s.
     * @param instrument The {@link MusicInstrument} to apply to goat horn {@link ItemStack}s.
     */
    public void setInstrument(@NotNull MusicInstrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Sets the {@link Map} of {@link Attribute}s that will be applied to the {@link ItemStack}.
     * This replaces the {@link #attributes} map.
     * @param attributes The {@link List} of {@link Attribute}s that will be applied to the {@link ItemStack}.
     */
    public void setAttributes(@NotNull Map<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;
    }

    /**
     * Adds an {@link Attribute} and {@link AttributeModifier} to the {@link #attributes} map.
     * @param attribute The {@link Attribute}
     * @param attributeModifier The {@link AttributeModifier}.
     */
    public void addAttribute(@NotNull Attribute attribute, @NotNull AttributeModifier attributeModifier) {
        attributes.put(attribute, attributeModifier);
    }

    /**
     * Removes an {@link Attribute} from the {@link #attributes} map.
     * @param attribute The {@link Attribute}
     */
    public void removeAttribute(@NotNull Attribute attribute) {
        attributes.remove(attribute);
    }

    /**
     * Set the {@link OfflinePlayer} to associated with the {@link ItemStack} if it is a player head.
     * @param offlinePlayer The {@link OfflinePlayer}.
     */
    public void setPlayer(@NotNull OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    /**
     * Set the {@link #enchantmentGlint} option.
     * @param enchantmentGlint true means the {@link ItemStack} will have glint, false means the {@link ItemStack} will never have a glint, null will use the default value of the {@link ItemStack}.
     */
    public void setEnchantmentGlint(@Nullable Boolean enchantmentGlint) {
        this.enchantmentGlint = enchantmentGlint;
    }

    /**
     * Set the {@link #fireResistant} option.
     * @param fireResistant true means the {@link ItemStack} will be fire-resistant, false means the {@link ItemStack} will never be fire-resistant, and null will use the default value of the {@link ItemStack}
     */
    public void setFireResistant(@Nullable Boolean fireResistant) {
        this.fireResistant = fireResistant;
    }

    /**
     * Set the {@link #fireResistant} option.
     * @param glider true means the {@link ItemStack} will act like an Elytra, false means the {@link ItemStack} will never act like an Elytra, and null will use the default value of the {@link ItemStack}
     */
    public void setGlider(@Nullable Boolean glider) {
        this.glider = glider;
    }

    /**
     * Set the {@link #hideToolTip} option.
     * @param hideToolTip true means the {@link ItemStack} will hide tool tips, false means the {@link ItemStack} will never hide tool tips, and null will use the default value of the {@link ItemStack}
     */
    public void setHideToolTip(@Nullable Boolean hideToolTip) {
        this.hideToolTip = hideToolTip;
    }

    /**
     * Set the {@link #unbreakable} option.
     * @param unbreakable true means the {@link ItemStack} will never lose durability, false means the {@link ItemStack} will lose durability, and null will use the default value of the {@link ItemStack}
     */
    public void setUnbreakable(@Nullable Boolean unbreakable) {
        this.unbreakable = unbreakable;
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
    public @NotNull List<Component> getLore() {
        return lore;
    }

    /**
     * Get the {@link #enchantments} that will be applied to the {@link ItemStack} once created. Never null, but may be an empty map.
     * @return A {@link Map} of {@link Enchantment}s to levels. Never null, but the map may be empty.
     */
    public @NotNull Map<Enchantment, Integer> getEnchantments() {
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
    public @NotNull List<PotionEffect> getPotionEffects() {
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
    public @NotNull List<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    /**
     * Get the {@link #attributes} that will be applied to the {@link ItemStack} once created. Never null, but may be an empty map.
     * @return A {@link Map} of {@link Attribute}s to {@link AttributeModifier}s. Never null, but the map may be empty.
     */
    public @NotNull Map<Attribute, AttributeModifier> getAttributes() {
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
