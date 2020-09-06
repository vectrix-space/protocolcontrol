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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.channel.ChannelInitializer;
import com.ichorpowered.protocolcontrol.util.Exceptions;
import io.netty.channel.ChannelFuture;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.spongepowered.api.Game;

@Singleton
public final class ProtocolInjector {
  public static final String INCOMING_HANDLER = "protocolcontrol_incoming";
  public static final String OUTGOING_HANDLER = "protocolcontrol_outgoing";
  private final Game game;
  private final Logger logger;
  private final ChannelInitializer initializer;
  private List<ChannelFuture> endpoints;
  private boolean setup = false;
  private boolean enabled = false;

  @Inject
  public ProtocolInjector(final Game game,
                          final Logger logger,
                          final ChannelInitializer initializer) {
    this.game = game;
    this.logger = logger;
    this.initializer = initializer;
  }

  @SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
  protected void setup() {
    if(this.setup) return;

    Exceptions.catchingReport(
      () -> {
        final Field networkField = MinecraftServer.class.getDeclaredField("field_147144_o");
        final NetworkSystem networkSystem = (NetworkSystem) networkField.get(this.game.getServer());
        final Field endpointsField = networkSystem.getClass().getDeclaredField("field_151274_e");

        this.endpoints = (List<ChannelFuture>) endpointsField.get(networkSystem);
        this.setup = true;
      },
      this.logger,
      ProtocolInjector.class,
      "injector",
      "Encountered a major exception attempting to setup channel injector"
    );
  }

  protected void enable() {
    if(this.enabled || !this.setup) return;

    Exceptions.catchingReport(
      () -> {
        for(final ChannelFuture future : this.endpoints) {
          future.channel().pipeline().addFirst(this.initializer);
        }

        this.enabled = true;
      },
      this.logger,
      ProtocolInjector.class,
      "injector",
      "Encountered a major exception attempting to enable channel injector"
    );
  }

  protected void disable() {
    if(!this.enabled) return;

    Exceptions.catchingReport(
      () -> {
        for(final ChannelFuture future : this.endpoints) {
          future.channel().pipeline().remove(this.initializer);
        }

        this.enabled = false;
      },
      this.logger,
      ProtocolInjector.class,
      "injector",
      "Encountered a major exception attempting to disable channel injector"
    );
  }

  public boolean enabled() {
    return this.enabled;
  }
}
