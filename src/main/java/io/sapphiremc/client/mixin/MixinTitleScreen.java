/*
 * Copyright (c) 2022 DenaryDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package io.sapphiremc.client.mixin;

import io.sapphiremc.client.SapphireClientMod;
import io.sapphiremc.client.gui.OptionsScreenBuilder;
import io.sapphiremc.client.gui.SClientTitleScreen;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    @Shadow @Final private static Identifier MINECRAFT_TITLE_TEXTURE;

    @Shadow @Final private static Identifier EDITION_TITLE_TEXTURE;

    @Shadow @Final private static Identifier PANORAMA_OVERLAY;

    @Shadow @Final public static CubeMapRenderer PANORAMA_CUBE_MAP;

    protected MixinTitleScreen(Text title) {
        super(title);
    }

    /**
     * @author DenaryDev
     * @reason Custom main menu
     */
    @Overwrite
    public static @NotNull CompletableFuture<Void> loadTexturesAsync(@NotNull TextureManager textureManager, Executor executor) {
        CompletableFuture<Void> future = CompletableFuture.allOf(textureManager.loadTextureAsync(MINECRAFT_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(EDITION_TITLE_TEXTURE, executor), textureManager.loadTextureAsync(PANORAMA_OVERLAY, executor), PANORAMA_CUBE_MAP.loadTexturesAsync(textureManager, executor));
        return CompletableFuture.allOf(future, SClientTitleScreen.loadTexturesAsync(textureManager, executor));
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void sapphireclient$addChangeScreenButton(CallbackInfo ci) {
        addDrawableChild(new ButtonWidget(this.width - 22, 2, 20, 20, new TranslatableText("S"), (element) ->
                this.client.setScreen(OptionsScreenBuilder.build(SapphireClientMod.getInstance()))));
    }
}
