/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardMixin {

    @Shadow @Final private Minecraft minecraft;
    private int cachedKey = 0;

    @Inject(method = "handleDebugKeys", at = @At("HEAD"))
    private void chromium$cacheKey(int key, CallbackInfoReturnable<Boolean> cir) {
        cachedKey = key;
    }

    @Redirect(method = "handleDebugKeys",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasPermissions(I)Z")
    )
    public boolean chromium$hasPermissions(LocalPlayer player, int level) {
        if (cachedKey == 293) {
            return minecraft.getCurrentServer() != null || player.hasPermissions(level);
        } else {
            return player.hasPermissions(level);
        }
    }
}
