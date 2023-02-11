/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameModeSelectionScreen.class)
public class MixinGameModeSelectionScreen {

    @Redirect(method = "apply(Lnet/minecraft/client/MinecraftClient;Ljava/util/Optional;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasPermissionLevel(I)Z"
            )
    )
    private static boolean chromium$checkPermissionLevel(ClientPlayerEntity player, int level) {
        if (MinecraftClient.getInstance().getCurrentServerEntry() == null) {
            return player.hasPermissionLevel(level);
        } else {
            return level == 2;
        }
    }
}
