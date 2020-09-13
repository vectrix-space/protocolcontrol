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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;

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
@SuppressWarnings("unchecked")
public final class PacketRemapper {
  private final Map<Class<?>, PacketStructure<?>> structures = Maps.newHashMap();
  private final MethodHandles.Lookup lookup = MethodHandles.lookup();
  private final Logger logger;

  @Inject
  public PacketRemapper(final Logger logger) {
    this.logger = logger;
  }

  /**
   * Returns a {@link PacketStructure} instance of the specified {@code T} packet.
   *
   * @param packet the packet class
   * @param <T> the packet type
   * @return the packet structure
   */
  public <T> @NonNull PacketStructure<T> structure(final @NonNull Class<?> packet) {
    return (PacketStructure<T>) this.structures.computeIfAbsent(requireNonNull(packet, "packet"), key ->
      PacketStructure.generate(this.logger, this.lookup, key));
  }

  /**
   * Returns a {@link Wrapped} instance of the specified {@code T} packet.
   *
   * @param packet the packet
   * @param <T> the packet type
   * @return the packet wrapper
   */
  public <T> @NonNull Wrapped<T> wrap(final @NonNull T packet) {
    return new Wrapped<>(requireNonNull(packet, "packet"), this.structure(packet.getClass()));
  }

  /**
   * A wrapper containing the {@code T} packet and the specific
   * {@link PacketStructure} this packet uses.
   *
   * @param <T> the packet type
   */
  public static final class Wrapped<T> {
    private final T packet;
    private final PacketStructure<T> structure;

    /* package */ Wrapped(final @NonNull T packet, final @NonNull PacketStructure<T> structure) {
      this.packet = requireNonNull(packet, "packet");
      this.structure = requireNonNull(structure, "structure");
    }

    /**
     * Returns the packet this wrapper belongs to.
     *
     * @return the packet
     */
    public @NonNull T packet() {
      return this.packet;
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
    public <E> @Nullable E get(final @NonNull Class<E> type, final int index) throws Throwable {
      final MethodHandle handle = this.structure.getter(requireNonNull(type, "type"), index);
      if(handle == null) throw new IllegalStateException("Unable to locate method handle");
      final Object field = handle.invoke(this.packet());
      return field != null ? type.cast(field) : null;
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
    public <E> void set(final @NonNull Class<E> type, final int index, final @Nullable E value) throws Throwable {
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
