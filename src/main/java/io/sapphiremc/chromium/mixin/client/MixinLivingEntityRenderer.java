/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import io.sapphiremc.chromium.client.dummy.DummyClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity> {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void chromium$checkEntityLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof DummyClientPlayerEntity || MinecraftClient.getInstance().currentScreen instanceof TitleScreen) cir.setReturnValue(false);
    }
}
