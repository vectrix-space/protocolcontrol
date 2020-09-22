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

import com.ichorpowered.protocolcontrol.packet.PacketDirection;
import com.ichorpowered.protocolcontrol.packet.PacketType;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class ProtocolEventSubscriber<E> implements EventSubscriber<E> {
  private final PacketType packetType;
  private final PacketDirection packetDirection;
  private final int postOrder;
  private final boolean includeCancelled;

  public ProtocolEventSubscriber(final @NonNull PacketType packetType,
                                 final @NonNull PacketDirection packetDirection,
                                 final int postOrder,
                                 final boolean includeCancelled) {
    this.packetType = packetType;
    this.packetDirection = packetDirection;
    this.postOrder = postOrder;
    this.includeCancelled = includeCancelled;
  }

  public PacketType packetType() {
    return this.packetType;
  }

  public PacketDirection packetDirection() {
    return this.packetDirection;
  }

  @Override
  public int postOrder() {
    return this.postOrder;
  }

  @Override
  public boolean consumeCancelledEvents() {
    return this.includeCancelled;
  }
}
