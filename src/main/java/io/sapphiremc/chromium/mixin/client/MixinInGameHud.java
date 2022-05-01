/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import com.google.common.base.Strings;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.shared.config.ChromiumConfig;
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
import java.util.List;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Inject(method = "render", at = @At("TAIL"))
    private void chromium$renderInfoPanel(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
        boolean flag = this.client.world != null && (!this.client.options.hudHidden || this.client.currentScreen != null) && !this.client.options.debugEnabled;
        if (flag) {
            ChromiumConfig config = ChromiumMod.getConfig();
            PlayerEntity player = getCameraPlayer();
            List<String> list = new ArrayList<>();

            if (config.isShowFps()) {
                list.add(ChromiumMod.getFpsString());
            }

            if (config.isShowTime()) {
                list.add(ChromiumMod.getTime());
            }

            if (config.isShowCoords()) {
                list.add(ChromiumMod.getCoordsString(player));
            }

            if (config.isShowLight()) {
                list.add(ChromiumMod.getLightString(player));
            }

            if (config.isShowBiome()) {
                list.add(ChromiumMod.getBiomeString(player));
            }

            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    String s = list.get(i);
                    if (Strings.isNullOrEmpty(s)) continue;
                    this.getTextRenderer().drawWithShadow(matrices, s, 2, 2 + (i * 9), 0xE0E0E0);
                }
            }
        }
    }
}
