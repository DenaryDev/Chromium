/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import com.google.common.base.Strings;
import io.sapphiremc.chromium.ChromiumMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Inject(method = "render", at = @At("TAIL"))
    private void chromium$renderInfoPanel(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
        boolean flag = this.client.world != null && (!this.client.options.hudHidden || this.client.currentScreen != null) && !this.client.options.debugEnabled;
        if (flag) {
            final var config = ChromiumMod.getConfig();
            final var player = getCameraPlayer();
            final var info = new ArrayList<String>();

            if (config.isShowFps()) {
                info.add(ChromiumMod.getFpsString());
            }

            if (config.isShowTime()) {
                info.add(ChromiumMod.getTimeString());
            }

            if (config.isShowCoords()) {
                info.add(ChromiumMod.getCoordsString(player));
            }

            if (config.isShowLight()) {
                info.add(ChromiumMod.getLightString(player));
            }

            if (config.isShowBiome()) {
                info.add(ChromiumMod.getBiomeString(player));
            }

            if (info.size() > 0) {
                for (int i = 0; i < info.size(); i++) {
                    final var s = info.get(i);
                    if (Strings.isNullOrEmpty(s)) continue;
                    this.getTextRenderer().drawWithShadow(matrices, s, 2, 2 + (i * 9), 0xE0E0E0);
                }
            }
        }
    }
}
