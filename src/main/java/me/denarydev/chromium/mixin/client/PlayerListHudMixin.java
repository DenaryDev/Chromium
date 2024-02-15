/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.chromium.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import me.denarydev.chromium.ChromiumMod;
import me.denarydev.chromium.util.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author DenaryDev
 * @since 19:59 14.12.2023
 */
@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Shadow @Final private MinecraftClient client;

    @ModifyConstant(method = "render", constant = @Constant(intValue = 13))
    private int chromium$modifyPingTextRenderOffset(int offset) {
        return ChromiumMod.getConfig().showPingAmount ? offset + 45 : offset;
    }

    @Inject(method = "renderLatencyIcon", at = @At("HEAD"), cancellable = true)
    private void chromium$betterPingRender(DrawContext context, int x, int offsetX, int y, PlayerListEntry player, CallbackInfo ci) {
        if (ChromiumMod.getConfig().showPingAmount) {
            final var text = ChromiumMod.getConfig().pingAmountFormat.replace("<num>", String.valueOf(player.getLatency()));
            final var width = client.textRenderer.getWidth(text);
            final int color = ChromiumMod.getConfig().pingAmountAutoColor ? ColorUtils.getColor(player.getLatency()) : ColorUtils.fromHex(ChromiumMod.getConfig().pingAmountColor);

            int textX = x + offsetX - width - 13;
            if (ChromiumMod.getConfig().replacePingBars) {
                textX += 13;
            }

            context.drawTextWithShadow(client.textRenderer, text, textX, y, color);

            if (ChromiumMod.getConfig().replacePingBars) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                ci.cancel();
            }
        }
    }
}
