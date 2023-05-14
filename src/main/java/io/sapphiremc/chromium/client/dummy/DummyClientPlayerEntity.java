/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.dummy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.jetbrains.annotations.Nullable;

public class DummyClientPlayerEntity extends LocalPlayer {
    private static DummyClientPlayerEntity instance;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity();
        return instance;
    }

    private DummyClientPlayerEntity() {
        super(Minecraft.getInstance(), DummyClientWorld.getInstance(), DummyClientPacketListener.getInstance(), null, null, false, false);
    }

    @Override
    public boolean isModelPartShown(PlayerModelPart modelPart) {
        return true;
    }

    @Nullable
    @Override
    protected PlayerInfo getPlayerInfo() {
        return connection.getPlayerInfo(getUUID());
    }
}
