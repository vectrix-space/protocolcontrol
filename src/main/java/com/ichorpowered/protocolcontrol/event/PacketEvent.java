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
package com.ichorpowered.protocolcontrol.event;

import com.google.common.reflect.TypeToken;
import com.ichorpowered.protocolcontrol.channel.ChannelProfile;
import com.ichorpowered.protocolcontrol.packet.PacketDirection;
import net.kyori.event.Cancellable;
import org.checkerframework.checker.nullness.qual.NonNull;

import static java.util.Objects.requireNonNull;

/**
 * An event containing the packet, the {@link PacketDirection}
 * and the {@link ChannelProfile} which it applies to.
 */
@SuppressWarnings("UnstableApiUsage")
public final class PacketEvent extends Cancellable.Impl {
  private final ChannelProfile profile;
  private final PacketDirection direction;
  private Object packet;

  public PacketEvent(final @NonNull ChannelProfile profile,
                     final @NonNull PacketDirection direction,
                     final @NonNull Object packet) {
    this.profile = requireNonNull(profile, "profile");
    this.direction = requireNonNull(direction, "direction");
    this.packet = requireNonNull(packet, "packet");
  }

  /**
   * Returns the {@link ChannelProfile} the packet applies to.
   *
   * @return the channel profile
   */
  public @NonNull ChannelProfile profile() {
    return this.profile;
  }

  /**
   * Returns the {@link PacketDirection} the packet applies to.
   *
   * @return the packet direction
   */
  public @NonNull PacketDirection direction() {
    return this.direction;
  }

  /**
   * Returns the packet {@link TypeToken}.
   *
   * @return the packet type token
   */
  public @NonNull TypeToken<?> type() {
    return TypeToken.of(this.packet.getClass());
  }

  /**
   * Returns the packet object.
   *
   * @return the packet object
   */
  public @NonNull Object packet() {
    return this.packet;
  }

  /**
   * Sets the packet object.
   *
   * @param packet the packet object
   */
  public void packet(final @NonNull Object packet) {
    this.packet = requireNonNull(packet, "packet");
  }
}
