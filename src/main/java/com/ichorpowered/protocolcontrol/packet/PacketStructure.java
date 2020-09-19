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
 * Represents the structure of the packets data. This provides
 * the {@link MethodHandle}s to extract data from the specified
 * packet without needing to create them every time.
 *
 * @param <T> the packet type
 */
@SuppressWarnings("UnstableApiUsage")
public final class PacketStructure<T> {
  protected static <E> @NonNull PacketStructure<E> generate(final @NonNull Logger logger, final MethodHandles.@NonNull Lookup lookup,
                                                            final @NonNull Class<E> packet) {
    final Map<TypeToken<?>, List<Handle>> handleMap = Maps.newHashMap();
    PacketStructure.find(packet, Class::getSuperclass, fields -> {
      for(final Field field : fields) {
        try {
          field.setAccessible(true);
          final List<Handle> methodHandles = handleMap.computeIfAbsent(TypeToken.of(field.getType()), key -> new ArrayList<>());
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
    return new PacketStructure<>(packet, handleMap);
  }

  protected static void find(final @NonNull Class<?> search, final @NonNull Function<Class<?>, Class<?>> superSearch,
                             final @NonNull Consumer<Field[]> fieldSearch) {
    Class<?> searchClass = search;
    while(searchClass != null) {
      final Field[] fields = searchClass.getDeclaredFields();
      fieldSearch.accept(fields);
      searchClass = superSearch.apply(searchClass);
    }
  }

  private final Class<T> packet;
  private final Map<TypeToken<?>, List<Handle>> handles;

  /* package */ PacketStructure(final @NonNull Class<T> packet, final @NonNull Map<TypeToken<?>, List<Handle>> handles) {
    this.packet = packet;
    this.handles = handles;
  }

  public @NonNull Class<T> packet() {
    return this.packet;
  }

  public @Nullable MethodHandle getter(final @NonNull TypeToken<?> type, final int index) {
    return this.getter0(requireNonNull(type, "type"), index);
  }

  public @Nullable MethodHandle getter(final @NonNull Class<?> type, final int index) {
    return this.getter0(TypeToken.of(requireNonNull(type, "type")), index);
  }

  public @Nullable MethodHandle setter(final @NonNull TypeToken<?> type, final int index) {
    return this.setter0(requireNonNull(type, "type"), index);
  }

  public @Nullable MethodHandle setter(final @NonNull Class<?> type, final int index) {
    return this.setter0(TypeToken.of(requireNonNull(type, "type")), index);
  }

  private @Nullable MethodHandle getter0(final @NonNull TypeToken<?> type, final int index) {
    final List<Handle> handles = this.handles.get(type);
    if(handles == null || index < 0 || index >= handles.size()) return null;
    return handles.get(index).getter();
  }

  private @Nullable MethodHandle setter0(final @NonNull TypeToken<?> type, final int index) {
    final List<Handle> handles = this.handles.get(type);
    if(handles == null || index < 0 || index >= handles.size()) return null;
    return handles.get(index).setter();
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
