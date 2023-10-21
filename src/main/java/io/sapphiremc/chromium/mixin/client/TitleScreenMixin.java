/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.mixin.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import io.sapphiremc.chromium.client.ChromiumClientMod;
import io.sapphiremc.chromium.client.dummy.DummyClientPlayerEntity;
import io.sapphiremc.chromium.client.gui.OptionsScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    protected void chromium$addChromiumSettingsButton(CallbackInfo ci) {
        assert this.minecraft != null;
        addRenderableWidget(Button.builder(Component.literal("S"), (element) -> this.minecraft.setScreen(OptionsScreenBuilder.build()))
                .bounds(this.width - 22, -10, 20, 20)
                .build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void chromium$renderPlayerModel(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        final var player = DummyClientPlayerEntity.getInstance();
        final int height = this.height / 4 + 132;
        final int playerX = this.width / 2 - 160;
        final int size = 40;
        final float playerLookX = -mouseX + playerX;
        final float lookY = -mouseY + height - size;
        renderEntityFollowsMouse(graphics, playerX, height, size, playerLookX, lookY, player);

        final var entity = ChromiumClientMod.getRandomEntity();
        final int entityX = this.width / 2 + 160;
        final float entityLookX = -mouseX + entityX;
        if (entity != null) {
            renderEntityFollowsMouse(graphics, entityX, height, size, entityLookX, lookY, entity);
        }
    }

    @SuppressWarnings({"deprecation"})
    @Unique
    private void renderEntityFollowsMouse(GuiGraphics graphics, int x, int y, int size, float lookX, float lookY, LivingEntity player) {
        final float sideRot = (float) Math.atan(lookX / 200);
        final float upRot = (float) Math.atan(lookY / 200);
        final var poseMultiplier = (new Quaternionf()).rotateZ((float) Math.PI);
        final float yBodyRot = player.yBodyRot;
        final float yRot = player.getYRot();
        final float xRot = player.getXRot();
        final float yHeadRotO = player.yHeadRotO;
        final float yHeadRot = player.yHeadRot;
        player.yBodyRot = 180 + sideRot * 20;
        player.setYRot(180 + sideRot * 40);
        player.setXRot(-upRot * 20);
        player.yHeadRot = player.getYRot();
        player.yHeadRotO = player.getYRot();
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 50);
        graphics.pose().mulPoseMatrix((new Matrix4f()).scaling((float) size, (float) size, (float) (-size)));
        graphics.pose().mulPose(poseMultiplier);
        Lighting.setupForEntityInInventory();
        final var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(player, 0.0, 0.0, 0.0, 0.0F, 1.0F, graphics.pose(), graphics.bufferSource(), 15728880));
        graphics.flush();
        dispatcher.setRenderShadow(true);
        graphics.pose().popPose();
        Lighting.setupFor3DItems();
        player.yBodyRot = yBodyRot;
        player.setYRot(yRot);
        player.setXRot(xRot);
        player.yHeadRotO = yHeadRotO;
        player.yHeadRot = yHeadRot;
    }
}
