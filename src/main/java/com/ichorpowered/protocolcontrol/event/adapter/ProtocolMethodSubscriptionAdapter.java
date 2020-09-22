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
package com.ichorpowered.protocolcontrol.event.adapter;

import com.google.common.base.MoreObjects;
import com.ichorpowered.protocolcontrol.packet.PacketDirection;
import com.ichorpowered.protocolcontrol.packet.PacketType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import net.kyori.event.ReifiedEvent;
import net.kyori.event.method.EventExecutor;
import net.kyori.event.method.MethodSubscriptionAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ProtocolMethodSubscriptionAdapter implements MethodSubscriptionAdapter<Object> {
  private final EventBus<Object> bus;
  private final EventExecutor.Factory<Object, Object> factory;
  private final ProtocolMethodScanner methodScanner;

  public ProtocolMethodSubscriptionAdapter(final @NonNull EventBus<Object> bus,
                                           final EventExecutor.@NonNull Factory<Object, Object> factory,
                                           final ProtocolMethodScanner methodScanner) {
    this.bus = bus;
    this.factory = factory;
    this.methodScanner = methodScanner;
  }

  @Override
  public void register(final @NonNull Object listener) {
    this.findSubscribers(listener, this.bus::register);
  }

  @Override
  public void unregister(final @NonNull Object listener) {
    this.bus.unregister(h -> h instanceof ProtocolMethodSubscriptionAdapter.MethodEventSubscriber && ((ProtocolMethodSubscriptionAdapter.MethodEventSubscriber) h).listener() == listener);
  }

  private void findSubscribers(final @NonNull Object listener, final BiConsumer<@NonNull Class<?>, @NonNull EventSubscriber<Object>> consumer) {
    for(final Method method : listener.getClass().getDeclaredMethods()) {
      if(!this.methodScanner.shouldRegister(listener, method)) {
        continue;
      }
      if(method.getParameterCount() != 1) {
        throw new ProtocolMethodSubscriptionAdapter.SubscriberGenerationException("Unable to create an event subscriber for method '" + method + "'. Method must have only one parameter.");
      }
      final Class<?> methodParameterType = method.getParameterTypes()[0];
      if(!this.bus.eventType().isAssignableFrom(methodParameterType)) {
        throw new ProtocolMethodSubscriptionAdapter.SubscriberGenerationException("Unable to create an event subscriber for method '" + method + "'. " +
          "Method parameter type '" + methodParameterType + "' does not extend event type '" + this.bus.eventType() + '\'');
      }
      final EventExecutor<Object, Object> executor;
      try {
        executor = this.factory.create(listener, method);
      } catch(final Exception e) {
        throw new ProtocolMethodSubscriptionAdapter.SubscriberGenerationException("Encountered an exception while creating an event subscriber for method '" + method + '\'', e);
      }
      final PacketType packetType = this.methodScanner.packetType(method);
      final PacketDirection packetDirection = this.methodScanner.packetDirection(method);
      final int postOrder = this.methodScanner.postOrder(listener, method);
      final boolean consumeCancelled = this.methodScanner.consumeCancelledEvents(listener, method);
      consumer.accept(methodParameterType, new ProtocolMethodSubscriptionAdapter.MethodEventSubscriber(methodParameterType, method, executor, listener, packetType, packetDirection, postOrder, consumeCancelled));
    }
  }

  /**
   * Exception thrown when a {@link EventSubscriber} cannot be generated for a
   * {@link Method} at runtime.
   */
  public static final class SubscriberGenerationException extends RuntimeException {
    private static final long serialVersionUID = -313293888991253131L;

    private
    SubscriberGenerationException(final String message) {
      super(message);
    }

    SubscriberGenerationException(final String message, final Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Implements {@link EventSubscriber} for a given {@link Method}.
   */
  public static final class MethodEventSubscriber extends ProtocolEventSubscriber<Object> {
    private final Class<?> event;
    private final @Nullable Type generic;
    private final EventExecutor<Object, Object> executor;
    private final Object listener;

    MethodEventSubscriber(final Class<?> eventClass,
                          final @NonNull Method method,
                          final @NonNull EventExecutor<Object, Object> executor,
                          final @NonNull Object listener,
                          final @NonNull PacketType packetType,
                          final @NonNull PacketDirection packetDirection,
                          final int postOrder,
                          final boolean includeCancelled) {
      super(packetType, packetDirection, postOrder, includeCancelled);
      this.event = eventClass;
      this.generic = ReifiedEvent.class.isAssignableFrom(this.event) ? genericType(method.getGenericParameterTypes()[0]) : null;
      this.executor = executor;
      this.listener = listener;
    }

    private static @Nullable Type genericType(final Type type) {
      if(type instanceof ParameterizedType) {
        return ((ParameterizedType) type).getActualTypeArguments()[0];
      }
      return null;
    }

    @NonNull Object listener() {
      return this.listener;
    }

    @Override
    public void invoke(final @NonNull Object event) throws Throwable {
      this.executor.invoke(this.listener, event);
    }

    @Override
    public @Nullable Type genericType() {
      return this.generic;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.event, this.generic, this.executor, this.listener, this.packetType(), this.packetDirection(), this.postOrder(), this.consumeCancelledEvents());
    }

    @Override
    public boolean equals(final Object other) {
      if(this == other) return true;
      if(!(other instanceof MethodEventSubscriber)) return false;
      final ProtocolMethodSubscriptionAdapter.MethodEventSubscriber that = (ProtocolMethodSubscriptionAdapter.MethodEventSubscriber) other;
      return Objects.equals(this.event, that.event)
        && Objects.equals(this.generic, that.generic)
        && Objects.equals(this.executor, that.executor)
        && Objects.equals(this.listener, that.listener)
        && Objects.equals(this.packetType(), that.packetType())
        && Objects.equals(this.packetDirection(), that.packetDirection())
        && Objects.equals(this.postOrder(), that.postOrder())
        && Objects.equals(this.consumeCancelledEvents(), that.consumeCancelledEvents());
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
        .add("event", this.event)
        .add("generic", this.generic)
        .add("executor", this.executor)
        .add("listener", this.listener)
        .add("packetType", this.packetType())
        .add("packetDirection", this.packetDirection())
        .add("priority", this.postOrder())
        .add("includeCancelled", this.consumeCancelledEvents())
        .toString();
    }
  }
}
