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

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.packet.translator.Translator;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A registry of {@link Translator}s for translation types.
 */
@Singleton
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public final class PacketTranslation {
  private final Map<TypeToken<?>, Translator<?>> translators = new HashMap<>();

  @Inject
  public PacketTranslation() {
  }

  /**
   * Returns a {@link Translator} for the specified {@link TypeToken}
   * translation type.
   *
   * @param type the translation type token
   * @param <T> the translation type
   * @return the translator, if it exists
   */
  public <T> @Nullable Translator<T> translate(final @NonNull TypeToken<T> type) {
    return (Translator<T>) this.translators.get(requireNonNull(type, "type"));
  }

  /**
   * Sets the specified {@link TypeToken} translation type with
   * the specified {@link Translator}.
   *
   * @param type the translation type token
   * @param translator the translator
   * @param <T> the translation type
   */
  public <T> void translate(final @NonNull TypeToken<T> type, final @NonNull Translator<T> translator) {
    this.translators.put(requireNonNull(type, "type"), requireNonNull(translator, "translator"));
  }
}
