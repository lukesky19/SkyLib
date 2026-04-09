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
package com.github.lukesky19.skylib.paper;

import com.github.lukesky19.skylib.common.platform.PlatformUtils;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.paper.api.serializer.*;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

/**
 * The plugin's boostrap.
 */
@SuppressWarnings("unused") // Entry point for the paper plugin.
public final class SkyLibBootstrap implements PluginBootstrap {
    /**
     * Default Constructor.
     */
    public SkyLibBootstrap() {}

    /**
     * The plugin's bootstrap.
     * @param bootstrapContext A {@link BootstrapContext}.
     */
    @Override
    public void bootstrap(@NonNull BootstrapContext bootstrapContext) {
        PlatformUtils.init(TypeSerializerCollection.builder()
                .registerExact(AttributeModifier.Operation.class, new AttributeModifierOperationSerializer())
                .registerExact(Attribute.class, new AttributeSerializer())
                .registerExact(BlockType.class, new BlockTypeSerializer())
                .registerExact(Enchantment.class, new EnchantmentSerializer())
                .registerExact(EntityType.class, new EntityTypeSerializer())
                .registerExact(EquipmentSlot.class, new EquipmentSlotSerializer())
                .registerExact(ItemType.class, new ItemTypeSerializer())
                .registerExact(Material.class, new MaterialSerializer())
                .registerExact(MusicInstrument.class, new MusicInstrumentSerializer())
                .registerExact(PotionEffectType.class, new PotionEffectTypeSerializer())
                .registerExact(PotionType.class, new PotionTypeSerializer())
                .registerExact(TrimMaterial.class, new TrimMaterialSerializer())
                .registerExact(TrimPattern.class, new TrimPatternSerializer())
                .registerExact(ItemStackConfig.class, new ItemStackConfigSerializer())
                .build());
    }
}