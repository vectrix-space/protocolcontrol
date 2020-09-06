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
import com.ichorpowered.protocolcontrol.util.Exceptions;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
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
 * {@link Structure} to allow efficient reuse of reflection
 * and {@link MethodHandle} lookups.
 */
@Singleton
@SuppressWarnings("unchecked")
public final class PacketRemapper {
  private final Map<Class<?>, Structure<?>> structures = Maps.newHashMap();
  private final MethodHandles.Lookup lookup = MethodHandles.lookup();
  private final Logger logger;

  @Inject
  public PacketRemapper(final Logger logger) {
    this.logger = logger;
  }

  /**
   * Returns a {@link Structure} instance of the specified {@code T} packet.
   *
   * @param packet the packet class
   * @param <T> the packet type
   * @return the packet structure
   */
  public <T> @NonNull Structure<T> structure(final @NonNull Class<?> packet) {
    return (Structure<T>) this.structures.computeIfAbsent(requireNonNull(packet, "packet"), key ->
      Structure.generate(this.logger, this.lookup, key));
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
   * An wrapper containing the {@code T} packet and the specific
   * {@link Structure} this packet uses.
   *
   * @param <T> the packet type
   */
  public static final class Wrapped<T> {
    private final T packet;
    private final Structure<T> structure;

    /* package */ Wrapped(final @NonNull T packet, final @NonNull Structure<T> structure) {
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

  /**
   * Represents the structure of the packets data. This provides
   * the {@link MethodHandle}s to extract data from the specified
   * packet without needing to create them every time.
   *
   * @param <T> the packet type
   */
  public static final class Structure<T> {
    private static <E> @NonNull Structure<E> generate(final @NonNull Logger logger, final MethodHandles.@NonNull Lookup lookup,
                                                      final @NonNull Class<E> packet) {
      final Map<Class<?>, List<Handle>> handleMap = Maps.newHashMap();

      // Search function to grab fields from the packet class and other super classes.
      Structure.find(packet, Class::getSuperclass, fields -> {
        for(final Field field : fields) {
          try {
            field.setAccessible(true);

            final List<Handle> methodHandles = handleMap.computeIfAbsent(field.getType(), key -> new ArrayList<>());
            methodHandles.add(new Handle(lookup.unreflectGetter(field), lookup.unreflectSetter(field)));
          } catch(Throwable throwable) {
            Exceptions.catchingReport(
              throwable,
              logger,
              PacketRemapper.class,
              "remapper",
              "Encountered a major exception attempting to access packet field",
              report -> report.category("structure_generate")
                .detail("packet", packet)
                .detail("field", field.getName())
            );
          }
        }
      });

      return new Structure<>(packet, handleMap);
    }

    private static void find(final @NonNull Class<?> search, final @NonNull Function<Class<?>, Class<?>> superSearch,
                             final @NonNull Consumer<Field[]> fieldSearch) {
      Class<?> searchClass = search;
      while(searchClass != null) {
        final Field[] fields = searchClass.getDeclaredFields();
        fieldSearch.accept(fields);

        searchClass = superSearch.apply(searchClass);
      }
    }

    private final Class<T> packet;
    private final Map<Class<?>, List<Handle>> handles;

    /* package */ Structure(final @NonNull Class<T> packet, final @NonNull Map<Class<?>, List<Handle>> handles) {
      this.packet = packet;
      this.handles = handles;
    }

    public @NonNull Class<T> packet() {
      return this.packet;
    }

    public @Nullable MethodHandle getter(final @NonNull Class<?> type, final int index) {
      requireNonNull(type, "type");
      final List<Handle> handles = this.handles.get(type);
      if(handles == null || index < 0 || index >= handles.size()) return null;
      return handles.get(index).getter();
    }

    public @Nullable MethodHandle setter(final @NonNull Class<?> type, final int index) {
      requireNonNull(type, "type");
      final List<Handle> handles = this.handles.get(type);
      if(handles == null || index < 0 || index >= handles.size()) return null;
      return handles.get(index).setter();
    }
  }

  public static final class Handle {
    private final MethodHandle getter;
    private final MethodHandle setter;

    /* package */ Handle(final @NonNull MethodHandle getter, final @NonNull MethodHandle setter) {
      this.getter = getter;
      this.setter = setter;
    }

    public @NonNull MethodHandle getter() {
      return this.getter;
    }

    public @NonNull MethodHandle setter() {
      return this.setter;
    }
  }
}
