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
package com.ichorpowered.protocolcontrol.packet.translator;

import com.google.common.reflect.TypeToken;
import java.util.Map;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public final class ForwardingMapTranslator<K, V, F extends Map<K, V>> implements Translator<F> {
  private final Translator<K> keyTranslator;
  private final Translator<V> valueTranslator;
  private final TypeToken<?> translatable;
  private final Supplier<F> mapSupplier;

  public ForwardingMapTranslator(final @NonNull Translator<K> keyTranslator,
                                 final @NonNull Translator<V> valueTranslator,
                                 final @NonNull TypeToken<?> translatable,
                                 final @NonNull Supplier<F> mapSupplier) {
    this.keyTranslator = keyTranslator;
    this.valueTranslator = valueTranslator;
    this.translatable = translatable;
    this.mapSupplier = mapSupplier;
  }

  @Override
  public @Nullable F wrap(final @Nullable Object object) {
    if(object == null) return null;
    final F translated = this.mapSupplier.get();
    for(final Map.Entry<?, ?> element : ((Map<?, ?>) object).entrySet()) {
      translated.put(requireNonNull(this.keyTranslator.wrap(element.getKey()), "key"),
        requireNonNull(this.valueTranslator.wrap(element.getValue()), "value"));
    }
    return translated;
  }

  @Override
  public <E> @Nullable E unwrap(final @Nullable F translation) {
    if(translation == null) return null;
    final Map<?, ?> translatable = this.mapSupplier.get();
    for(final Map.Entry<K, V> element : translation.entrySet()) {
      translatable.put(requireNonNull(this.keyTranslator.unwrap(element.getKey()), "key"),
        requireNonNull(this.valueTranslator.unwrap(element.getValue()), "value"));
    }
    return (E) translatable;
  }

  @Override
  public @NonNull TypeToken<?> translatable() {
    return this.translatable;
  }
}
