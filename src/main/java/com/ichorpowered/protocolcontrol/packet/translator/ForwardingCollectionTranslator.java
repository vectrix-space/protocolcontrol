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
import java.util.Collection;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public final class ForwardingCollectionTranslator<T, F extends Collection<T>> implements Translator<F> {
  private final Translator<T> translator;
  private final TypeToken<?> translatable;
  private final Supplier<F> collectionSupplier;

  public ForwardingCollectionTranslator(final @NonNull Translator<T> translator,
                                        final @NonNull TypeToken<?> translatable,
                                        final @NonNull Supplier<F> collectionSupplier) {
    this.translator = translator;
    this.translatable = translatable;
    this.collectionSupplier = collectionSupplier;
  }

  @Override
  public @Nullable F wrap(final @Nullable Object object) {
    if(object == null) return null;
    final F translated = this.collectionSupplier.get();
    for(final Object element : (Collection<?>) object) {
      translated.add(requireNonNull(this.translator.wrap(element), "element"));
    }
    return translated;
  }

  @Override
  public <E> @Nullable E unwrap(final @Nullable F translation) {
    if(translation == null) return null;
    final Collection<?> translatable = this.collectionSupplier.get();
    for(final T element : translation) {
      translatable.add(requireNonNull(this.translator.unwrap(element), "element"));
    }
    return (E) translatable;
  }

  @Override
  public @NonNull TypeToken<?> translatable() {
    return this.translatable;
  }
}
