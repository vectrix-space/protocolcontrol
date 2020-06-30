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
  maven { url "https://maven.pkg.github.com/ichorpowered/protocolcontrol" }
}

dependencies {
  compile "com.ichorpowered:protocolcontrol:0.0.1-SNAPSHOT"
}
```

[Gradle]: https://www.gradle.org/
[Java]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
