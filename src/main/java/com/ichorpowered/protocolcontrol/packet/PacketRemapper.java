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

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.packet.translator.Translator;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * An efficient packet remapping processor that maps packet
 * fields to their types & order. This can then be used to
 * get or set packet fields.
 *
 * This packet remapper stores mapped packets into a
 * {@link PacketStructure} to allow efficient reuse of
 * reflection and {@link MethodHandle} lookups.
 */
@Singleton
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public final class PacketRemapper {
  private final Map<Class<?>, PacketStructure> structures = Maps.newHashMap();
  private final MethodHandles.Lookup lookup = MethodHandles.lookup();
  private final Logger logger;
  private final PacketTranslation translation;

  @Inject
  public PacketRemapper(final Logger logger,
                        final PacketTranslation translation) {
    this.logger = logger;
    this.translation = translation;
  }

  /**
   * Returns a {@link PacketStructure} instance of the specified {@code T} packet.
   *
   * @param packet the packet class
   * @return the packet structure
   */
  public @NonNull PacketStructure structure(final @NonNull Class<?> packet) {
    return this.structures.computeIfAbsent(requireNonNull(packet, "packet"), key ->
      PacketStructure.generate(this.logger, this.lookup, key));
  }

  /**
   * Returns a {@link Wrapped} instance of a new packet created
   * by the specified {@link PacketType} and {@link PacketDirection}.
   *
   * @param type the packet type
   * @param direction the packet direction
   * @return the packet wrapper
   * @throws Throwable exceptions related to creating the packet
   */
  public @NonNull Wrapped wrap(final @NonNull PacketType type, final @NonNull PacketDirection direction) throws Throwable {
    final Object packet = requireNonNull(type, "type").create(requireNonNull(direction, "direction"));
    return this.wrap(packet);
  }

  /**
   * Returns a {@link Wrapped} instance of the specified packet.
   *
   * @param packet the packet
   * @return the packet wrapper
   */
  public @NonNull Wrapped wrap(final @NonNull Object packet) {
    return new Wrapped(requireNonNull(packet, "packet"), this.structure(packet.getClass()), this.translation);
  }

  /**
   * A wrapper containing the packet and the specific
   * {@link PacketStructure} this packet uses.
   */
  public static final class Wrapped {
    private final Object packet;
    private final PacketStructure structure;
    private final PacketTranslation translation;

    /* package */ Wrapped(final @NonNull Object packet,
                          final @NonNull PacketStructure structure,
                          final @NonNull PacketTranslation translation) {
      this.packet = requireNonNull(packet, "packet");
      this.structure = requireNonNull(structure, "structure");
      this.translation = requireNonNull(translation, "translation");
    }

    /**
     * Returns the packet this wrapper belongs to.
     *
     * @return the packet
     */
    public @NonNull Object packet() {
      return this.packet;
    }

    /**
     * Returns the element {@code E} of {@link TypeToken} type at
     * the specified index position of the fields in the packet
     * class using the appropriate translator.
     *
     * @param type the element type token
     * @param index the element index
     * @param <E> the element type
     * @return the element
     * @throws Throwable exceptions attempting to locate the
     *                   translator, locate or parse the field
     *                   or the fields element
     */
    public <E> @Nullable E get(final @NonNull TypeToken<E> type, final int index) throws Throwable {
      final Translator<E> translator = this.translation.translate(type);
      if(translator == null) return this.getRaw(type, index);
      final Object field = this.getRaw(translator.translatable(), index);
      return translator.wrap(field);
    }

    /**
     * Returns the element {@code E} of {@link TypeToken} type at
     * the specified index position of the fields in the packet
     * class.
     *
     * @param type the element type token
     * @param index the element index
     * @param <E> the element type
     * @return the element
     * @throws Throwable exceptions attempting to locate or parse
     *                   the field or the fields element
     */
    public <E> @Nullable E getRaw(final @NonNull TypeToken<E> type, final int index) throws Throwable {
      final MethodHandle handle = this.structure.getter(requireNonNull(type, "type"), index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      final Object field = handle.invoke(this.packet());
      return field != null ? (E) field : null;
    }

    /**
     * Returns the element {@code E} of type {@link Class} at
     * the specified index position of the fields in the packet
     * class.
     *
     * @param type the element class type
     * @param index the element index
     * @param <E> the element type
     * @return the element
     * @throws Throwable exceptions attempting to locate or parse
     *                   the field or the fields element
     */
    public <E> @Nullable E getRaw(final @NonNull Class<E> type, final int index) throws Throwable {
      final MethodHandle handle = this.structure.getter(requireNonNull(type, "type"), index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      final Object field = handle.invoke(this.packet());
      return field != null ? type.cast(field) : null;
    }

    /**
     * Sets the element {@code E} of {@link TypeToken} type at the
     * specified index position of the fields in the packet class
     * to the specified value using the appropriate translator.
     *
     * @param type the element type token
     * @param index the element index
     * @param value the element
     * @param <E> the element type
     * @throws Throwable exceptions attempting to locate the
     *                   translator or locate the field
     */
    public <E> void set(final @NonNull TypeToken<E> type, final int index, final @Nullable E value) throws Throwable {
      final Translator<E> translator = this.translation.translate(type);
      if(translator == null) throw new IllegalStateException("Unable to locate translator");
      this.setRaw(translator.translatable(), index, translator.unwrap(value));
    }

    /**
     * Sets the element {@code E} of {@link TypeToken} type at the
     * specified index position of the fields in the packet class
     * to the specified value.
     *
     * @param type the element type token
     * @param index the element index
     * @param value the element
     * @param <E> the element type
     * @throws Throwable exceptions attempting to locate the field
     */
    public <E> void setRaw(final @NonNull TypeToken<E> type, final int index, final @Nullable E value) throws Throwable {
      final MethodHandle handle = this.structure.setter(requireNonNull(type, "type"), index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      handle.invoke(this.packet(), value);
    }

    /**
     * Sets the element {@code E} of type {@link Class} at the
     * specified index position of the fields in the packet class
     * to the specified value.
     *
     * @param type the element class type
     * @param index the element index
     * @param value the element
     * @param <E> the element type
     * @throws Throwable exceptions attempting to locate the field
     */
    public <E> void setRaw(final @NonNull Class<E> type, final int index, final @Nullable E value) throws Throwable {
      final MethodHandle handle = this.structure.setter(requireNonNull(type, "type"), index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      handle.invoke(this.packet(), value);
    }

    /**
     * Returns the {@code int} element at the specified index
     * position of the fields in the packet class.
     *
     * @param index the element index
     * @return the int element
     * @throws Throwable exceptions attempting to locate or parse
     *                   the field or the fields element
     */
    public int getInt(final int index) throws Throwable {
      final MethodHandle handle = this.structure.getter(int.class, index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      final Object field = handle.invoke(this.packet());
      return field != null ? (int) field : 0;
    }

    /**
     * Sets the {@code int} element at the specified index position
     * of the fields in the packet class to the specified value.
     *
     * @param index the element index
     * @param value the int element
     * @throws Throwable exceptions attempting to locate the field
     */
    public void setInt(final int index, final int value) throws Throwable {
      final MethodHandle handle = this.structure.setter(int.class, index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      handle.invoke(this.packet(), value);
    }

    /**
     * Returns the {@code double} element at the specified index
     * position of the fields in the packet class.
     *
     * @param index the element index
     * @return the double element
     * @throws Throwable exceptions attempting to locate or parse
     *                   the field or the fields element
     */
    public double getDouble(final int index) throws Throwable {
      final MethodHandle handle = this.structure.getter(double.class, index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      final Object field = handle.invoke(this.packet());
      return field != null ? (double) field : 0D;
    }

    /**
     * Sets the {@code double} element at the specified index position
     * of the fields in the packet class to the specified value.
     *
     * @param index the element index
     * @param value the double element
     * @throws Throwable exceptions attempting to locate the field
     */
    public void setDouble(final int index, final double value) throws Throwable {
      final MethodHandle handle = this.structure.setter(double.class, index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      handle.invoke(this.packet(), value);
    }
  }
}
