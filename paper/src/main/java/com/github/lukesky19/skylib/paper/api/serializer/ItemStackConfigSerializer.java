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
package com.github.lukesky19.skylib.paper.api.serializer;

import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.paper.api.registry.RegistryUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes and deserializes {@link ItemStackConfig}.
 */
public class ItemStackConfigSerializer implements TypeSerializer<ItemStackConfig> {
    /**
     * Constructor
     */
    public ItemStackConfigSerializer() {}

    @Override
    public @Nullable ItemStackConfig emptyValue(@NonNull Type type, @NonNull ConfigurationOptions options) {
        return new ItemStackConfig(
                null,
                null,
                null,
                null,
                List.of(),
                null,
                null,
                List.of(),
                new ItemStackConfig.PotionConfig(null, List.of()),
                new ItemStackConfig.ColorConfig(false, null, null, null),
                null,
                List.of(),
                new ItemStackConfig.DecoratedPotConfig(null, null, null, null),
                new ItemStackConfig.ArmorTrimConfig(null, null),
                List.of(),
                new ItemStackConfig.OptionsConfig(null, null, null, null, null));
    }

    /**
     * Deserializes the {@link ItemStackConfig} to multiple nodes.
     * @param type The {@link Type}.
     * @param node The {@link ConfigurationNode} to deserialize.
     * @return The {@link ItemStackConfig} or null.
     */
    @Override
    public @NonNull ItemStackConfig deserialize(@NonNull Type type, @NonNull ConfigurationNode node) throws SerializationException {
        // Item Type
        ItemType itemType = null;
        ConfigurationNode itemTypeNode = node.node("item-type");
        if(!itemTypeNode.virtual()) {
            String rawKey = itemTypeNode.getString();
            if(rawKey != null) {
                itemType = RegistryUtil.getItemType(rawKey).orElse(null);
            }
        }

        // Amount
        Integer amount = null;
        ConfigurationNode amountNode = node.node("amount");
        if(!amountNode.virtual()) {
            amount = amountNode.getInt();
        }

        // Max Stack Size
        Integer maxStackSize = null;
        ConfigurationNode maxStackSizeNode = node.node("max-stack-size");
        if(!maxStackSizeNode.virtual()) {
            maxStackSize = maxStackSizeNode.getInt();
        }

        // Name
        String name = null;
        ConfigurationNode nameNode = node.node("name");
        if(!nameNode.virtual()) {
            name = nameNode.getString();
        }

        // Lore
        List<String> lore = new ArrayList<>();
        ConfigurationNode loreNode = node.node("lore");
        if(!loreNode.virtual()) {
            lore = loreNode.getList(String.class);
            if(lore == null) lore = new ArrayList<>();
        }

        // EntityType
        EntityType entityType = null;
        ConfigurationNode entityTypeNode = node.node("entity-type");
        if(!entityTypeNode.virtual()) {
            String rawKey = entityTypeNode.getString();
            if(rawKey != null) {
                entityType = RegistryUtil.getEntityType(rawKey).orElse(null);
            }
        }

        // Instrument
        MusicInstrument instrument = null;
        ConfigurationNode instrumentNode = node.node("instrument");
        if(!instrumentNode.virtual()) {
            String rawKey = instrumentNode.getString();
            if(rawKey != null) {
                instrument = RegistryUtil.getInstrument(rawKey).orElse(null);
            }
        }

        // Enchantments
        List<ItemStackConfig.EnchantmentConfig> enchantmentConfigList = new ArrayList<>();
        ConfigurationNode enchantmentsNode = node.node("enchantments");
        if(!enchantmentsNode.virtual()) {
            List<? extends ConfigurationNode> childrenNodes = enchantmentsNode.childrenList();
            childrenNodes.forEach(childNode -> {
                ConfigurationNode enchantmentNode = childNode.node("enchantment");
                ConfigurationNode levelNode = childNode.node("level");

                Enchantment enchantment = null;
                if(!enchantmentNode.virtual()) {
                    String rawKey = enchantmentNode.getString();
                    if(rawKey != null) {
                        enchantment = RegistryUtil.getEnchantment(rawKey).orElse(null);
                    }
                }

                Integer level = null;
                if(!levelNode.virtual()) {
                    level = levelNode.getInt();
                }

                enchantmentConfigList.add(new ItemStackConfig.EnchantmentConfig(enchantment, level));
            });
        }

        // Potion Config
        ItemStackConfig.PotionConfig potionConfig = new ItemStackConfig.PotionConfig(null, List.of());
        ConfigurationNode potionConfigNode = node.node("potion-config");
        if(!potionConfigNode.virtual()) {
            PotionType potionType = null;
            ConfigurationNode potionTypeNode = potionConfigNode.node("potion-type");
            if(!potionTypeNode.virtual()) {
                String rawKey = potionTypeNode.getString();
                if(rawKey != null) {
                    potionType = RegistryUtil.getPotionType(rawKey).orElse(null);
                }
            }

            List<ItemStackConfig.PotionEffectConfig> potionEffectConfigList = new ArrayList<>();
            ConfigurationNode potionEffectsNode = potionConfigNode.node("potion-effects");
            if(!potionEffectsNode.virtual()) {
                List<? extends ConfigurationNode> childrenNodes = potionEffectsNode.childrenList();
                childrenNodes.forEach(childNode -> {
                    ConfigurationNode typeNode = childNode.node("type");
                    ConfigurationNode durationNode = childNode.node("duration-seconds");
                    ConfigurationNode amplifierNode = childNode.node("amplifier");

                    PotionEffectType potionEffectType = null;
                    if(!typeNode.virtual()) {
                        String rawKey = typeNode.getString();
                        if(rawKey != null) {
                            potionEffectType = RegistryUtil.getPotionEffectType(rawKey).orElse(null);
                        }
                    }

                    Double durationSeconds = null;
                    if(!durationNode.virtual()) {
                        durationSeconds = durationNode.getDouble();
                    }

                    Integer amplifier = null;
                    if(!amplifierNode.virtual()) {
                        amplifier = amplifierNode.getInt();
                    }

                    potionEffectConfigList.add(new ItemStackConfig.PotionEffectConfig(potionEffectType, durationSeconds, amplifier));
                });
            }

            potionConfig = new ItemStackConfig.PotionConfig(potionType, potionEffectConfigList);
        }

        // Color Config
        ItemStackConfig.ColorConfig colorConfig = new ItemStackConfig.ColorConfig(false, null, null, null);
        ConfigurationNode colorConfigNode = node.node("color-config");
        if(!colorConfigNode.virtual()) {
            boolean random = false;
            ConfigurationNode randomNode = colorConfigNode.node("random");
            if(!randomNode.virtual()) {
                random = randomNode.getBoolean();
            }

            ConfigurationNode redNode = colorConfigNode.node("red");
            Integer red = null;
            if(!redNode.virtual()) {
                red = redNode.getInt();
            }

            ConfigurationNode greenNode = colorConfigNode.node("green");
            Integer green = null;
            if(!greenNode.virtual()) {
                green = greenNode.getInt();
            }

            ConfigurationNode blueNode = colorConfigNode.node("blue");
            Integer blue = null;
            if(!blueNode.virtual()) {
                blue = blueNode.getInt();
            }

            colorConfig = new ItemStackConfig.ColorConfig(random, red,green, blue);
        }

        // Model Name
        String modelName = null;
        ConfigurationNode modelNameNode = node.node("model-name");
        if(!modelNameNode.virtual()) {
            modelName = modelNameNode.getString();
        }

        // Item Flags
        List<String> itemFlagList = new ArrayList<>();
        ConfigurationNode itemFlagsNode = node.node("item-flags");
        if(!itemFlagsNode.virtual()) {
            itemFlagsNode.getList(String.class);
        }

        // Decorated Pot Config
        ItemStackConfig.DecoratedPotConfig decoratedPotConfig = new ItemStackConfig.DecoratedPotConfig(null, null, null, null);
        ConfigurationNode decoratedPotNode = node.node("decorated-pot");
        if(!decoratedPotNode.virtual()) {
            Material frontSherd = null;
            ConfigurationNode frontSherdNode = decoratedPotNode.node("front-sherd");
            if(!frontSherdNode.virtual()) {
                String rawKey = frontSherdNode.getString();
                if(rawKey != null) {
                    frontSherd = Material.getMaterial(rawKey.toUpperCase());
                }
            }

            Material leftSherd = null;
            ConfigurationNode leftSherdNode = decoratedPotNode.node("left-sherd");
            if(!leftSherdNode.virtual()) {
                String rawKey = leftSherdNode.getString();
                if(rawKey != null) {
                    leftSherd = Material.getMaterial(rawKey.toUpperCase());
                }
            }

            Material rightSherd = null;
            ConfigurationNode rightSherdNode = decoratedPotNode.node("right-sherd");
            if(!rightSherdNode.virtual()) {
                String rawKey = rightSherdNode.getString();
                if(rawKey != null) {
                    rightSherd = Material.getMaterial(rawKey.toUpperCase());
                }
            }

            Material backSherd = null;
            ConfigurationNode backSherdNode = decoratedPotNode.node("back-sherd");
            if(!backSherdNode.virtual()) {
                String rawKey = backSherdNode.getString();
                if(rawKey != null) {
                    backSherd = Material.getMaterial(rawKey.toUpperCase());
                }
            }

            decoratedPotConfig = new ItemStackConfig.DecoratedPotConfig(frontSherd, leftSherd, rightSherd, backSherd);
        }

        // Armor Trim Config
        ItemStackConfig.ArmorTrimConfig armorTrimConfig = new ItemStackConfig.ArmorTrimConfig(null, null);
        ConfigurationNode armorTrimNode = node.node("armor-trim");
        if(!armorTrimNode.virtual()) {
            TrimMaterial trimMaterial = null;
            ConfigurationNode trimMaterialNode = armorTrimNode.node("trim-material");
            if (!trimMaterialNode.virtual()) {
                String rawKey = trimMaterialNode.getString();
                if (rawKey != null) {
                    trimMaterial = RegistryUtil.getTrimMaterial(rawKey).orElse(null);
                }
            }

            TrimPattern trimPattern = null;
            ConfigurationNode trimPatternNode = armorTrimNode.node("trim-pattern");
            if (!trimPatternNode.virtual()) {
                String rawKey = trimPatternNode.getString();
                if (rawKey != null) {
                    trimPattern = RegistryUtil.getTrimPattern(rawKey).orElse(null);
                }
            }

            armorTrimConfig = new ItemStackConfig.ArmorTrimConfig(trimMaterial, trimPattern);
        }

        // Attributes
        List<ItemStackConfig.AttributeConfig> attributeConfigList = new ArrayList<>();
        ConfigurationNode attributesNode = node.node("attributes");
        if(!attributesNode.virtual()) {
            List<? extends ConfigurationNode> childrenNodes = attributesNode.childrenList();

            childrenNodes.forEach(childNode -> {
                Attribute attribute = null;
                ConfigurationNode attributeNode = childNode.node("attribute");
                if(!attributeNode.virtual()) {
                    String rawKey = attributeNode.getString();
                    if(rawKey != null) {
                        attribute = RegistryUtil.getAttribute(rawKey).orElse(null);
                    }
                }

                Double attributeAmount = null;
                ConfigurationNode attributeAmountNode = childNode.node("amount");
                if(!attributeAmountNode.virtual()) {
                    attributeAmount = attributeAmountNode.getDouble();
                }

                AttributeModifier.Operation operation = null;
                ConfigurationNode operationNode = childNode.node("operation");
                if(!operationNode.virtual()) {
                    String rawKey = operationNode.getString();
                    if(rawKey != null) {
                        try {
                            operation = AttributeModifier.Operation.valueOf(rawKey.toUpperCase());
                        } catch (IllegalArgumentException ignored) {}
                    }
                }

                EquipmentSlot equipmentSlot = null;
                ConfigurationNode equipmentSlotNode = childNode.node("equipment-slot");
                if(!equipmentSlotNode.virtual()) {
                    String rawKey = equipmentSlotNode.getString();
                    if(rawKey != null) {
                        try {
                            equipmentSlot = EquipmentSlot.valueOf(rawKey.toUpperCase());
                        } catch (IllegalArgumentException ignored) {}
                    }
                }

                attributeConfigList.add(new ItemStackConfig.AttributeConfig(attribute, attributeAmount, operation, equipmentSlot));
            });
        }

        // Options
        ItemStackConfig.OptionsConfig optionsConfig = new ItemStackConfig.OptionsConfig(false, false, false, false, false);
        ConfigurationNode optionsNode = node.node("options");
        if(!optionsNode.virtual()) {
            Boolean enchantmentGlint = null;
            ConfigurationNode enchantmentGlintNode = optionsNode.node("enchantment-glint");
            if(!enchantmentsNode.virtual()) {
                enchantmentGlint = enchantmentGlintNode.get(Boolean.class);
            }

            Boolean unbreakable = null;
            ConfigurationNode unbreakableNode = optionsNode.node("unbreakable");
            if(!unbreakableNode.virtual()) {
                unbreakable = unbreakableNode.get(Boolean.class);
            }

            Boolean fireResistant  = null;
            ConfigurationNode fireResistantNode = optionsNode.node("fire-resistant");
            if(!fireResistantNode.virtual()) {
                fireResistant = fireResistantNode.get(Boolean.class);
            }

            Boolean hideToolTips  = null;
            ConfigurationNode hideToolTipNode = optionsNode.node("hide-tool-tip");
            if(!hideToolTipNode.virtual()) {
                hideToolTips = hideToolTipNode.get(Boolean.class);
            }

            Boolean glider  = null;
            ConfigurationNode gliderNode = optionsNode.node("glider");
            if(!gliderNode.virtual()) {
                glider = gliderNode.get(Boolean.class);
            }

            optionsConfig = new ItemStackConfig.OptionsConfig(enchantmentGlint, unbreakable, fireResistant, hideToolTips, glider);
        }

        return new ItemStackConfig(
                itemType,
                amount,
                maxStackSize,
                name,
                lore,
                entityType,
                instrument,
                enchantmentConfigList,
                potionConfig,
                colorConfig,
                modelName,
                itemFlagList,
                decoratedPotConfig,
                armorTrimConfig,
                attributeConfigList,
                optionsConfig);
    }

    /**
     * Serializes the {@link ItemStackConfig} from the nodes.
     * @param type The {@link Type}.
     * @param itemStackConfig The {@link ItemStackConfig}.
     * @param node The {@link ConfigurationNode} to write to.
     * @throws SerializationException If serialization fails.
     */
    @Override
    public void serialize(@NonNull Type type, @Nullable ItemStackConfig itemStackConfig, @NonNull ConfigurationNode node) throws SerializationException {
        if(itemStackConfig == null) {
            node.raw(null);
            return;
        }

        // Item Type
        ConfigurationNode itemTypeNode = node.node("item-type");
        if(itemStackConfig.itemType() != null) {
            itemTypeNode.set(String.class, itemStackConfig.itemType().getKey().toString());
        } else {
            itemTypeNode.raw(null);
        }

        // Amount
        ConfigurationNode amountNode = node.node("amount");
        if(itemStackConfig.amount() != null) {
            amountNode.set(itemStackConfig.amount());
        } else {
            amountNode.raw(null);
        }

        // Max Stack Size
        ConfigurationNode maxStackSizeNode = node.node("max-stack-size");
        if(itemStackConfig.maxStackSize() != null) {
            maxStackSizeNode.set(itemStackConfig.maxStackSize());
        } else {
            maxStackSizeNode.raw(null);
        }

        // Name
        ConfigurationNode nameNode = node.node("name");
        if(itemStackConfig.name() != null) {
            nameNode.set(itemStackConfig.name());
        } else {
            nameNode.raw(null);
        }

        // Lore
        ConfigurationNode loreNode = node.node("lore");
        if(!itemStackConfig.lore().isEmpty()) {
            loreNode.setList(String.class, itemStackConfig.lore());
        } else {
            loreNode.raw(null);
        }

        // EntityType
        ConfigurationNode entityTypeNode = node.node("entity-type");
        if(itemStackConfig.entityType() != null) {
            entityTypeNode.set(itemStackConfig.entityType().getKey().toString());
        } else {
            entityTypeNode.raw(null);
        }

        // Instrument
        ConfigurationNode instrumentNode = node.node("instrument");
        if(itemStackConfig.instrument() != null) {
            Registry<MusicInstrument> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT);
            NamespacedKey key = registry.getKey(itemStackConfig.instrument());

            if(key != null) {
                instrumentNode.set(key.toString());
            } else {
                instrumentNode.raw(null);
            }
        } else {
            instrumentNode.raw(null);
        }

        // Enchantments
        ConfigurationNode enchantmentsNode = node.node("enchantments");
        if(!itemStackConfig.enchantments().isEmpty()) {
            for(ItemStackConfig.EnchantmentConfig enchantmentConfig : itemStackConfig.enchantments()) {
                ConfigurationNode enchantmentConfigNode = enchantmentsNode.appendListNode();
                ConfigurationNode enchantmentNode = enchantmentConfigNode.node("enchantment");
                ConfigurationNode levelNode = enchantmentConfigNode.node("level");

                if(enchantmentConfig.enchantment() != null) {
                    enchantmentNode.set(enchantmentConfig.enchantment().getKey().toString());
                } else {
                    enchantmentNode.raw(null);
                }

                if(enchantmentConfig.level() != null) {
                    levelNode.set(enchantmentConfig.level());
                } else {
                    levelNode.raw(null);
                }
            }
        } else {
            enchantmentsNode.raw(null);
        }

        // Potion Config
        ConfigurationNode potionConfigNode = node.node("potion-config");
        if(itemStackConfig.potionConfig().potionType() != null || !itemStackConfig.potionConfig().potionEffects().isEmpty()) {
            ConfigurationNode potionTypeNode = potionConfigNode.node("potion-type");
            if(itemStackConfig.potionConfig().potionType() != null) {
                potionTypeNode.set(itemStackConfig.potionConfig().potionType().getKey().toString());
            } else {
                potionTypeNode.raw(null);
            }

            ConfigurationNode potionEffectsNode = potionConfigNode.node("potion-effects");
            if(!itemStackConfig.potionConfig().potionEffects().isEmpty()) {
                for(ItemStackConfig.PotionEffectConfig potionEffectConfig : itemStackConfig.potionConfig().potionEffects()) {
                    ConfigurationNode childNode = potionEffectsNode.appendListNode();

                    ConfigurationNode typeNode = childNode.node("type");
                    if(potionEffectConfig.type() != null) {
                        typeNode.set(potionEffectConfig.type().getKey().toString());
                    } else {
                        typeNode.raw(null);
                    }

                    ConfigurationNode durationNode = childNode.node("duration-seconds");
                    if(potionEffectConfig.durationSeconds() != null) {
                        durationNode.set(potionEffectConfig.durationSeconds());
                    } else {
                        durationNode.raw(null);
                    }

                    ConfigurationNode amplifierNode = childNode.node("amplifier");
                    if(potionEffectConfig.amplifier() != null) {
                        amplifierNode.set(potionEffectConfig.amplifier());
                    } else {
                        amplifierNode.raw(null);
                    }
                }
            } else {
                potionEffectsNode.raw(null);
            }
        } else {
            potionConfigNode.raw(null);
        }

        // Color Config
        ConfigurationNode colorConfigNode = node.node("color-config");
        boolean hasColor = itemStackConfig.color().red() != null && itemStackConfig.color().green() != null && itemStackConfig.color().blue() != null;
        if(itemStackConfig.color().random() || hasColor) {
            ConfigurationNode randomNode = colorConfigNode.node("random");
            if(itemStackConfig.color().random()) {
                randomNode.set(true);
            } else {
                randomNode.raw(null);
            }

            ConfigurationNode redNode = colorConfigNode.node("red");
            ConfigurationNode greenNode = colorConfigNode.node("green");
            ConfigurationNode blueNode = colorConfigNode.node("blue");
            if(hasColor) {
                redNode.set(itemStackConfig.color().red());
                greenNode.set(itemStackConfig.color().green());
                blueNode.set(itemStackConfig.color().blue());
            } else {
                redNode.raw(null);
                greenNode.raw(null);
                blueNode.raw(null);
            }
        } else {
            colorConfigNode.raw(null);
        }

        // Model Name
        ConfigurationNode modelNameNode = node.node("model-name");
        if(itemStackConfig.modelName() != null) {
            modelNameNode.set(itemStackConfig.modelName());
        } else {
            modelNameNode.raw(null);
        }

        // Item Flags
        ConfigurationNode itemFlagsNode = node.node("item-flags");
        if(!itemStackConfig.itemFlags().isEmpty()) {
            itemFlagsNode.set(itemStackConfig.itemFlags());
        } else {
            itemFlagsNode.raw(null);
        }

        // Decorated Pot Config
        ConfigurationNode decoratedPotNode = node.node("decorated-pot");
        if(itemStackConfig.decoratedPot().frontSherd() != null
                || itemStackConfig.decoratedPot().leftSherd() != null
                || itemStackConfig.decoratedPot().rightSherd() != null
                || itemStackConfig.decoratedPot().backSherd() != null) {
            ConfigurationNode frontSherdNode = decoratedPotNode.node("front-sherd");
            if(itemStackConfig.decoratedPot().frontSherd() != null) {
                frontSherdNode.set(itemStackConfig.decoratedPot().frontSherd().toString());
            } else {
                frontSherdNode.raw(null);
            }

            ConfigurationNode leftSherdNode = decoratedPotNode.node("left-sherd");
            if(itemStackConfig.decoratedPot().leftSherd() != null) {
                leftSherdNode.set(itemStackConfig.decoratedPot().leftSherd().toString());
            } else {
                leftSherdNode.raw(null);
            }

            ConfigurationNode rightSherdNode = decoratedPotNode.node("right-sherd");
            if(itemStackConfig.decoratedPot().rightSherd() != null) {
                rightSherdNode.set(itemStackConfig.decoratedPot().rightSherd().toString());
            } else {
                rightSherdNode.raw(null);
            }

            ConfigurationNode backSherdNode = decoratedPotNode.node("back-sherd");
            if(itemStackConfig.decoratedPot().backSherd() != null) {
                backSherdNode.set(itemStackConfig.decoratedPot().backSherd().toString());
            } else {
                backSherdNode.raw(null);
            }
        } else {
            decoratedPotNode.raw(null);
        }

        // Armor Trim Config
        ConfigurationNode armorTrimNode = node.node("armor-trim");
        if(itemStackConfig.armorTrim().trimMaterial() != null && itemStackConfig.armorTrim().trimPattern() != null) {
            RegistryAccess registryAccess = RegistryAccess.registryAccess();
            Registry<TrimMaterial> trimMaterialRegistry = registryAccess.getRegistry(RegistryKey.TRIM_MATERIAL);
            Registry<TrimPattern> trimPatternRegistry = registryAccess.getRegistry(RegistryKey.TRIM_PATTERN);

            NamespacedKey trimMaterialKey = trimMaterialRegistry.getKey(itemStackConfig.armorTrim().trimMaterial());
            NamespacedKey trimPatternKey = trimPatternRegistry.getKey(itemStackConfig.armorTrim().trimPattern());

            ConfigurationNode trimMaterialNode = armorTrimNode.node("trim-material");
            ConfigurationNode trimPatternNode = armorTrimNode.node("trim-pattern");

            if(trimMaterialKey != null) {
                trimMaterialNode.set(trimMaterialKey.toString());
            } else {
                trimMaterialNode.raw(null);
            }

            if(trimPatternKey != null) {
                trimPatternNode.set(trimPatternKey.toString());
            } else {
                trimPatternNode.raw(null);
            }
        } else {
            if(itemStackConfig.armorTrim().trimMaterial() == null && itemStackConfig.armorTrim().trimPattern() == null) {
                armorTrimNode.raw(null);
            } else {
                RegistryAccess registryAccess = RegistryAccess.registryAccess();
                Registry<TrimMaterial> trimMaterialRegistry = registryAccess.getRegistry(RegistryKey.TRIM_MATERIAL);
                Registry<TrimPattern> trimPatternRegistry = registryAccess.getRegistry(RegistryKey.TRIM_PATTERN);

                ConfigurationNode trimMaterialNode = armorTrimNode.node("trim-material");
                ConfigurationNode trimPatternNode = armorTrimNode.node("trim-pattern");
                if(itemStackConfig.armorTrim().trimMaterial() != null) {
                    NamespacedKey trimMaterialKey = trimMaterialRegistry.getKey(itemStackConfig.armorTrim().trimMaterial());

                    if(trimMaterialKey != null) {
                        trimMaterialNode.set(trimMaterialKey.toString());
                    } else {
                        trimMaterialNode.raw(null);
                    }
                } else {
                    trimMaterialNode.raw(null);
                }

                if(itemStackConfig.armorTrim().trimPattern() != null) {
                    NamespacedKey trimPatternKey = trimPatternRegistry.getKey(itemStackConfig.armorTrim().trimPattern());

                    if(trimPatternKey != null) {
                        trimPatternNode.set(trimPatternKey.toString());
                    } else {
                        trimPatternNode.raw(null);
                    }
                } else {
                    trimPatternNode.raw(null);
                }
            }
        }

        // Attributes
        ConfigurationNode attributesNode = node.node("attributes");
        if(!itemStackConfig.attributes().isEmpty()) {
            for(ItemStackConfig.AttributeConfig attributeConfig : itemStackConfig.attributes()) {
                ConfigurationNode childNode = attributesNode.appendListNode();
                
                ConfigurationNode attributeNode = childNode.node("attribute");
                if(attributeConfig.attribute() != null) {
                    attributeNode.set(attributeConfig.attribute().getKey().toString());
                } else {
                    attributeNode.raw(null);
                }
                
                ConfigurationNode attributeAmountNode = childNode.node("amount");
                if(attributeConfig.amount() != null) {
                    attributeAmountNode.set(attributeConfig.amount());
                } else {
                    attributeAmountNode.raw(null);
                }
                
                ConfigurationNode operationNode = childNode.node("operation");
                if(attributeConfig.operation() != null) {
                    operationNode.set(attributeConfig.operation().toString());
                } else {
                    operationNode.raw(null);
                }
                
                ConfigurationNode equipmentSlotNode = childNode.node("equipment-slot");
                if(attributeConfig.equipmentSlot() != null) {
                    equipmentSlotNode.set(attributeConfig.equipmentSlot().toString());
                } else {
                    equipmentSlotNode.raw(null);
                }
            }
        } else {
            attributesNode.raw(null);
        }

        // Options
        ConfigurationNode optionsNode = node.node("options");
        ItemStackConfig.OptionsConfig optionsConfig = itemStackConfig.options();

        ConfigurationNode enchantmentGlintNode = optionsNode.node("enchantment-glint");
        if(optionsConfig.enchantmentGlint() != null && optionsConfig.enchantmentGlint()) {
            enchantmentGlintNode.set(true);
        } else {
            enchantmentGlintNode.raw(null);
        }

        ConfigurationNode unbreakableNode = optionsNode.node("unbreakable");
        if(optionsConfig.unbreakable() != null && optionsConfig.unbreakable()) {
            unbreakableNode.set(true);
        } else {
            unbreakableNode.raw(null);
        }

        ConfigurationNode fireResistantNode = optionsNode.node("fire-resistant");
        if(optionsConfig.fireResistant() != null && optionsConfig.fireResistant()) {
            fireResistantNode.set(true);
        } else {
            fireResistantNode.raw(null);
        }

        ConfigurationNode hideToolTipNode = optionsNode.node("hide-tool-tip");
        if(optionsConfig.hideToolTip() != null && optionsConfig.hideToolTip()) {
            hideToolTipNode.set(true);
        } else {
            hideToolTipNode.raw(null);
        }
        ConfigurationNode gliderNode = optionsNode.node("glider");
        if(optionsConfig.glider() != null && optionsConfig.glider()) {
            gliderNode.set(true);
        } else {
            gliderNode.raw(null);
        }
    }
}