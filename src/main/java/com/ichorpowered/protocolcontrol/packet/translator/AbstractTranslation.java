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

import org.checkerframework.checker.nullness.qual.NonNull;

import static java.util.Objects.requireNonNull;

public abstract class AbstractTranslation<T> {
  private final Class<T> type;
  private T instance;

  public AbstractTranslation(final @NonNull Class<T> type, final @NonNull T instance) {
    this.type = requireNonNull(type, "type");
    this.instance = requireNonNull(instance, "instance");
  }

  /**
   * Returns the raw {@code T} translatable type.
   *
   * @return the translatable type
   */
  public @NonNull Class<T> type() {
    return this.type;
  }

  /**
   * Returns the raw {@code T} translatable object.
   *
   * @return the translatable object
   */
  public @NonNull T instance() {
    return this.instance;
  }

  /**
   * Sets the raw {@code T} translatable object.
   *
   * @param instance the translatable object
   */
  public void instance(final @NonNull T instance) {
    this.instance = requireNonNull(instance, "instance");
  }
}
