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
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CJigsawBlockGeneratePacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CMarkRecipeSeenPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateRecipeBookStatusPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerDiggingPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Represents the {@link PacketDirection#INCOMING} and {@link PacketDirection#OUTGOING}
 * packet types in the game as of version 1.16.5.
 */
@SuppressWarnings({"unchecked", "UnstableApiUsage"})
public enum PacketType {
  ADVANCEMENT_INFO(null, SAdvancementInfoPacket.class),
  ANIMATE_BLOCK_BREAK(null, SAnimateBlockBreakPacket.class),
  ANIMATE_HAND(CAnimateHandPacket.class, SAnimateHandPacket.class),
  BLOCK_ACTION(null, SBlockActionPacket.class),
  CAMERA(null, SCameraPacket.class),
  CHANGE_BLOCK(null, SChangeBlockPacket.class),
  CHANGE_GAME_STATE(null, SChangeGameStatePacket.class),
  CHAT(CChatMessagePacket.class, SChatPacket.class),
  CHUNK_DATA(null, SChunkDataPacket.class),
  CLICK_WINDOW(CClickWindowPacket.class, null),
  CLIENT_SETTINGS(CClientSettingsPacket.class, null),
  CLIENT_STATUS(CClientStatusPacket.class, null),
  CLOSE_WINDOW(CCloseWindowPacket.class, SCloseWindowPacket.class),
  COLLECT_ITEM(null, SCollectItemPacket.class),
  COMBAT(null, SCombatPacket.class),
  COMMAND_LIST(null, SCommandListPacket.class),
  CONFIRM_TELEPORT(CConfirmTeleportPacket.class, null),
  CONFIRM_TRANSACTION(CConfirmTransactionPacket.class, SConfirmTransactionPacket.class),
  COOLDOWN(null, SCooldownPacket.class),
  CREATIVE_INVENTORY_ACTION(CCreativeInventoryActionPacket.class, null),
  CUSTOM_PAYLOAD_PLAY(CCustomPayloadPacket.class, SCustomPayloadPlayPacket.class),
//  CUSTOM_PAYLOAD_LOGIN(CCustomPayloadLoginPacket.class, SCustomPayloadLoginPacket.class),
  DESTROY_ENTITIES(null, SDestroyEntitiesPacket.class),
  DISCONNECT(null, SDisconnectPacket.class),
//  DISCONNECT_LOGIN(null, SDisconnectLoginPacket.class),
  DISPLAY_OBJECTIVE(null, SDisplayObjectivePacket.class),
  EDIT_BOOK(CEditBookPacket.class, null),
  ENCHANT_ITEM(CEnchantItemPacket.class, null),
  ENTITY_ACTION(CEntityActionPacket.class, null),
  ENTITY_EQUIPMENT(null, SEntityEquipmentPacket.class),
  ENTITY_HEAD_LOOK(null, SEntityHeadLookPacket.class),
  ENTITY_METADATA(null, SEntityMetadataPacket.class),
  ENTITY(null, SEntityPacket.class),
  ENTITY_PROPERTIES(null, SEntityPropertiesPacket.class),
  ENTITY_STATUS(null, SEntityStatusPacket.class),
  ENTITY_TELEPORT(null, SEntityTeleportPacket.class),
  ENTITY_VELOCITY(null, SEntityVelocityPacket.class),
  EXPLOSION(null, SExplosionPacket.class),
  HELD_ITEM_CHANGE(CHeldItemChangePacket.class, SHeldItemChangePacket.class),
  INPUT(CInputPacket.class, null),
  JIGSAW_BLOCK_GENERATE(CJigsawBlockGeneratePacket.class, null),
  JOIN_GAME(null, SJoinGamePacket.class),
  KEEP_ALIVE(CKeepAlivePacket.class, SKeepAlivePacket.class),
  LOCK_DIFFICULTY(CLockDifficultyPacket.class, null),
  MAP_DATA(null, SMapDataPacket.class),
  MARK_RECIPE_SEEN(CMarkRecipeSeenPacket.class, null),
  MERCHANT_OFFERS(null, SMerchantOffersPacket.class),
  MOUNT_ENTITY(null, SMountEntityPacket.class),
  MOVE_VEHICLE(CMoveVehiclePacket.class, SMoveVehiclePacket.class),
  MULTI_BLOCK_CHANGE(null, SMultiBlockChangePacket.class),
  OPEN_BOOK_WINDOW(null, SOpenBookWindowPacket.class),
  OPEN_HORSE_WINDOW(null, SOpenHorseWindowPacket.class),
  OPEN_SIGN_MENU(null, SOpenSignMenuPacket.class),
  OPEN_WINDOW(null, SOpenWindowPacket.class),
  PICK_ITEM(CPickItemPacket.class, null),
  PLACE_RECIPE(CPlaceRecipePacket.class, SPlaceGhostRecipePacket.class),
  PLAY_ENTITY_EFFECT(null, SPlayEntityEffectPacket.class),
  PLAYER(CPlayerPacket.class, null),
  PLAYER_ABILITIES(CPlayerAbilitiesPacket.class, SPlayerAbilitiesPacket.class),
  PLAYER_DIGGING(CPlayerDiggingPacket.class, SPlayerDiggingPacket.class),
  PLAYER_LIST_HEADER_FOOTER(null, SPlayerListHeaderFooterPacket.class),
  PLAYER_LIST_ITEM(null, SPlayerListItemPacket.class),
  PLAYER_LOOK(null, SPlayerLookPacket.class),
  PLAYER_POSITION_LOOK(null, SPlayerPositionLookPacket.class),
  PLAYER_TRY_USE_ITEM(CPlayerTryUseItemPacket.class, null),
  PLAYER_TRY_USE_ITEM_ON_BLOCK(CPlayerTryUseItemOnBlockPacket.class, null),
  PLAY_SOUND_EFFECT(null, SPlaySoundEffectPacket.class),
  PLAY_SOUND_EVENT(null, SPlaySoundEventPacket.class),
  PLAY_SOUND(null, SPlaySoundPacket.class),
  QUERY_ENTITY_NBT(CQueryEntityNBTPacket.class, SQueryNBTResponsePacket.class),
  QUERY_TILE_ENTITY_RESPONSE(CQueryEntityNBTPacket.class, SQueryNBTResponsePacket.class),
  RECIPE_BOOK(null, SRecipeBookPacket.class),
  REMOVE_ENTITY_EFFECT(null, SRemoveEntityEffectPacket.class),
  RENAME_ITEM(CRenameItemPacket.class, null),
  RESPAWN(null, SRespawnPacket.class),
  SCOREBOARD_OBJECTIVE(null, SScoreboardObjectivePacket.class),
  SELECT_ADVANCEMENTS_TAB(null, SSelectAdvancementsTabPacket.class),
  SELECT_TRADE(CSelectTradePacket.class, null),
  SEEN_ADVANCEMENTS(CSeenAdvancementsPacket.class, null),
  SEND_RESOURCE_PACK(CResourcePackStatusPacket.class, SSendResourcePackPacket.class),
  SERVER_DIFFICULTY(CSetDifficultyPacket.class, SServerDifficultyPacket.class),
  SET_EXPERIENCE(null, SSetExperiencePacket.class),
  SET_PASSENGERS(null, SSetPassengersPacket.class),
  SET_SLOT(null, SSetSlotPacket.class),
  SPAWN_EXPERIENCE_ORB(null, SSpawnExperienceOrbPacket.class),
  SPAWN_MOB(null, SSpawnMobPacket.class),
  SPAWN_MOVING_SOUND_EFFECT(null, SSpawnMovingSoundEffectPacket.class),
  SPAWN_OBJECT(null, SSpawnObjectPacket.class),
  SPAWN_PAINTING(null, SSpawnPaintingPacket.class),
  SPAWN_PARTICLE(null, SSpawnParticlePacket.class),
  SPAWN_PLAYER(null, SSpawnPlayerPacket.class),
  SPECTATE(CSpectatePacket.class, null),
  STATISTICS(null, SStatisticsPacket.class),
  STEER_BOAT(CSteerBoatPacket.class, null),
  STOP_SOUND(null, SStopSoundPacket.class),
  TAB_COMPLETE(CTabCompletePacket.class, STabCompletePacket.class),
  TAGS_LIST(null, STagsListPacket.class),
  TEAMS(null, STeamsPacket.class),
  TITLE(null, STitlePacket.class),
  UNLOAD_CHUNK(null, SUnloadChunkPacket.class),
  UPDATE_BEACON(CUpdateBeaconPacket.class, null),
  UPDATE_CHUNK_POSITION(null, SUpdateChunkPositionPacket.class),
  UPDATE_COMMAND_BLOCK(CUpdateCommandBlockPacket.class, null),
  UPDATE_HEALTH(null, SUpdateHealthPacket.class),
  UPDATE_JIGSAW_BLOCK(CUpdateJigsawBlockPacket.class, null),
  UPDATE_LIGHT(null, SUpdateLightPacket.class),
  UPDATE_MINECART_COMMAND_BLOCK(CUpdateMinecartCommandBlockPacket.class, null),
  UPDATE_RECIPES(CUpdateRecipeBookStatusPacket.class, SUpdateRecipesPacket.class),
  UPDATE_SIGN(CUpdateSignPacket.class, null),
  UPDATE_SCORE(null, SUpdateScorePacket.class),
  UPDATE_STRUCTURE_BLOCK(CUpdateStructureBlockPacket.class, null),
  UPDATE_TILE_ENTITY(null, SUpdateTileEntityPacket.class),
  UPDATE_TIME(null, SUpdateTimePacket.class),
  UPDATE_VIEW_DISTANCE(null, SUpdateViewDistancePacket.class),
  USE_ENTITY(CUseEntityPacket.class, null),
  WINDOW_ITEMS(null, SWindowItemsPacket.class),
  WINDOW_PROPERTY(null, SWindowPropertyPacket.class),
  WORLD_BORDER(null, SWorldBorderPacket.class),
  WORLD_SPAWN_CHANGED(null, SWorldSpawnChangedPacket.class),

  UNSPECIFIED(null, null);

  private final Class<?> inboundType;
  private final Class<?> outboundType;

  PacketType(final @Nullable Class<?> inboundType, final @Nullable Class<?> outboundType) {
    this.inboundType = inboundType;
    this.outboundType = outboundType;
  }

  /**
   * Returns the {@link PacketDirection#INCOMING} type.
   *
   * @return the incoming type
   */
  public @Nullable Class<?> inboundType() {
    return this.inboundType;
  }

  /**
   * Returns the {@link PacketDirection#OUTGOING} type.
   *
   * @return the outgoing type
   */
  public @Nullable Class<?> outboundType() {
    return this.outboundType;
  }

  /**
   * Creates an instance of the {@code E} packet for the specified
   * {@link PacketDirection}.
   *
   * @param direction the packet direction
   * @param <E> the packet type
   * @return the packet
   * @throws Throwable exceptions if ths specified direction, or the packet
   *                   type for the specified direction is null, the direction
   *                   is UNSPECIFIED, or there was a problem attempting to
   *                   create an instance
   */
  public <E> @NonNull E create(final @NonNull PacketDirection direction) throws Throwable {
    if(direction == PacketDirection.INCOMING) return (E) requireNonNull(this.inboundType, "inbound").newInstance();
    if(direction == PacketDirection.OUTGOING) return (E) requireNonNull(this.outboundType, "outbound").newInstance();
    throw new IllegalArgumentException("PacketDirection cannot be unspecified");
  }

  /**
   * Validates that this packet type contains the specified
   * type.
   *
   * @param type the packet type
   * @return whether the specified type is matching
   */
  public boolean validate(final @NonNull TypeToken<?> type) {
    return (this.inboundType != null && this.inboundType.equals(type.getRawType()))
      || (this.outboundType != null && this.outboundType.equals(type.getRawType()));
  }
}
