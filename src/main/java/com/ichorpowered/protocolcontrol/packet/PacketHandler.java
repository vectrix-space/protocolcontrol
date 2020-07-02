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
package com.ichorpowered.protocolcontrol.packet;

import com.ichorpowered.protocolcontrol.ProtocolChannel;
import com.ichorpowered.protocolcontrol.ProtocolInjector;
import com.ichorpowered.protocolcontrol.channel.ChannelProfile;
import com.ichorpowered.protocolcontrol.ProtocolEvent;
import com.ichorpowered.protocolcontrol.event.PacketEvent;
import com.ichorpowered.protocolcontrol.util.Exceptions;
import com.mojang.authlib.GameProfile;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

import static java.util.Objects.requireNonNull;

@ChannelHandler.Sharable
public final class PacketHandler extends ChannelDuplexHandler {
  private final Logger logger;
  private final ProtocolChannel channels;
  private final ProtocolEvent events;
  private final PacketRemapper remapper;
  private final ChannelProfile profile;
  private boolean injected = false;

  public PacketHandler(final @NonNull Logger logger,
                       final @NonNull ProtocolChannel channels,
                       final @NonNull ProtocolEvent events,
                       final @NonNull PacketRemapper remapper,
                       final @NonNull ChannelProfile profile) {
    this.logger = requireNonNull(logger, "logger");
    this.channels = requireNonNull(channels, "channels");
    this.events = requireNonNull(events, "events");
    this.remapper = requireNonNull(remapper, "remapper");
    this.profile = requireNonNull(profile, "profile");
  }

  public @NonNull Logger logger() {
    return this.logger;
  }

  public @NonNull ProtocolEvent event() {
    return this.events;
  }

  public @NonNull ChannelProfile profile() {
    return this.profile;
  }

  @Override
  public void channelActive(final ChannelHandlerContext context) throws Exception {
    try {
      if(!this.injected) {
        final Incoming incoming = new Incoming(this);
        final Outgoing outgoing = new Outgoing(this);

        context.pipeline().remove(this).addBefore("packet_handler", "protocolcontrol_listener", this);
        context.pipeline().addAfter("decoder", ProtocolInjector.INCOMING_HANDLER, incoming);
        context.pipeline().addAfter("packet_handler", ProtocolInjector.OUTGOING_HANDLER, outgoing);

        this.remapper.structure(SPacketLoginSuccess.class); // Prepare the structure early.

        this.profile.active(true);

        this.injected = true;
      }
    } catch(Throwable throwable) {
      Exceptions.catchingReport(
        throwable,
        this.logger,
        PacketHandler.class,
        "packet",
        "Encountered a major exception attempting to handle channel active"
      );
    }

    super.channelActive(context);
  }

  @Override
  public void write(final ChannelHandlerContext context, final Object message, final ChannelPromise promise) throws Exception {
    try {
      if(message instanceof SPacketLoginSuccess) {
        final PacketRemapper.Wrapped<SPacketLoginSuccess> wrapped = this.remapper.wrap((SPacketLoginSuccess) message);
        final GameProfile profile = wrapped.get(GameProfile.class, 0);

        if(profile != null) {
          this.profile.player(profile.getId());
          this.channels.add(this.profile);
        } else {
          this.logger.warn("Failed to acquire player on login for a connected channel.");
        }
      }
    } catch(Throwable throwable) {
      Exceptions.catchingReport(
        throwable,
        this.logger,
        PacketHandler.class,
        "packet",
        "Encountered a major exception attempting to handle channel write"
      );
    }

    super.write(context, message, promise);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext context) throws Exception {
    if(this.injected) {
      final UUID player = this.profile.player();
      if(player != null) this.channels.clear(player);

      this.profile.active(false);
    }

    super.channelInactive(context);
  }

  protected static class Incoming extends ChannelInboundHandlerAdapter {
    private final PacketHandler handler;

    public Incoming(final @NonNull PacketHandler handler) {
      this.handler = handler;
    }

    @Override
    public void channelRead(final ChannelHandlerContext context, Object message) throws Exception {
      try {
        if(message instanceof Packet) {
          final PacketEvent<?> packetEvent = new PacketEvent<>(this.handler.profile(), PacketDirection.INCOMING, (Packet<?>) message);
          this.handler.event().fire(packetEvent).get();

          if(packetEvent.cancel()) return;
          message = packetEvent.packet();
        }
      } catch(Throwable throwable) {
        Exceptions.catchingReport(
          throwable,
          this.handler.logger(),
          PacketHandler.class,
          "packet",
          "Encountered a minor exception attempting to handle an incoming packet"
        );
      }

      super.channelRead(context, message);
    }
  }

  protected static class Outgoing extends ChannelOutboundHandlerAdapter {
    private final PacketHandler handler;

    public Outgoing(final @NonNull PacketHandler handler) {
      this.handler = handler;
    }

    @Override
    public void write(final ChannelHandlerContext context, Object message, final ChannelPromise promise) throws Exception {
      try {
        if(message instanceof Packet) {
          final PacketEvent<?> packetEvent = new PacketEvent<>(this.handler.profile(), PacketDirection.OUTGOING, (Packet<?>) message);
          this.handler.event().fire(packetEvent).get();

          if(packetEvent.cancel()) return;
          message = packetEvent.packet();
        }
      } catch(Throwable throwable) {
        Exceptions.catchingReport(
          throwable,
          this.handler.logger(),
          PacketHandler.class,
          "packet",
          "Encountered a minor exception attempting to handle an outgoing packet"
        );
      }

      super.write(context, message, promise);
    }
  }
}
