/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
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
@Mixin(PlayerTabOverlay.class)
public class PlayerListComponentMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyConstant(method = "render", constant = @Constant(intValue = 13))
    private int chromium$modifyPingTextRenderOffset(int offset) {
        return ChromiumMod.getConfig().showPingAmount ? offset + 45 : offset;
    }

    @Inject(method = "renderPingIcon", at = @At("HEAD"), cancellable = true)
    private void chromium$betterPingRender(GuiGraphics graphics, int x, int offsetX, int y, PlayerInfo player, CallbackInfo ci) {
        if (ChromiumMod.getConfig().showPingAmount) {
            final var pingText = ChromiumMod.getConfig().pingAmountFormat.replace("<num>", String.valueOf(player.getLatency()));
            final var pingTextWidth = minecraft.font.width(pingText);
            final int pingTextColor = ChromiumMod.getConfig().pingAmountAutoColor ? ColorUtils.getColor(player.getLatency()) : ColorUtils.fromHex(ChromiumMod.getConfig().pingAmountColor);

            int textX = x + offsetX - pingTextWidth - 13;
            if (ChromiumMod.getConfig().replacePingBars) {
                textX += 13;
            }

            graphics.drawString(minecraft.font, pingText, textX, y, pingTextColor);

            if (ChromiumMod.getConfig().replacePingBars) {
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                ci.cancel();
            }
        }
    }
}
