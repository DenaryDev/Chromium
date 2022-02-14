/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.common.skins;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.properties.Property;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.common.manager.Manager;
import io.sapphiremc.chromium.common.skins.provider.MojangSkinsProvider;
import java.io.File;
import java.util.HashSet;
import java.util.UUID;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class SkinsManager implements Manager {

    private SkinsStorage skinsStorage;

    @Override
    public Env getEnv(){
        return Env.SERVER;
    }

    @Override
    public void initialize() {
        skinsStorage = new SkinsStorage(new SkinIO(FabricLoader.getInstance().getConfigDir().resolve(ChromiumMod.getModId() + File.separator + "skins")));
    }

    public void loadPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (getSkin(uuid).equals(SkinsStorage.DEFAULT_SKIN)) {
            setSkin(player, MojangSkinsProvider.getSkin(player.getGameProfile().getName()), false);
        } else {
            setSkin(player, getSkin(player.getUuid()), false);
        }
    }

    public void unloadPlayer(ServerPlayerEntity player) {
        skinsStorage.remove(player.getUuid());
    }

    public Property getSkin(UUID uuid) {
        return skinsStorage.getSkin(uuid);
    }

    public void setSkin(ServerPlayerEntity player, Property skin, boolean update) {
        skinsStorage.setSkin(player.getUuid(), skin);
        applySkin(player, skin, update);
    }

    public void clearSkin(ServerPlayerEntity player) {
        setSkin(player, SkinsStorage.DEFAULT_SKIN, true);
    }

    private void applySkin(ServerPlayerEntity player, Property skin, boolean update) {
        player.getGameProfile().getProperties().removeAll("textures");
        player.getGameProfile().getProperties().put("textures", skin);
        //if (update) update(player); TODO
    }

    private void update(ServerPlayerEntity player) {
        PlayerListS2CPacket removePacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, ImmutableList.of(player));
        PlayerListS2CPacket addPacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, ImmutableList.of(player));
        PlayerPositionLookS2CPacket posPacket = new PlayerPositionLookS2CPacket(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), new HashSet<>(), 0, false);
        UpdateSelectedSlotS2CPacket slotPacket = new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot);

        sendPacket(player, removePacket);
        sendPacket(player, addPacket);

        player.sendAbilitiesUpdate();

        sendPacket(player, posPacket);
        sendPacket(player, slotPacket);

        player.getInventory().updateItems();
    }

    private void sendPacket(ServerPlayerEntity player, Packet<?> packet) {
        player.networkHandler.sendPacket(packet);
    }
}
