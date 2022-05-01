/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.shared.skins;

import com.mojang.authlib.properties.Property;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.shared.manager.Manager;
import io.sapphiremc.chromium.shared.skins.provider.MojangSkinsProvider;
import java.io.File;
import java.util.UUID;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

public class SkinsManager implements Manager {

    private SkinsStorage skinsStorage;

    @Override
    public Env getEnv(){
        return Env.SERVER;
    }

    @Override
    public void initialize() {
        skinsStorage = new SkinsStorage(new SkinIO(FabricLoader.getInstance().getConfigDir().resolve(ChromiumMod.MOD_ID + File.separator + "skins")));
    }

    public void loadPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (getSkin(uuid).equals(SkinsStorage.DEFAULT_SKIN)) {
            setSkin(player, MojangSkinsProvider.getSkin(player.getGameProfile().getName()));
        } else {
            setSkin(player, getSkin(player.getUuid()));
        }
    }

    public void unloadPlayer(ServerPlayerEntity player) {
        skinsStorage.remove(player.getUuid());
    }

    public Property getSkin(UUID uuid) {
        return skinsStorage.getSkin(uuid);
    }

    public void setSkin(ServerPlayerEntity player, Property skin) {
        skinsStorage.setSkin(player.getUuid(), skin);
        applySkin(player, skin);
    }

    public void clearSkin(ServerPlayerEntity player) {
        setSkin(player, SkinsStorage.DEFAULT_SKIN);
    }

    private void applySkin(ServerPlayerEntity player, Property skin) {
        player.getGameProfile().getProperties().removeAll("textures");
        player.getGameProfile().getProperties().put("textures", skin);
    }
}
