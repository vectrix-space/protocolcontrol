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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.ichorpowered.protocolcontrol.packet.translator.Translator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.advancement.Advancement;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public final class AdvancementAdditionsTranslator implements Translator<Collection<Advancement>> {
  @Override
  public @Nullable Collection<Advancement> wrap(final @Nullable Object object) {
    if(object == null) return null;
    final Set<Advancement> advancements = Sets.newHashSet();
    final Map<ResourceLocation, net.minecraft.advancements.Advancement.Builder> advancementMap = (Map<ResourceLocation, net.minecraft.advancements.Advancement.Builder>) object;
    for(final Map.Entry<ResourceLocation, net.minecraft.advancements.Advancement.Builder> entry : advancementMap.entrySet()) {
      advancements.add((Advancement) entry.getValue().build(entry.getKey()));
    }
    return advancements;
  }

  @Override
  public <E> @Nullable E unwrap(final @Nullable Collection<Advancement> translation) {
    if(translation == null) return null;
    final Map<ResourceLocation, net.minecraft.advancements.Advancement.Builder> advancementMap = Maps.newHashMap();
    for(final Advancement advancement : translation) {
      final net.minecraft.advancements.Advancement element = (net.minecraft.advancements.Advancement) advancement;
      advancementMap.put(element.getId(), element.copy());
    }
    return (E) advancementMap;
  }

  @Override
  public @NonNull TypeToken<?> translatable() {
    return new TypeToken<Map<ResourceLocation, net.minecraft.advancements.Advancement.Builder>>() {};
  }
}
