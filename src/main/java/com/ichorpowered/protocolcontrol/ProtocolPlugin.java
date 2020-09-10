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
import com.google.inject.Injector;
import com.ichorpowered.protocolcontrol.service.ProtocolService;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(
  id = "protocolcontrol",
  name = "ProtocolControl",
  description = "A minimal protocol manipulation library for Sponge.",
  authors = {
    "Vectrix"
  }
)
public final class ProtocolPlugin {
  private final Injector injector;
  private final PluginContainer plugin;
  private final Logger logger;
  private ProtocolChannel protocolChannel;
  private ProtocolEvent protocolEvent;
  private ProtocolInjector protocolInjector;

  @Inject
  public ProtocolPlugin(final Injector injector,
                        final PluginContainer plugin,
                        final Logger logger) {
    this.injector = injector;
    this.plugin = plugin;
    this.logger = logger;
  }

  @Listener(order = Order.FIRST)
  public void onGameInitialization(final GameInitializationEvent event) {
    final Injector childInjector = this.injector.createChildInjector(new ProtocolModule());
    this.protocolChannel = childInjector.getInstance(ProtocolChannel.class);
    this.protocolEvent = childInjector.getInstance(ProtocolEvent.class);
    this.protocolInjector = childInjector.getInstance(ProtocolInjector.class);
    this.protocolInjector.setup();
    this.protocolChannel.enable();
    this.protocolEvent.enable();
    final ProtocolService protocolService = childInjector.getInstance(ProtocolService.class);
    Sponge.getServiceManager().setProvider(this, ProtocolService.class, protocolService);
  }

  @Listener(order = Order.FIRST)
  public void onGameStarting(final GamePostInitializationEvent event) {
    this.protocolInjector.enable();
    this.logger.info("Successfully injected " + this.plugin.getName() + " version " + this.plugin.getVersion().orElse("UNKNOWN"));
  }

  @Listener(order = Order.LAST)
  public void onGameStopped(final GameStoppingServerEvent event) {
    if(this.protocolInjector != null && this.protocolInjector.enabled()) this.protocolInjector.disable();
    if(this.protocolEvent != null && this.protocolEvent.enabled()) this.protocolEvent.disable();
    if(this.protocolChannel != null && this.protocolChannel.enabled()) this.protocolChannel.disable();
    this.logger.info("Stopped " + this.plugin.getName());
  }
}
