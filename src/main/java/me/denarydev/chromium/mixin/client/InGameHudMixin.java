/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.chromium.mixin.client;

import com.google.common.base.Strings;
import me.denarydev.chromium.ChromiumMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Shadow @Final private DebugHud debugHud;

    @Inject(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderAutosaveIndicator(Lnet/minecraft/client/gui/DrawContext;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void chromium$renderInfoPanel(DrawContext context, float tickDelta, CallbackInfo ci) {
        boolean flag = this.client.world != null && (!this.client.options.hudHidden || this.client.currentScreen != null) && !this.debugHud.shouldShowDebugHud();
        if (flag) {
            final var config = ChromiumMod.getConfig();
            final var player = getCameraPlayer();
            final var info = new ArrayList<String>();

            if (config.showFps) {
                info.add(ChromiumMod.getFpsString());
            }

            if (config.showTime) {
                info.add(ChromiumMod.getTimeString());
            }

            if (config.showCoords) {
                info.add(ChromiumMod.getCoordsString(player));
            }

            if (config.showLight) {
                info.add(ChromiumMod.getLightString(player));
            }

            if (config.showBiome) {
                info.add(ChromiumMod.getBiomeString(player));
            }

            if (!info.isEmpty()) {
                for (int i = 0; i < info.size(); i++) {
                    final var s = info.get(i);
                    if (Strings.isNullOrEmpty(s)) continue;
                    context.drawTextWithShadow(this.getTextRenderer(), s, 2, 2 + (i * 9), 0xE0E0E0);
                }
            }
        }
    }
}
