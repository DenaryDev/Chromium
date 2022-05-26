/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import io.sapphiremc.chromium.client.gui.ChromiumTitleScreen;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    @Shadow @Final private static Identifier MINECRAFT_TITLE_TEXTURE;

    @Shadow @Final private static Identifier EDITION_TITLE_TEXTURE;

    @Shadow @Final private static Identifier PANORAMA_OVERLAY;

    @Shadow @Final public static CubeMapRenderer PANORAMA_CUBE_MAP;

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "loadTexturesAsync", at = @At("RETURN"), cancellable = true)
    private static void chromium$loadTexturesAsync(TextureManager textureManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.setReturnValue(CompletableFuture.allOf(cir.getReturnValue(), ChromiumTitleScreen.loadTexturesAsync(textureManager, executor)));
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void chromium$addChromiumSettingsButton(CallbackInfo ci) {
        addDrawableChild(new ButtonWidget(this.width - 22, 2, 20, 20, Text.literal("S"), (element) ->
                this.client.setScreen(OptionsScreenBuilder.build())));
    }
}
