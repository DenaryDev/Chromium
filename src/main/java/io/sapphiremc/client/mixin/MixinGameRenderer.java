package io.sapphiremc.client.mixin;

import com.google.common.base.Strings;
import io.sapphiremc.client.SapphireClientMod;
import io.sapphiremc.client.config.SapphireClientConfig;
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
public abstract class MixinGameRenderer {

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Inject(method = "render", at = @At("TAIL"))
    private void sapphireclient$render(MatrixStack matrices, float tickDelta, CallbackInfo callbackInfo) {
        boolean flag = this.client.world != null && (!this.client.options.hudHidden || this.client.currentScreen != null) && !this.client.options.debugEnabled;
        if (flag) {
            List<String> list = new ArrayList<>();

            if (SapphireClientConfig.SHOW_FPS.getValue()) {
                list.add(SapphireClientMod.getFpsString());
            }

            if (SapphireClientConfig.SHOW_TIME.getValue()) {
                list.add(SapphireClientMod.getTime());
            }

            if (SapphireClientConfig.SHOW_COORDS.getValue()) {
                list.add(SapphireClientMod.getCoordsString(getCameraPlayer()));
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
