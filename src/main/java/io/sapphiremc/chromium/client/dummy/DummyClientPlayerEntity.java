/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.dummy;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private static DummyClientPlayerEntity instance;
    private Identifier skinIdentifier = null;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity();
        return instance;
    }

    private DummyClientPlayerEntity() {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null, false, false);
    }

    public void updateSkin() {
        MinecraftClient.getInstance().getSkinProvider().loadSkin(getGameProfile(), (type, identifier, texture) -> {
            if (type.equals(MinecraftProfileTexture.Type.SKIN)) {
                skinIdentifier = identifier;
            }
        }, false);
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public boolean hasSkinTexture() {
        return true;
    }

    @Override
    public Identifier getSkinTexture() {
        return skinIdentifier == null ? DefaultSkinHelper.getTexture(this.getUuid()) : skinIdentifier;
    }

    @Override
    public String getModel() {
        return "slim";
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public double squaredDistanceTo(Vec3d vector) {
        return 0;
    }

    @Override
    public double squaredDistanceTo(Entity entity) {
        return 0;
    }

    @Override
    public double squaredDistanceTo(double x, double y, double z) {
        return 0;
    }
}
