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
import com.ichorpowered.protocolcontrol.ProtocolEvent;
import com.ichorpowered.protocolcontrol.ProtocolInjector;
import com.ichorpowered.protocolcontrol.channel.ChannelProfile;
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
    Exceptions.catchingReport(
      () -> {
        if(!this.injected) {
          final Incoming incoming = new Incoming(this);
          final Outgoing outgoing = new Outgoing(this);
          context.pipeline().remove(this).addBefore("packet_handler", ProtocolInjector.CHANNEL_HANDLER, this);
          context.pipeline().addAfter("decoder", ProtocolInjector.INCOMING_HANDLER, incoming);
          context.pipeline().addAfter("packet_handler", ProtocolInjector.OUTGOING_HANDLER, outgoing);
          this.remapper.structure(SPacketLoginSuccess.class); // Prepare the structure early.
          this.profile.active(true);
          this.injected = true;
        }
      },
      this.logger,
      PacketHandler.class,
      "channel",
      "Encountered a major exception attempting to handle channel active",
      report -> report.category("channel_active")
        .detail("profile", this.profile)
        .detail("context", context.name())
    );
    super.channelActive(context);
  }

  @Override
  public void write(final ChannelHandlerContext context, final Object message, final ChannelPromise promise) throws Exception {
    Exceptions.catchingReport(
      () -> {
        if(message instanceof SPacketLoginSuccess) {
          final PacketRemapper.Wrapped wrapped = this.remapper.wrap(message);
          final GameProfile profile = wrapped.get(GameProfile.class, 0);
          if(profile != null) {
            this.profile.id(profile.getId());
            this.channels.add(this.profile);
          } else {
            this.logger.warn("Failed to acquire player on login for a connected channel.");
          }
        }
      },
      this.logger,
      PacketHandler.class,
      "packet",
      "Encountered a major exception attempting to handle channel write",
      report -> report.category("packet_write")
        .detail("profile", this.profile)
        .detail("player", this.profile.player())
        .detail("context", context.name())
        .detail("message", message)
    );
    super.write(context, message, promise);
  }

  @Override
  public void channelInactive(final ChannelHandlerContext context) throws Exception {
    Exceptions.catchingReport(
      () -> {
        if(this.injected) {
          final UUID player = this.profile.id();
          if(player != null) this.channels.remove(player);
          this.profile.active(false);
        }
      },
      this.logger,
      PacketHandler.class,
      "channel",
      "Encountered a major exception attempting to handle channel inactive",
      report -> report.category("channel_inactive")
        .detail("profile", this.profile)
        .detail("context", context.name())
    );
    super.channelInactive(context);
  }

  protected static class Incoming extends ChannelInboundHandlerAdapter {
    private final Logger logger;
    private final ProtocolEvent event;
    private final ChannelProfile profile;

    public Incoming(final @NonNull PacketHandler handler) {
      this.logger = handler.logger();
      this.event = handler.event();
      this.profile = handler.profile();
    }

    @Override
    public void channelRead(final ChannelHandlerContext context, final Object message) throws Exception {
      Object transformedMessage = message;
      try {
        if(transformedMessage instanceof Packet && this.event.hasSubscribers()) {
          final PacketEvent packetEvent = new PacketEvent(this.profile, PacketDirection.INCOMING, transformedMessage);
          this.event.fire(packetEvent).get();
          if(packetEvent.cancelled()) return;
          transformedMessage = packetEvent.packet();
        }
      } catch(Throwable throwable) {
        Exceptions.catchingReport(
          throwable,
          this.logger,
          PacketHandler.class,
          "packet",
          "Encountered a minor exception attempting to handle an incoming packet",
          report -> report.category("packet_read")
            .detail("profile", this.profile)
            .detail("player", this.profile.player())
            .detail("context", context.name())
            .detail("message", message)
        );
      }
      super.channelRead(context, transformedMessage);
    }
  }

  protected static class Outgoing extends ChannelOutboundHandlerAdapter {
    private final Logger logger;
    private final ProtocolEvent event;
    private final ChannelProfile profile;

    public Outgoing(final @NonNull PacketHandler handler) {
      this.logger = handler.logger();
      this.event = handler.event();
      this.profile = handler.profile();
    }

    @Override
    public void write(final ChannelHandlerContext context, final Object message, final ChannelPromise promise) throws Exception {
      Object transformedMessage = message;
      try {
        if(transformedMessage instanceof Packet && this.event.hasSubscribers()) {
          final PacketEvent packetEvent = new PacketEvent(this.profile, PacketDirection.OUTGOING, transformedMessage);
          this.event.fire(packetEvent).get();
          if(packetEvent.cancelled()) return;
          transformedMessage = packetEvent.packet();
        }
      } catch(Throwable throwable) {
        Exceptions.catchingReport(
          throwable,
          this.logger,
          PacketHandler.class,
          "packet",
          "Encountered a minor exception attempting to handle an outgoing packet",
          report -> report.category("packet_write")
            .detail("profile", this.profile)
            .detail("player", this.profile.player())
            .detail("context", context.name())
            .detail("message", message)
        );
      }
      super.write(context, transformedMessage, promise);
    }
  }
}
