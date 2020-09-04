Protocol Control
================

**Under development and not stable!**

A minimal packet manipulation library for Sponge.

## Prerequisites
* [Java] 8

## Building
__Note:__ If you do not have [Gradle] installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build ProtocolControl you simply need to run the `gradle` command. You can find the compiled JAR file in `./build/libs` labeled similarly to 'protocolcontrol-x.x.x-SNAPSHOT.jar'.

## Dependency
Using `ProtocolControl` in your plugin requires you to use ForgeGradle and add `ProtocolControl` as a compile-time dependency.

```gradle
repositories {
  maven { url "https://repo.ichorpowered.com/repository/maven-public" }
}

dependencies {
  compile "com.ichorpowered:protocolcontrol:0.0.2-SNAPSHOT"
}
```

## Usage
To get started, you will need to get the instance of the `ProtocolService` from the `ServiceManager`.

```java
Optional<ProtocolService> protocolService = Sponge.getServiceManager().provide(ProtocolService.class);
```

### Creating a Listener
To create a packet listener, you need to add the `@Subscribe` annotation on your listener methods, with a parameter for `PacketEvent` with a generic argument of the `Packet<?>` type you are listening for.

```java
@Subscribe
public void onLoginEvent(PacketEvent<SPacketLoginSuccess> event) {
  ...
}
```

You can register the listener by passing in the listener object to `ProtocolEvent#register`. You can also unregister the listener with `ProtocolEvent#unregister`.

The `ProtocolEvent` is acquired from `ProtocolService#events`.

### Manipulating a Packet
The event has a method to get the `ChannelProfile` the packet is going to or coming from and the `PacketDirection`.

You have access to the original instance of the packet as well as a way to override the packet.

```java
@Subscribe
public void onLoginEvent(PacketEvent<SPacketLoginSuccess> event) {
  SPacketLoginSuccess packet = event.packet(); // Gets the packet instance.
  event.packet(...); // Override the packet instance.
}
```

You can also prevent a packet from reaching its destination by cancelling the event.

```java
@Subscribe
public void onLoginEvent(PacketEvent<SPacketLoginSuccess> event) {
  if (!event.cancel()) event.cancel(true); // If the packet is not already cancelled, cancel it.
}
```

### Wrapping a Packet
`PacketRemapper` provides an optional wrapper for your packet, which provides methods to `get` and `set` the fields for a packet. This is particularly useful but not limited to packets that do not provide convenient getters or setters. You just need to know the field type and the index for the field of that type in order to use them.

```java
@Subscribe
public void onLoginEvent(PacketEvent<SPacketLoginSuccess> event) {
  PacketRemapper.Wrapper<SPacketLoginSuccess> wrapper = this.remapper.wrap(event.packet());
  GameProfile profile = wrapper.<GameProfile>get(GameProfile.class, 0); // Gets the game profile.
  wrapper.<GameProfile>set(GameProfile.class, 0, ...); // Sets the game profile.
}
```

The `PacketRemapper` is acquired from `ProtocolService#remapper`.

### Sending a Packet
You can send a new packet to the client or to the server using the `ChannelProfile#send` method. 

```java
@Listener
public void onPlayerMove(final MoveEntityEvent event, final @First Player player) {
  final Location<World> location = player.getLocation();
  final ChannelProfile profile = this.protocolChannel.profile(player.getUniqueId());
    
  try {
    final PacketRemapper.Wrapped<SPacketBlockChange> blockChange = this.remapper.wrap(new SPacketBlockChange());
    blockChange.set(BlockPos.class, 0, new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    blockChange.set(IBlockState.class, 0, (IBlockState) BlockTypes.WATER.getDefaultState());

    profile.send(PacketDirection.OUTGOING, blockChange.packet()); // Sends the block change packet to the client.
  } catch(Throwable throwable) {
    throwable.printStackTrace();
  }
}
```

The `ProtocolChannel` is acquired from `ProtocolService#channels`. You can then grab the players `ChannelProfile` using `ProtocolChannel#profile`.

[Gradle]: https://www.gradle.org/
[Java]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
