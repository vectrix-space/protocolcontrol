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
package com.ichorpowered.protocolcontrol.event.annotation;

import com.ichorpowered.protocolcontrol.event.EventOrder;
import com.ichorpowered.protocolcontrol.packet.PacketDirection;
import com.ichorpowered.protocolcontrol.packet.PacketType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that indicates that marks a method as a listener
 * for protocol events.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
  /**
   * The {@link EventOrder} of events that will be posted to
   * this listener.
   *
   * @return the order
   */
  EventOrder order() default EventOrder.NORMAL;

  /**
   * The {@link PacketType} events must have in order to
   * be posted to this listener.
   *
   * @return the packet type
   */
  PacketType type() default PacketType.UNSPECIFIED;

  /**
   * The {@link PacketDirection} events must have in order to
   * be posted to this listener.
   *
   * @return the packet direction
   */
  PacketDirection direction() default PacketDirection.UNSPECIFIED;

  /**
   * Whether to ignore cancelled events posted to this listener.
   *
   * @return whether to ignore cancelled events
   */
  boolean ignoreCancelled() default false;
}
