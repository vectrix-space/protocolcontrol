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
import com.ichorpowered.protocolcontrol.event.PacketEvent;
import com.ichorpowered.protocolcontrol.util.Exceptions;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import net.kyori.event.method.asm.ASMEventExecutorFactory;
import net.minecraft.network.Packet;
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

    this.service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder()
      .setNameFormat("ProtocolControl Network Executor - #%d")
      .setDaemon(true)
      .build());

    this.bus = new SimpleEventBus<>(Object.class);
    this.methodAdapter = new SimpleMethodSubscriptionAdapter<>(this.bus, new ASMEventExecutorFactory<>());
    this.enabled = true;
  }

  protected void disable() {
    if(!this.enabled) return;

    this.bus.unregisterAll();
    this.service.shutdownNow();
    this.enabled = false;
  }

  public boolean enabled() {
    return this.enabled;
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
   * @param <T> the packet type
   * @return a completable future
   */
  public <T extends Packet<?>> @NonNull CompletableFuture<PacketEvent<T>> fire(final @NonNull PacketEvent<T> event) {
    requireNonNull(event, "event");
    if(!this.enabled) return CompletableFuture.completedFuture(event);
    if(!this.bus.hasSubscribers(event.getClass())) return CompletableFuture.completedFuture(event);

    final CompletableFuture<PacketEvent<T>> eventFuture = new CompletableFuture<>();
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
   * @param <T> the packet type
   */
  public <T extends Packet<?>> void fireAndForget(final @NonNull PacketEvent<T> event) {
    requireNonNull(event, "event");
    if(!this.enabled) return;
    if(!this.bus.hasSubscribers(event.getClass())) return;
    this.service.execute(() -> this.postEvent(event));
  }

  private <T extends Packet<?>> void postEvent(final @NonNull PacketEvent<T> event) {
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
}
