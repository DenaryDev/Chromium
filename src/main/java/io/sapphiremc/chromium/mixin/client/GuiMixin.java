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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract Font getFont();

    @Shadow protected abstract Player getCameraPlayer();

    @Inject(method = "render", at = @At("TAIL"))
    private void chromium$renderInfoPanel(GuiGraphics context, float tickDelta, CallbackInfo callbackInfo) {
        boolean flag = this.minecraft.level != null && (!this.minecraft.options.hideGui || this.minecraft.screen != null) && !this.minecraft.options.renderDebug;
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
                    context.drawString(this.getFont(), s, 2, 2 + (i * 9), 0xE0E0E0);
                }
            }
        }
    }
}
