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
package com.ichorpowered.protocolcontrol.channel;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.ichorpowered.protocolcontrol.ProtocolInjector;
import com.ichorpowered.protocolcontrol.packet.PacketDirection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.Packet;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public final class ChannelProfile {
  private final Channel channel;
  private @Nullable UUID id;
  private WeakReference<Player> player;
  private boolean active = false;

  @Inject
  public ChannelProfile(final @Assisted Channel channel) {
    this.channel = channel;
  }

  /**
   * Returns the {@link Channel} this profile belongs
   * to.
   *
   * @return the channel
   */
  public @NonNull Channel channel() {
    return this.channel;
  }

  /**
   * Returns the player identifier this channel belongs to.
   *
   * @return the player identifier
   */
  public @Nullable UUID id() {
    return this.id;
  }

  /**
   * Sets the player identifier this channel belongs to.
   *
   * @param player the player identifier
   */
  public void id(final @Nullable UUID player) {
    this.id = player;
  }

  /**
   * Returns the player this channel belongs to, if it exists.
   *
   * @return the player, if present
   */
  public @NonNull Optional<Player> player() {
    if(this.id == null) return Optional.empty();
    if(this.player != null) return Optional.ofNullable(this.player.get());
    return this.player(this.id);
  }

  /**
   * Returns {@code true} of this channel profile
   * is active, otherwise returns {@code false}.
   *
   * @return the channel active state
   */
  public boolean active() {
    return this.active;
  }

  /**
   * Sets whether this channel profile is active.
   *
   * @param active the channel active state
   */
  public void active(final boolean active) {
    this.active = active;
  }

  /**
   * Sends the specified {@code T} packet in the specified
   * {@link PacketDirection} on the channel event loop.
   *
   * @param direction the packet direction
   * @param packet the packet
   * @param <T> the packet type
   */
  public <T extends Packet<?>> void send(final @NonNull PacketDirection direction, final @NonNull T packet) {
    this.send(direction, packet, false);
  }

  /**
   * Sends the specified {@code T} packet in the specified
   * {@link PacketDirection} on the specified thread.
   *
   * @param direction the packet direction
   * @param packet the packet
   * @param currentThread whether to send on the current thread or
   *                      post to the event loop
   * @param <T> the packet type
   */
  public <T extends Packet<?>> void send(final @NonNull PacketDirection direction, final @NonNull T packet,
                                         final boolean currentThread) {
    if(direction == PacketDirection.INCOMING) {
      final ChannelHandlerContext context = this.channel.pipeline().context(ProtocolInjector.INCOMING_HANDLER);
      if(currentThread) {
        this.read(context, packet);
      } else {
        this.channel.eventLoop().execute(() -> this.read(context, packet));
      }
    } else {
      final ChannelHandlerContext context = this.channel.pipeline().context(ProtocolInjector.OUTGOING_HANDLER);
      if(currentThread) {
        context.write(packet, this.channel.voidPromise());
      } else {
        this.channel.eventLoop().execute(() -> context.write(packet, this.channel.voidPromise()));
      }
    }
  }

  private void read(final @Nullable ChannelHandlerContext context, final @NonNull Object message) {
    if(context != null) {
      context.fireChannelRead(message);
    } else {
      this.channel.pipeline().fireChannelRead(message);
    }
  }

  private @NonNull Optional<Player> player(final @NonNull UUID id) {
    if (!Sponge.isServerAvailable()) return Optional.empty();
    final Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(id);
    optionalPlayer.ifPresent(value -> this.player = new WeakReference<>(value));
    return optionalPlayer;
  }

  public interface Factory {
    ChannelProfile create(@Assisted Channel channel);
  }
}
