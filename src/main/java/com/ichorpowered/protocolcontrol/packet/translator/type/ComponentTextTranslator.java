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
package com.ichorpowered.protocolcontrol.packet.translator.type;

import com.google.common.reflect.TypeToken;
import com.ichorpowered.protocolcontrol.packet.translator.Translator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.util.text.ITextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public final class ComponentTextTranslator implements Translator<Component> {
  @Override
  public @Nullable Component wrap(final @Nullable Object object) {
    if(object == null) return null;
    final ITextComponent message = (ITextComponent) object;
    return GsonComponentSerializer.gson().deserialize(ITextComponent.Serializer.toJson(message));
  }

  @Override
  public <E> @Nullable E unwrap(final @Nullable Component translation) {
    if(translation == null) return null;
    final ITextComponent message = ITextComponent.Serializer.fromJson(GsonComponentSerializer.gson().serializeToTree(translation));
    return (E) message;
  }

  @Override
  public @NonNull TypeToken<?> translatable() {
    return TypeToken.of(ITextComponent.class);
  }
}
