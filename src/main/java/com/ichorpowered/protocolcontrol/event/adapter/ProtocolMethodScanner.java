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

import com.ichorpowered.protocolcontrol.event.annotation.Subscribe;
import com.ichorpowered.protocolcontrol.packet.PacketDirection;
import com.ichorpowered.protocolcontrol.packet.PacketType;
import java.lang.reflect.Method;
import net.kyori.event.method.MethodScanner;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ProtocolMethodScanner implements MethodScanner<Object> {
  @Override
  public boolean shouldRegister(final @NonNull Object listener, final @NonNull Method method) {
    return method.isAnnotationPresent(Subscribe.class);
  }

  @Override
  public int postOrder(final @NonNull Object listener, final @NonNull Method method) {
    return method.getAnnotation(Subscribe.class).order().ordinal();
  }

  @Override
  public boolean consumeCancelledEvents(final @NonNull Object listener, final @NonNull Method method) {
    return method.getAnnotation(Subscribe.class).ignoreCancelled();
  }

  public PacketType packetType(final @NonNull Method method) {
    return method.getAnnotation(Subscribe.class).type();
  }

  public PacketDirection packetDirection(final @NonNull Method method) {
    return method.getAnnotation(Subscribe.class).direction();
  }
}
