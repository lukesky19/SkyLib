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
package com.github.lukesky19.skylib.api.registry;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Utility class for retrieving various objects from their respective registries.
 */
public class RegistryUtil {
    private static @Nullable Registry<@NotNull ItemType> itemTypeRegistry;
    private static @Nullable Registry<@NotNull BlockType> blockTypeRegistry;
    private static @Nullable Registry<@NotNull EntityType> entityTypeRegistry;
    private static @Nullable Registry<@NotNull PotionType> potionTypeRegistry;
    private static @Nullable Registry<@NotNull PotionEffectType> potionEffectTypeRegistry;
    private static @Nullable Registry<@NotNull Enchantment> enchantmentRegistry;
    private static @Nullable Registry<@NotNull Attribute> attributeRegistry;
    private static @Nullable Registry<@NotNull MusicInstrument> instrumentRegistry;
    private static @Nullable Registry<@NotNull TrimPattern> trimPatternRegistry;
    private static @Nullable Registry<@NotNull TrimMaterial> trimMaterialRegistry;
    private static @Nullable Registry<@NotNull Particle> particleRegistry;

    /**
     * All methods in this class are static so this constructor will throw a runtime exception if used.
     * @throws RuntimeException if the constructor is used.
     */
    public RegistryUtil() {
        throw new RuntimeException("This class cannot be instanced. Use the static references to methods instead.");
    }

    /**
     * Creates a {@link NamespacedKey} from the given name.
     * If a namespace is not provided, the default minecraft: namespace will be used. Otherwise, the one provided will be used.
     * @param name The name of the key or the name with a namespace and key following the namespace:key format.
     * @return An {@link Optional} containing a {@link NamespacedKey} if one was created successfully. May be empty if the NamespacedKey fails to be created.
     */
    public static @NotNull Optional<NamespacedKey> createNamespacedKey(@NotNull String name) {
        if(name.contains(":")) {
            return Optional.ofNullable(NamespacedKey.fromString(name));
        } else {
            return Optional.of(NamespacedKey.minecraft(name));
        }
    }

    /**
     * Retrieves an {@link ItemType} from the {@link Registry} for {@link ItemType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link ItemType} to retrieve.
     * @return An {@link Optional} containing the {@link ItemType}.
     */
    public static @NotNull Optional<ItemType> getItemType(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<ItemType> optionalItemType = getItemType(key);
        if(optionalItemType.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a ItemType for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid ItemType."));
        }

        return optionalItemType;
    }

    /**
     * Retrieves an {@link ItemType} from the {@link Registry} for {@link ItemType}s based on the provided name.
     * @param key The key of the {@link ItemType} to retrieve or null.
     * @return An {@link Optional} containing the {@link ItemType}.
     */
    public static @NotNull Optional<ItemType> getItemType(@NotNull String key) {
        if(itemTypeRegistry == null) itemTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(itemTypeRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link BlockType} from the {@link Registry} for {@link BlockType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link BlockType} to retrieve.
     * @return An {@link Optional} containing the {@link BlockType}.
     */
    public static @NotNull Optional<BlockType> getBlockType(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<BlockType> optionalBlockType = getBlockType(key);
        if(optionalBlockType.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a BlockType for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid BlockType."));
        }

        return optionalBlockType;
    }

    /**
     * Retrieves a {@link BlockType} from the {@link Registry} for {@link BlockType}s based on the provided key.
     * @param key The name of the {@link BlockType} to retrieve.
     * @return An {@link Optional} containing the {@link BlockType}.
     */
    public static @NotNull Optional<BlockType> getBlockType(@NotNull String key) {
        if(blockTypeRegistry == null) blockTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(blockTypeRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link EntityType} from the {@link Registry} for {@link EntityType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link EntityType} to retrieve or null.
     * @return An {@link Optional} containing the {@link EntityType}.
     */
    public static @NotNull Optional<EntityType> getEntityType(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<EntityType> optionalEntityType = getEntityType(key);
        if(optionalEntityType.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a EntityType for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid EntityType."));
        }

        return optionalEntityType;
    }

    /**
     * Retrieves an {@link EntityType} from the {@link Registry} for {@link EntityType}s based on the provided name.
     * @param key The key of the {@link EntityType} to retrieve or null.
     * @return An {@link Optional} containing the {@link EntityType}.
     */
    public static @NotNull Optional<EntityType> getEntityType(@NotNull String key) {
        if(entityTypeRegistry == null) entityTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(entityTypeRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves a {@link PotionType} from the {@link Registry} for {@link PotionType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link PotionType} to retrieve or null.
     * @return An {@link Optional} containing the {@link PotionType}.
     */
    public static @NotNull Optional<PotionType> getPotionType(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<PotionType> optionalPotionType = getPotionType(key);
        if(optionalPotionType.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a PotionType for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid PotionType."));
        }

        return optionalPotionType;
    }

    /**
     * Retrieves a {@link PotionType} from the {@link Registry} for {@link PotionType}s based on the provided name.
     * @param key The key of the {@link PotionType} to retrieve or null.
     * @return An {@link Optional} containing the {@link PotionType}.
     */
    public static @NotNull Optional<PotionType> getPotionType(@NotNull String key) {
        if(potionTypeRegistry == null) potionTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.POTION);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(potionTypeRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves a {@link PotionEffectType} from the {@link Registry} for {@link PotionEffectType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link PotionEffectType} to retrieve.
     * @return An {@link Optional} containing the {@link PotionEffectType}.
     */
    public static @NotNull Optional<PotionEffectType> getPotionEffectType(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<PotionEffectType> optionalPotionEffectType = getPotionEffectType(key);
        if(optionalPotionEffectType.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a PotionEffectType for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid PotionEffectType."));
        }

        return optionalPotionEffectType;
    }

    /**
     * Retrieves a {@link PotionEffectType} from the {@link Registry} for {@link PotionEffectType}s based on the provided name.
     * @param key The key of the {@link PotionEffectType} to retrieve.
     * @return An {@link Optional} containing the {@link PotionEffectType}.
     */
    public static @NotNull Optional<PotionEffectType> getPotionEffectType(@NotNull String key) {
        if(potionEffectTypeRegistry == null) potionEffectTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(potionEffectTypeRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link Enchantment} from the {@link Registry} for {@link Enchantment}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link Enchantment} to retrieve or null.
     * @return An {@link Optional} containing the {@link Enchantment}.
     */
    public static @NotNull Optional<Enchantment> getEnchantment(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<Enchantment> optionalEnchantment = getEnchantment(key);
        if(optionalEnchantment.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a Enchantment for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid Enchantment."));
        }

        return optionalEnchantment;
    }

    /**
     * Retrieves an {@link Enchantment} from the {@link Registry} for {@link Enchantment}s based on the provided name.
     * @param key The key of the {@link Enchantment} to retrieve or null.
     * @return An {@link Optional} containing the {@link Enchantment}.
     */
    public static @NotNull Optional<Enchantment> getEnchantment(@NotNull String key) {
        if(enchantmentRegistry == null) enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(enchantmentRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link Attribute} from the {@link Registry} for {@link Attribute}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link Attribute} to retrieve.
     * @return An {@link Optional} containing the {@link Attribute}.
     */
    public static @NotNull Optional<Attribute> getAttribute(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<Attribute> optionalAttribute = getAttribute(key);
        if(optionalAttribute.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a Attribute for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid Attribute."));
        }

        return optionalAttribute;
    }

    /**
     * Retrieves an {@link Attribute} from the {@link Registry} for {@link Attribute}s based on the provided name.
     * @param key The key of the {@link Attribute} to retrieve.
     * @return An {@link Optional} containing the {@link Attribute}.
     */
    public static @NotNull Optional<Attribute> getAttribute(@NotNull String key) {
        if(attributeRegistry == null) attributeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(attributeRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves a {@link MusicInstrument} from the {@link Registry} for {@link MusicInstrument} based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link MusicInstrument} to retrieve.
     * @return An {@link Optional} containing the {@link MusicInstrument}.
     */
    public static @NotNull Optional<MusicInstrument> getInstrument(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<MusicInstrument> optionalMusicInstrument = getInstrument(key);
        if(optionalMusicInstrument.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find an instrument for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid instrument."));
        }

        return optionalMusicInstrument;
    }

    /**
     * Retrieves a {@link MusicInstrument} from the {@link Registry} for {@link MusicInstrument} based on the provided name.
     * @param key The key of the {@link MusicInstrument} to retrieve.
     * @return An {@link Optional} containing the {@link MusicInstrument}.
     */
    public static @NotNull Optional<MusicInstrument> getInstrument(@NotNull String key) {
        if(instrumentRegistry == null) instrumentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(instrumentRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves a {@link TrimPattern} from the {@link Registry} for {@link TrimPattern} based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The name of the {@link TrimPattern} to retrieve.
     * @return An {@link Optional} containing the {@link TrimPattern}.
     */
    public static @NotNull Optional<TrimPattern> getTrimPattern(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<TrimPattern> optionalTrimPattern = getTrimPattern(key);
        if(optionalTrimPattern.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find an armor trim pattern for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid armor trim pattern."));
        }

        return optionalTrimPattern;
    }

    /**
     * Retrieves a {@link TrimPattern} from the {@link Registry} for {@link TrimPattern} based on the provided name.
     * @param key The name of the {@link TrimPattern} to retrieve.
     * @return An {@link Optional} containing the {@link TrimPattern}.
     */
    public static @NotNull Optional<TrimPattern> getTrimPattern(@NotNull String key) {
        if(trimPatternRegistry == null) trimPatternRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(trimPatternRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link TrimMaterial} from the {@link Registry} for {@link TrimMaterial} based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The name of the {@link TrimMaterial} to retrieve.
     * @return An {@link Optional} containing the {@link TrimMaterial}.
     */
    public static @NotNull Optional<TrimMaterial> getTrimMaterial(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<TrimMaterial> optionalTrimMaterial = getTrimMaterial(key);
        if(optionalTrimMaterial.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find an armor trim material for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid armor trim material."));
        }

        return optionalTrimMaterial;
    }

    /**
     * Retrieves a {@link TrimMaterial} from the {@link Registry} for {@link TrimMaterial} based on the provided name.
     * @param key The name of the {@link TrimMaterial} to retrieve.
     * @return An {@link Optional} containing the {@link TrimMaterial}.
     */
    public static @NotNull Optional<TrimMaterial> getTrimMaterial(@NotNull String key) {
        if(trimMaterialRegistry == null) trimMaterialRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(trimMaterialRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }

    /**
     * Retrieves a {@link Particle} from the {@link Registry} for {@link Particle}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param key The key of the {@link Particle} to retrieve.
     * @return An {@link Optional} containing the {@link Particle}.
     */
    public static @NotNull Optional<@NotNull Particle> getParticle(@NotNull ComponentLogger logger, @NotNull String key) {
        Optional<Particle> optionalParticle = getParticle(key);
        if(optionalParticle.isEmpty()) {
            logger.warn(AdventureUtil.deserialize("Failed to find a particle for the NamespacedKey: " + key));
            logger.info(AdventureUtil.deserialize("Ensure that the name corresponds to a valid armor trim material."));
        }

        return optionalParticle;
    }

    /**
     * Retrieves a {@link Particle} from the {@link Registry} for {@link Particle}s based on the provided name.
     * @param key The key of the {@link Particle} to retrieve.
     * @return An {@link Optional} containing the {@link Particle}.
     */
    public static @NotNull Optional<Particle> getParticle(@NotNull String key) {
        if(particleRegistry == null) particleRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.PARTICLE_TYPE);

        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(key.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey namespacedKey = optionalNamespacedKey.get();
            return Optional.ofNullable(particleRegistry.get(namespacedKey));
        }

        return Optional.empty();
    }
}