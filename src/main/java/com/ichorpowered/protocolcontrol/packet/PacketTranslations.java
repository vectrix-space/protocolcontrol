/*
 * This file is part of ProtocolControl, licensed under the MIT License (MIT).
 *
 * Copyright (c) IchorPowered <http://ichorpowered.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ichorpowered.protocolcontrol.packet;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.packet.translation.type.ResourceKey;
import com.ichorpowered.protocolcontrol.packet.translator.DelegateTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.ForwardingCollectionTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.ForwardingMapTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.type.AdvancementAdditionsTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.type.ComponentTextTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.type.ResourceKeyTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.type.Vector3iTranslator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementProgress;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulty;

@Singleton
@SuppressWarnings("UnstableApiUsage")
public final class PacketTranslations {
  private final PacketTranslation translation;

  @Inject
  public PacketTranslations(final PacketTranslation translation) {
    this.translation = translation;
  }

  public void register() {
    this.translation.translate(TypeToken.of(AdvancementProgress.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.advancements.AdvancementProgress.class)));
    this.translation.translate(TypeToken.of(BlockState.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.block.state.IBlockState.class)));
    this.translation.translate(TypeToken.of(BlockType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.block.Block.class)));
    this.translation.translate(TypeToken.of(BossBarColor.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.world.BossInfo.Color.class)));
    this.translation.translate(TypeToken.of(BossBarOverlay.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.world.BossInfo.Overlay.class)));
    this.translation.translate(TypeToken.of(ChatType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.text.ChatType.class)));
    this.translation.translate(TypeToken.of(ChatVisibility.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.class)));
    this.translation.translate(TypeToken.of(Difficulty.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.world.EnumDifficulty.class)));
    this.translation.translate(TypeToken.of(EquipmentType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.inventory.EntityEquipmentSlot.class)));
    this.translation.translate(TypeToken.of(GameMode.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.world.GameType.class)));
    this.translation.translate(TypeToken.of(GameProfile.class), new DelegateTranslator<>(TypeToken.of(com.mojang.authlib.GameProfile.class)));
    this.translation.translate(TypeToken.of(GeneratorType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.world.WorldType.class)));
    this.translation.translate(TypeToken.of(HandPreference.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.EnumHandSide.class)));
    this.translation.translate(TypeToken.of(HandType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.EnumHand.class)));
    this.translation.translate(TypeToken.of(ItemStack.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.item.ItemStack.class)));
    this.translation.translate(TypeToken.of(ParticleType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.EnumParticleTypes.class)));
    this.translation.translate(TypeToken.of(PotionEffectType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.potion.Potion.class)));
    this.translation.translate(TypeToken.of(SoundCategory.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.SoundCategory.class)));
    this.translation.translate(TypeToken.of(SoundType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.SoundEvent.class)));

    this.translation.translate(new TypeToken<Collection<Advancement>>() {}, new AdvancementAdditionsTranslator());
    this.translation.translate(TypeToken.of(ResourceKey.class), new ResourceKeyTranslator());
    this.translation.translate(TypeToken.of(Text.class), new ComponentTextTranslator());
    this.translation.translate(TypeToken.of(Vector3i.class), new Vector3iTranslator());

    this.translation.translate(new TypeToken<Set<ResourceKey>>() {}, new ForwardingCollectionTranslator<>(
      new ResourceKeyTranslator(),
      new TypeToken<Set<net.minecraft.util.ResourceLocation>>() {},
      Sets::newHashSet
    ));

    this.translation.translate(new TypeToken<Map<ResourceKey, AdvancementProgress>>() {}, new ForwardingMapTranslator<>(
      new ResourceKeyTranslator(),
      new DelegateTranslator<>(TypeToken.of(net.minecraft.advancements.AdvancementProgress.class)),
      new TypeToken<Map<net.minecraft.util.ResourceLocation, net.minecraft.advancements.AdvancementProgress>>() {},
      Maps::newHashMap
    ));
  }
}
