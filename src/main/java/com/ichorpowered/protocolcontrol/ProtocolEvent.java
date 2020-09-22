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
package com.ichorpowered.protocolcontrol;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.event.EventHandler;
import com.ichorpowered.protocolcontrol.event.EventOrder;
import com.ichorpowered.protocolcontrol.event.PacketEvent;
import com.ichorpowered.protocolcontrol.event.adapter.ProtocolEventSubscriber;
import com.ichorpowered.protocolcontrol.event.adapter.ProtocolMethodScanner;
import com.ichorpowered.protocolcontrol.event.adapter.ProtocolMethodSubscriptionAdapter;
import com.ichorpowered.protocolcontrol.packet.PacketDirection;
import com.ichorpowered.protocolcontrol.packet.PacketType;
import com.ichorpowered.protocolcontrol.util.Exceptions;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.kyori.event.EventSubscriber;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.asm.ASMEventExecutorFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

import static java.util.Objects.requireNonNull;

/**
 * An event manager for sending and listening to {@link PacketEvent}s.
 */
@Singleton
public final class ProtocolEvent {
  private final Logger logger;
  private SimpleEventBus<Object> bus;
  private MethodSubscriptionAdapter<Object> methodAdapter;
  private ExecutorService service;
  private boolean enabled = false;

  @Inject
  public ProtocolEvent(final Logger logger) {
    this.logger = logger;
  }

  protected void enable() {
    if(this.enabled) return;
    this.bus = new SimpleEventBus<Object>(Object.class) {
      @Override
      protected boolean shouldPost(final @NonNull Object event, final @NonNull EventSubscriber subscriber) {
        if(event instanceof PacketEvent && subscriber instanceof ProtocolEventSubscriber) {
          final PacketEvent packetEvent = (PacketEvent) event;
          final ProtocolEventSubscriber<?> protocolSubscriber = (ProtocolEventSubscriber<?>) subscriber;
          if(protocolSubscriber.packetType() != PacketType.UNSPECIFIED) {
            if(!Objects.equals(protocolSubscriber.packetType().type(), packetEvent.type())) return false;
          }
          if(protocolSubscriber.packetDirection() != PacketDirection.UNSPECIFIED) {
            if(protocolSubscriber.packetDirection() != packetEvent.direction()) return false;
          }
        }
        return super.shouldPost(event, subscriber);
      }
    };
    this.methodAdapter = new ProtocolMethodSubscriptionAdapter(this.bus, new ASMEventExecutorFactory<>(), new ProtocolMethodScanner());
    this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder()
      .setNameFormat("ProtocolControl Network Executor - #%d")
      .setDaemon(true)
      .build());
    this.enabled = true;
  }

  protected void disable() {
    if(!this.enabled) return;
    this.bus.unregisterAll();
    this.service.shutdownNow();
    this.enabled = false;
  }

  /**
   * Returns whether {@link ProtocolEvent} is enabled.
   *
   * @return whether events are enabled
   */
  public boolean enabled() {
    return this.enabled;
  }

  /**
   * Returns {@code true} if there are any {@link PacketEvent}s
   * being subscribed to.
   *
   * @return whether there are any subscribers
   */
  public boolean hasSubscribers() {
    if(!this.enabled) return false;
    return this.bus.hasSubscribers(PacketEvent.class);
  }

  /**
   * Registers the specified packet listener.
   *
   * @param listener the packet listener
   */
  public void register(final @NonNull Object listener) {
    this.methodAdapter.register(requireNonNull(listener, "listener"));
  }

  /**
   * Registers the specified {@link EventHandler} to listen for
   * packets.
   *
   * @param event the event class type
   * @param eventOrder the event order
   * @param packetType the packet type
   * @param packetDirection the packet direction
   * @param ignoreCancelled whether to ignore cancelled
   * @param handler the handler
   * @param <E> the event type
   */
  public <E> void register(final @NonNull Class<E> event,
                           final @NonNull EventOrder eventOrder,
                           final @NonNull PacketType packetType,
                           final @NonNull PacketDirection packetDirection,
                           final boolean ignoreCancelled,
                           final @NonNull EventHandler<E> handler) {
    this.bus.register(event, new KyoriToProtocolHandler<>(
      requireNonNull(handler, "handler"),
      requireNonNull(packetType, "packetType"),
      requireNonNull(packetDirection, "packetDirection"),
      requireNonNull(eventOrder, "eventOrder"),
      ignoreCancelled
    ));
  }

  /**
   * Unregisters the specified packet listener.
   *
   * @param listener the packet listener
   */
  public void unregister(final @NonNull Object listener) {
    this.methodAdapter.unregister(requireNonNull(listener, "listener"));
  }

  /**
   * Fires the specified {@link PacketEvent} and returns a
   * {@link CompletableFuture}.
   *
   * @param event the packet event
   * @return a completable future
   */
  public @NonNull CompletableFuture<PacketEvent> fire(final @NonNull PacketEvent event) {
    requireNonNull(event, "event");
    if(!this.enabled) return CompletableFuture.completedFuture(event);
    final CompletableFuture<PacketEvent> eventFuture = new CompletableFuture<>();
    this.service.execute(() -> {
      this.postEvent(event);
      eventFuture.complete(event);
    });
    return eventFuture;
  }

  /**
   * Fires the specified {@link PacketEvent} and ignores the
   * result.
   *
   * @param event the packet event
   */
  public void fireAndForget(final @NonNull PacketEvent event) {
    requireNonNull(event, "event");
    if(!this.enabled) return;
    this.service.execute(() -> this.postEvent(event));
  }

  private void postEvent(final @NonNull PacketEvent event) {
    final PostResult result = this.bus.post(event);
    final Collection<Throwable> exceptions = result.exceptions().values();
    for(final Throwable throwable : exceptions) {
      Exceptions.catchingReport(
        throwable,
        this.logger,
        PacketEvent.class,
        "event",
        "Encountered a minor exception attempting to post a packet event"
      );
    }
  }

  private static class KyoriToProtocolHandler<E> extends ProtocolEventSubscriber<E> {
    private final EventHandler<E> handler;

    public KyoriToProtocolHandler(final @NonNull EventHandler<E> handler,
                                  final @NonNull PacketType packetType,
                                  final @NonNull PacketDirection packetDirection,
                                  final @NonNull EventOrder eventOrder,
                                  final boolean includeCancelled) {
      super(packetType, packetDirection, eventOrder.ordinal(), includeCancelled);
      this.handler = handler;
    }

    @Override
    public void invoke(final @NonNull E event) throws Throwable {
      this.handler.execute(event);
    }
  }
}
