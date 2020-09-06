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
import com.google.inject.Singleton;
import com.ichorpowered.protocolcontrol.ProtocolChannel;
import com.ichorpowered.protocolcontrol.ProtocolEvent;
import com.ichorpowered.protocolcontrol.packet.PacketHandler;
import com.ichorpowered.protocolcontrol.packet.PacketRemapper;
import com.ichorpowered.protocolcontrol.util.Exceptions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;

@Singleton
public final class ChannelInitializer extends ChannelInboundHandlerAdapter {
  private final Logger logger;
  private final ProtocolChannel channels;
  private final ProtocolEvent events;
  private final PacketRemapper remapper;
  private final ChannelProfile.Factory profileFactory;

  @Inject
  public ChannelInitializer(final Logger logger,
                            final ProtocolChannel channels,
                            final ProtocolEvent events,
                            final PacketRemapper remapper,
                            final ChannelProfile.Factory profileFactory) {
    this.logger = logger;
    this.channels = channels;
    this.events = events;
    this.remapper = remapper;
    this.profileFactory = profileFactory;
  }

  @Override
  public void channelRead(final ChannelHandlerContext context, final Object message) throws Exception {
    Exceptions.catchingReport(
      () -> {
        final Channel channel = (Channel) message;
        final ChannelProfile profile = this.profileFactory.create(channel);
        final PacketHandler handler = new PacketHandler(this.logger, this.channels, this.events, this.remapper, profile);

        channel.pipeline().addLast(handler);
      },
      this.logger,
      ChannelInitializer.class,
      "channel",
      "Encountered a major exception attempting to initialize a channel",
      report -> report.category("channel_read")
        .detail("context", context.name())
    );

    super.channelRead(context, message);
  }
}
