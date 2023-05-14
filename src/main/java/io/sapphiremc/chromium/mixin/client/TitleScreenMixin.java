/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import io.sapphiremc.chromium.client.gui.ChromiumTitleScreen;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "preloadResources", at = @At("RETURN"), cancellable = true)
    private static void chromium$loadTexturesAsync(TextureManager textureManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.setReturnValue(CompletableFuture.allOf(cir.getReturnValue(), ChromiumTitleScreen.preloadResources(textureManager, executor)));
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void chromium$addChromiumSettingsButton(CallbackInfo ci) {
        assert this.minecraft != null;
        addRenderableWidget(Button.builder(Component.literal("S"), (element) -> this.minecraft.setScreen(OptionsScreenBuilder.build()))
                .bounds(this.width - 22, -10, 20, 20)
                .build());
    }
}
