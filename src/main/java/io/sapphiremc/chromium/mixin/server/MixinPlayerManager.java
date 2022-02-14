/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.server;

import io.sapphiremc.chromium.ChromiumMod;
import java.util.List;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {

    @Shadow public abstract List<ServerPlayerEntity> getPlayerList();

    @Inject(method = "onPlayerConnect", at = @At(value = "HEAD"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        ChromiumMod.getSkinsManager().loadPlayer(player);
    }

    @Inject(method = "remove", at = @At("TAIL"))
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        ChromiumMod.getSkinsManager().unloadPlayer(player);
    }

    @Inject(method = "disconnectAllPlayers", at = @At("HEAD"))
    private void disconnectAllPlayers(CallbackInfo ci) {
        for (ServerPlayerEntity player : getPlayerList()) {
            ChromiumMod.getSkinsManager().unloadPlayer(player);
        }
    }
}
