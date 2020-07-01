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

import com.google.common.collect.MapMaker;
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.channel.ChannelProfile;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public final class ProtocolChannel {
  private final ConcurrentMap<UUID, ChannelProfile> channels = new MapMaker().initialCapacity(1).weakValues().makeMap();
  private boolean enabled = false;

  public void enable() {
    if(this.enabled) return;
    this.enabled = true;
  }

  public void disable() {
    if(!this.enabled) return;
    this.enabled = false;

    this.channels.clear();
  }

  public boolean enabled() {
    return this.enabled;
  }

  /**
   * Sets the specified player {@link ChannelProfile}.
   *
   * @param player the player
   * @param profile the channel profile
   */
  public void set(final UUID player, final ChannelProfile profile) {
    this.channels.putIfAbsent(player, profile);
  }

  /**
   * Removes the specified player {@link ChannelProfile}.
   *
   * @param player the player
   */
  public void clear(final UUID player) {
    this.channels.remove(player);
  }

  /**
   * Returns the specified player {@link ChannelProfile}
   * if it exists.
   *
   * @param player the player
   * @return the channel profile, if present
   */
  @Nullable
  public ChannelProfile profile(final UUID player) {
    return this.channels.get(player);
  }
}
