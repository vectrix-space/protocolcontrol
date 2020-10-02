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
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.packet.translator.DelegateTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.type.ComponentTextTranslator;
import com.ichorpowered.protocolcontrol.packet.translator.type.Vector3iTranslator;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

@Singleton
@SuppressWarnings("UnstableApiUsage")
public final class PacketTranslations {
  private final PacketTranslation translation;

  @Inject
  public PacketTranslations(final PacketTranslation translation) {
    this.translation = translation;
  }

  public void register() {
    this.translation.translate(TypeToken.of(BlockType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.block.Block.class)));
    this.translation.translate(TypeToken.of(BlockState.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.block.state.IBlockState.class)));
    this.translation.translate(TypeToken.of(ItemStack.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.item.ItemStack.class)));
    this.translation.translate(TypeToken.of(HandType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.EnumHand.class)));
    this.translation.translate(TypeToken.of(ChatType.class), new DelegateTranslator<>(TypeToken.of(net.minecraft.util.text.ChatType.class)));
    this.translation.translate(TypeToken.of(Text.class), new ComponentTextTranslator());
    this.translation.translate(TypeToken.of(Vector3i.class), new Vector3iTranslator());
  }
}
