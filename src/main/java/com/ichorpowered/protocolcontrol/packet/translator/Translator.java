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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a function to wrap and unwrap raw packet
 * field types, to a {@code T} in order to make it easy
 * to manipulate packet data efficiently.
 *
 * @param <T> the translation type
 */
@SuppressWarnings("UnstableApiUsage")
public interface Translator<T> {
  /**
   * Wraps the specified object into this {@code T}
   * translation.
   *
   * @param object the translatable object
   * @return the translation object
   */
  @Nullable T wrap(@Nullable Object object);

  /**
   * Unwraps the specified {@code T} translation into
   * an object.
   *
   * @param translation the translation object
   * @return the translatable object
   */
  <E> @Nullable E unwrap(@Nullable T translation);

  /**
   * Returns the translatable {@link TypeToken}.
   *
   * @return the translatable type
   */
  @NonNull TypeToken<?> translatable();
}
