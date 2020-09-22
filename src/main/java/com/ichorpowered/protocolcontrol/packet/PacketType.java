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

import com.google.common.reflect.TypeToken;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents the {@link PacketDirection#INCOMING} and {@link PacketDirection#OUTGOING}
 * packet types in the game as of version 1.12.2.
 */
@SuppressWarnings("UnstableApiUsage")
public enum PacketType {
  ABILITIES(CPacketPlayerAbilities.class, SPacketPlayerAbilities.class),
  ADVANCEMENT_INFO(null, SPacketAdvancementInfo.class),
  ANIMATION(CPacketAnimation.class, SPacketAnimation.class),
  BLOCK_ACTION(null, SPacketBlockAction.class),
  BLOCK_BREAK_ANIMATION(null, SPacketBlockBreakAnim.class),
  BLOCK_CHANGE(null, SPacketBlockChange.class),
  CAMERA(null, SPacketCamera.class),
  CHANGE_GAME_STATE(null, SPacketChangeGameState.class),
  CHAT(CPacketChatMessage.class, SPacketChat.class),
  CHUNK_DATA(null, SPacketChunkData.class),
  CLICK_WINDOW(CPacketClickWindow.class, null),
  CLIENT_SETTINGS(CPacketClientSettings.class, null),
  CLIENT_STATUS(CPacketClientStatus.class, null),
  CLOSE_WINDOW(CPacketCloseWindow.class, SPacketCloseWindow.class),
  COLLECT_ITEM(null, SPacketCollectItem.class),
  COMBAT_EVENT(null, SPacketCombatEvent.class),
  CONFIRM_TELEPORT(CPacketConfirmTeleport.class, null),
  CONFIRM_TRANSACTION(CPacketConfirmTransaction.class, SPacketConfirmTransaction.class),
  COOLDOWN(null, SPacketCooldown.class),
  CREATIVE_INVENTORY_ACTION(CPacketCreativeInventoryAction.class, null),
  CUSTOM_PAYLOAD(CPacketCustomPayload.class, SPacketCustomPayload.class),
  CUSTOM_SOUND(null, SPacketCustomSound.class),
  DESTROY_ENTITIES(null, SPacketDestroyEntities.class),
  DISCONNECT(null, SPacketDisconnect.class),
  EFFECT(null, SPacketEffect.class),
  ENCHANT_ITEM(CPacketEnchantItem.class, null),
  ENTITY(null, SPacketEntity.class),
  ENTITY_ACTION(CPacketEntityAction.class, null),
  ENTITY_ATTACH(null, SPacketEntityAttach.class),
  ENTITY_EFFECT(null, SPacketEntityEffect.class),
  ENTITY_EQUIPMENT(null, SPacketEntityEquipment.class),
  ENTITY_HEAD_LOOK(null, SPacketEntityHeadLook.class),
  ENTITY_LOOK(null, SPacketEntity.S16PacketEntityLook.class),
  ENTITY_METADATA(null, SPacketEntityMetadata.class),
  ENTITY_MOVE_LOOK(null, SPacketEntity.S17PacketEntityLookMove.class),
  ENTITY_PROPERTIES(null, SPacketEntityProperties.class),
  ENTITY_STATUS(null, SPacketEntityStatus.class),
  ENTITY_TELEPORT(null, SPacketEntityTeleport.class),
  ENTITY_VELOCITY(null, SPacketEntityVelocity.class),
  EXPLOSION(null, SPacketExplosion.class),
  HELD_ITEM_CHANGE(CPacketHeldItemChange.class, SPacketHeldItemChange.class),
  INPUT(CPacketInput.class, null),
  JOIN_GAME(null, SPacketJoinGame.class),
  KEEP_ALIVE(CPacketKeepAlive.class, SPacketKeepAlive.class),
  LOGIN_START(CPacketLoginStart.class, null),
  LOGIN_SUCCESS(null, SPacketLoginSuccess.class),
  MAPS(null, SPacketMaps.class),
  MULTI_BLOCK_CHANGE(null, SPacketMultiBlockChange.class),
  OPEN_SIGN_EDITOR(null, SPacketSignEditorOpen.class),
  OPEN_WINDOW(null, SPacketOpenWindow.class),
  PARTICLES(null, SPacketParticles.class),
  PLACE_RECIPE(CPacketPlaceRecipe.class, null),
  PLACE_GHOST_RECIPE(null, SPacketPlaceGhostRecipe.class),
  PLAYER_DIGGING(CPacketPlayerDigging.class, null),
  PLAYER_LIST_HEADER_FOOTER(null, SPacketPlayerListHeaderFooter.class),
  PLAYER_LIST_ITEM(null, SPacketPlayerListItem.class),
  PLAYER_POSITION(CPacketPlayer.Position.class, null),
  PLAYER_POSITION_LOOK(null, SPacketPlayerPosLook.class),
  PLAYER_POSITION_ROTATION(CPacketPlayer.PositionRotation.class, null),
  PLAYER_ROTATION(CPacketPlayer.Rotation.class, null),
  RECIPE_BOOK(null, SPacketRecipeBook.class),
  RECIPE_INFO(CPacketRecipeInfo.class, null),
  REL_ENTITY_MOVE(null, SPacketEntity.S15PacketEntityRelMove.class),
  REMOVE_ENTITY_EFFECT(null, SPacketRemoveEntityEffect.class),
  RESOURCE_PACK_SEND(null, SPacketResourcePackSend.class),
  RESOURCE_PACK_STATUS(CPacketResourcePackStatus.class, null),
  RESPAWN(null, SPacketRespawn.class),
  SCOREBOARD_DISPLAY_OBJECTIVE(null, SPacketDisplayObjective.class),
  SCOREBOARD_OBJECTIVE(null, SPacketScoreboardObjective.class),
  SCOREBOARD_SCORE(null, SPacketUpdateScore.class),
  SEEN_ADVANCEMENTS(CPacketSeenAdvancements.class, null),
  SELECT_ADVANCEMENTS(null, SPacketSelectAdvancementsTab.class),
  SERVER_DIFFICULTY(null, SPacketServerDifficulty.class),
  SET_EXPERIENCE(null, SPacketSetExperience.class),
  SET_PASSENGERS(null, SPacketSetPassengers.class),
  SET_SLOT(null, SPacketSetSlot.class),
  SOUND(null, SPacketSoundEffect.class),
  SPAWN_EXPERIENCE_ORB(null, SPacketSpawnExperienceOrb.class),
  SPAWN_GLOBAL_ENTITY(null, SPacketSpawnGlobalEntity.class),
  SPAWN_MOB(null, SPacketSpawnMob.class),
  SPAWN_OBJECT(null, SPacketSpawnObject.class),
  SPAWN_PAINTING(null, SPacketSpawnPainting.class),
  SPAWN_PLAYER(null, SPacketSpawnPlayer.class),
  SPAWN_POSITION(null, SPacketSpawnPosition.class),
  SPECTATE(CPacketSpectate.class, null),
  STATISTICS(null, SPacketStatistics.class),
  STEER_BOAT(CPacketSteerBoat.class, null),
  TAB_COMPLETE(CPacketTabComplete.class, SPacketTabComplete.class),
  TEAMS(null, SPacketTeams.class),
  TIME_UPDATE(null, SPacketTimeUpdate.class),
  TITLE(null, SPacketTitle.class),
  UNLOAD_CHUNK(null, SPacketUnloadChunk.class),
  UNSPECIFIED(null, null),
  UPDATE_BOSS(null, SPacketUpdateBossInfo.class),
  UPDATE_HEALTH(null, SPacketUpdateHealth.class),
  UPDATE_SIGN(CPacketUpdateSign.class, null),
  UPDATE_TILE_ENTITY(null, SPacketUpdateTileEntity.class),
  USE_BED(null, SPacketUseBed.class),
  USE_ENTITY(CPacketUseEntity.class, null),
  USE_ITEM(CPacketPlayerTryUseItem.class, null),
  USE_ITEM_BLOCK(CPacketPlayerTryUseItemOnBlock.class, null),
  VEHICLE_MOVE(CPacketVehicleMove.class, SPacketMoveVehicle.class),
  WINDOW_ITEMS(null, SPacketWindowItems.class),
  WINDOW_PROPERTY(null, SPacketWindowProperty.class),
  WORLD_BORDER(null, SPacketWorldBorder.class);

  private final Class<?> inboundType;
  private final Class<?> outboundType;

  PacketType(final @Nullable Class<?> inboundType, final @Nullable Class<?> outboundType) {
    this.inboundType = inboundType;
    this.outboundType = outboundType;
  }

  public @Nullable Class<?> inboundType() {
    return this.inboundType;
  }

  public @Nullable Class<?> outboundType() {
    return this.outboundType;
  }

  public boolean validate(final @NonNull TypeToken<?> type) {
    return (this.inboundType != null && this.inboundType.equals(type.getRawType()))
      || (this.outboundType != null && this.outboundType.equals(type.getRawType()));
  }
}
