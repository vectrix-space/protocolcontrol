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
package com.ichorpowered.protocolcontrol.service;

import com.ichorpowered.protocolcontrol.ProtocolChannel;
import com.ichorpowered.protocolcontrol.ProtocolEvent;
import com.ichorpowered.protocolcontrol.packet.PacketRemapper;

public interface ProtocolService {
  /**
   * Returns the {@link ProtocolChannel} providing methods to
   * access channel profiles.
   *
   * @return the protocol channels
   */
  ProtocolChannel channels();

  /**
   * Returns the {@link ProtocolEvent} providing methods to
   * listen for packets.
   *
   * @return the protocol events
   */
  ProtocolEvent events();

  /**
   * Returns the {@link PacketRemapper} providing methods to
   * optionally remap packets from events.
   *
   * @return the packet remapper
   */
  PacketRemapper remapper();
}
