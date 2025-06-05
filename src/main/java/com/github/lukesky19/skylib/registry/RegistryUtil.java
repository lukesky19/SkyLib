package com.github.lukesky19.skylib.registry;

import com.github.lukesky19.skylib.adventure.AdventureUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
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
     * @param name The name of the {@link ItemType} to retrieve or null.
     * @return If any error occurred or an {@link ItemType} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link ItemType} is returned.
     */
    public static @NotNull Optional<ItemType> getItemType(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull ItemType> itemTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the ItemType from the registry.
            @Nullable ItemType itemType = itemTypeRegistry.get(key);
            if(itemType == null) {
                logger.error(AdventureUtil.serialize("Failed to find a ItemType for the NamespacedKey: " + key));
                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid ItemType."));

                return Optional.empty();
            }

            // Return the ItemType from the registry.
            return Optional.of(itemType);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link BlockType} from the {@link Registry} for {@link BlockType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link BlockType} to retrieve or null.
     * @return If any error occurred or an {@link BlockType} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link BlockType} is returned.
     */
    @NotNull
    public static Optional<BlockType> getBlockType(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull BlockType> blockTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the BlockType from the registry.
            @Nullable BlockType blockType = blockTypeRegistry.get(key);
            if(blockType == null) {
                logger.error(AdventureUtil.serialize("Failed to find a BlockType for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid BlockType."));

                return Optional.empty();
            }

            // Return the BlockType from the registry.
            return Optional.of(blockType);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link EntityType} from the {@link Registry} for {@link EntityType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link EntityType} to retrieve or null.
     * @return If any error occurred or an {@link EntityType} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link EntityType} is returned.
     */
    @NotNull
    public static Optional<EntityType> getEntityType(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull EntityType> entityTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the EntityType from the registry.
            @Nullable EntityType entityType = entityTypeRegistry.get(key);
            if(entityType == null) {
                logger.error(AdventureUtil.serialize("Failed to find a EntityType for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid EntityType."));

                return Optional.empty();
            }

            // Return the EntityType from the registry.
            return Optional.of(entityType);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link PotionType} from the {@link Registry} for {@link PotionType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link PotionType} to retrieve or null.
     * @return If any error occurred or an {@link PotionType} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link PotionType} is returned.
     */
    @NotNull
    public static Optional<@NotNull PotionType> getPotionType(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull PotionType> potionTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.POTION);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the PotionType from the registry.
            @Nullable PotionType potionType = potionTypeRegistry.get(key);
            if(potionType == null) {
                logger.error(AdventureUtil.serialize("Failed to find a PotionType for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid PotionType."));

                return Optional.empty();
            }

            // Return the PotionType from the registry.
            return Optional.of(potionType);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link PotionEffectType} from the {@link Registry} for {@link PotionEffectType}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link PotionEffectType} to retrieve.
     * @return If any error occurred or an {@link PotionEffectType} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link PotionEffectType} is returned.
     */
    @NotNull
    public static Optional<@NotNull PotionEffectType> getPotionEffectType(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull PotionEffectType> potionEffectTypeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.MOB_EFFECT);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the PotionEffectType from the registry.
            @Nullable PotionEffectType potionEffectType = potionEffectTypeRegistry.get(key);
            if(potionEffectType == null) {
                logger.error(AdventureUtil.serialize("Failed to find a PotionEffectType for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid PotionEffectType."));

                return Optional.empty();
            }

            // Return the PotionEffectType from the registry.
            return Optional.of(potionEffectType);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link Enchantment} from the {@link Registry} for {@link Enchantment}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link Enchantment} to retrieve or null.
     * @return If any error occurred or an {@link Enchantment} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link Enchantment} is returned.
     */
    @NotNull
    public static Optional<@NotNull Enchantment> getEnchantment(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull Enchantment> enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the Enchantment from the registry.
            @Nullable Enchantment enchantment = enchantmentRegistry.get(key);
            if(enchantment == null) {
                logger.error(AdventureUtil.serialize("Failed to find a Enchantment for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid Enchantment."));

                return Optional.empty();
            }

            // Return the Enchantment from the registry.
            return Optional.of(enchantment);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link Attribute} from the {@link Registry} for {@link Attribute}s based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link Attribute} to retrieve.
     * @return If any error occurred or an {@link Attribute} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link Attribute} is returned.
     */
    @NotNull
    public static Optional<@NotNull Attribute> getAttribute(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull Attribute> attributeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the Attribute from the registry.
            @Nullable Attribute attribute = attributeRegistry.get(key);
            if(attribute == null) {
                logger.error(AdventureUtil.serialize("Failed to find a Attribute for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid Attribute."));

                return Optional.empty();
            }

            // Return the Attribute from the registry.
            return Optional.of(attribute);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link MusicInstrument} from the {@link Registry} for {@link MusicInstrument} based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link MusicInstrument} to retrieve.
     * @return If any error occurred or a {@link MusicInstrument} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link MusicInstrument} is returned.
     */
    public static @NotNull Optional<@NotNull MusicInstrument> getInstrument(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull MusicInstrument> instrumentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the MusicInstrument from the registry.
            @Nullable MusicInstrument instrument = instrumentRegistry.get(key);
            if(instrument == null) {
                logger.error(AdventureUtil.serialize("Failed to find an instrument for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid instrument."));

                return Optional.empty();
            }

            // Return the MusicInstrument from the registry.
            return Optional.of(instrument);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link TrimPattern} from the {@link Registry} for {@link TrimPattern} based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link TrimPattern} to retrieve.
     * @return If any error occurred or a {@link TrimPattern} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link TrimPattern} is returned.
     */
    public static @NotNull Optional<@NotNull TrimPattern> getTrimPattern(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull TrimPattern> armorTrimPatternRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the TrimPattern from the registry.
            @Nullable TrimPattern trimPattern = armorTrimPatternRegistry.get(key);
            if(trimPattern == null) {
                logger.error(AdventureUtil.serialize("Failed to find an armor trim pattern for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid armor trim pattern."));

                return Optional.empty();
            }

            return Optional.of(trimPattern);
        }

        return Optional.empty();
    }

    /**
     * Retrieves an {@link TrimMaterial} from the {@link Registry} for {@link TrimMaterial} based on the provided name.
     * @param logger The {@link ComponentLogger} of the plugin using this method. Used to display error messages.
     * @param name The name of the {@link TrimMaterial} to retrieve.
     * @return If any error occurred or a {@link TrimMaterial} was not found for the given name, an empty {@link Optional}
     * is returned. Otherwise, an {@link Optional} containing the {@link TrimMaterial} is returned.
     */
    public static @NotNull Optional<@NotNull TrimMaterial> getTrimMaterial(@NotNull ComponentLogger logger, @NotNull String name) {
        Registry<@NotNull TrimMaterial> armorTrimMaterialRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL);

        // Create the NamespacedKey using the name normalized to lowercase.
        @NotNull Optional<NamespacedKey> optionalNamespacedKey = createNamespacedKey(name.toLowerCase());
        if(optionalNamespacedKey.isPresent()) {
            NamespacedKey key = optionalNamespacedKey.get();

            // Get the TrimMaterial from the registry.
            @Nullable TrimMaterial trimMaterial = armorTrimMaterialRegistry.get(key);
            if(trimMaterial == null) {
                logger.error(AdventureUtil.serialize("Failed to find an armor trim material for the NamespacedKey: " + key));

                logger.info(AdventureUtil.serialize("Ensure that the name corresponds to a valid armor trim material."));

                return Optional.empty();
            }

            return Optional.of(trimMaterial);
        }

        return Optional.empty();
    }
}
