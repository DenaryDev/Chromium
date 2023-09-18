/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.client.compat.IASCompat;
import io.sapphiremc.chromium.client.compat.ModMenuCompat;
import io.sapphiremc.chromium.client.dummy.DummyClientPlayerEntity;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.util.Calendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChromiumTitleScreen extends Screen {

    private static final ResourceLocation MORNING_BACKGROUND = new ResourceLocation(ChromiumMod.MOD_ID, "textures/ui/background/morning.png");
    private static final ResourceLocation DAY_BACKGROUND = new ResourceLocation(ChromiumMod.MOD_ID, "textures/ui/background/day.png");
    private static final ResourceLocation EVENING_BACKGROUND = new ResourceLocation(ChromiumMod.MOD_ID, "textures/ui/background/evening.png");
    private static final ResourceLocation NIGHT_BACKGROUND = new ResourceLocation(ChromiumMod.MOD_ID, "textures/ui/background/night.png");

    private static final ResourceLocation LOGO = new ResourceLocation(ChromiumMod.MOD_ID, "textures/ui/logo.png");
    private static final ResourceLocation GOLD = new ResourceLocation(ChromiumMod.MOD_ID, "textures/ui/gold.png");

    @Getter
    @Setter
    private boolean confirmOpened = false;
    private boolean widgetsAdded = false;

    private int i = width;
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;

    private Button quitButton;
    private Button cancelButton;
    private Button settingsButton;
    private Button realmsButton;

    public ChromiumTitleScreen() {
        this(false);
    }

    public ChromiumTitleScreen(boolean doBackgroundFade) {
        super(Component.translatable("narrator.screen.title"));
        this.doBackgroundFade = doBackgroundFade;
    }

    public static CompletableFuture<Void> preloadResources(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(
                textureManager.preload(MORNING_BACKGROUND, executor),
                textureManager.preload(DAY_BACKGROUND, executor),
                textureManager.preload(EVENING_BACKGROUND, executor),
                textureManager.preload(NIGHT_BACKGROUND, executor),
                textureManager.preload(LOGO, executor)
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        this.confirmOpened = !this.confirmOpened;
        return false;
    }

    @Override
    public void init() {
        DummyClientPlayerEntity.getInstance().getSkinTextureLocation(); // Load skin texture
        final int buttonW = (this.width) / 5;
        final int x = (buttonW + 64) / 2 - buttonW / 2;
        final int centerY = this.height / 2 + 32;
        int modifier = 0;

        final boolean hasModMenu = FabricLoader.getInstance().isModLoaded("modmenu");
        final boolean hasIas = FabricLoader.getInstance().isModLoaded("ias");
        if (hasModMenu && hasIas) {
            modifier = 28;
        } else if (hasModMenu) {
            modifier = 14;
        } else if (hasIas) {
            modifier = 14;
        }

        assert this.minecraft != null;
        this.addRenderableWidget(createButton(x, centerY - 38 - modifier, buttonW, 20, Component.translatable("menu.singleplayer"), (element) -> {
            this.confirmOpened = false;
            this.minecraft.setScreen(new SelectWorldScreen(this));
        }));
        this.addRenderableWidget(createButton(x, centerY - 10 - modifier, buttonW, 20, Component.translatable("menu.multiplayer"), (element) -> {
            this.confirmOpened = false;
            Screen screen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
            this.minecraft.setScreen(screen);
        }));
        if (hasModMenu) {
            this.addRenderableWidget(createButton(x, centerY + 4 - (modifier - 14), buttonW, 20, ModMenuApi.createModsButtonText(), (button) -> {
                this.confirmOpened = false;
                ModMenuCompat.openModsList(this.minecraft, this);
            }));
        }
        if (hasIas) {
            this.addRenderableWidget(createButton(x, centerY + 4 + (modifier - 14), buttonW, 20, Component.translatable("menu.chromium.accounts"), (button) -> {
                this.confirmOpened = false;
                IASCompat.openAccountsList(this.minecraft, this);
            }));
        }
        this.addRenderableWidget(createButton(x, centerY + 18 + modifier, buttonW, 20, Component.translatable("menu.options"), (element) -> {
            this.confirmOpened = false;
            this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));

        this.quitButton = createButton(this.width / 2 - 50, this.height / 2 - 10, 100, 20, Component.translatable("menu.chromium.quit"), (element) ->
                this.minecraft.stop());
        this.cancelButton = createButton(this.width / 2 - 50, this.height / 2 + 18, 100, 20, Component.translatable("menu.chromium.cancel"), (element) ->
                this.confirmOpened = false);
        this.settingsButton = createButton(this.width - 22, 2, 20, 20, Component.literal("S"), (element) ->
                this.minecraft.setScreen(OptionsScreenBuilder.build()));
        this.realmsButton = createButton(this.width - 44, 2, 20, 20, Component.literal("R"), (element) -> {
            this.minecraft.setScreen(new RealmsMainScreen(this));
        });
        this.addRenderableWidget(settingsButton);
        this.addRenderableWidget(realmsButton);
    }

    private Button createButton(int x, int y, int width, int height, Component text, Button.OnPress onPress) {
        return Button.builder(text, onPress)
                .bounds(x, y, width, height)
                .build();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = System.currentTimeMillis();
        }

        assert this.minecraft != null;
        final float f = this.doBackgroundFade ? (float) (System.currentTimeMillis() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) Mth.ceil(Mth.clamp(f, 0.0F, 1.0F)) : 1.0F);
        graphics.blit(getBackground(), 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        final float alpha = this.doBackgroundFade ? Mth.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
        final int what = Mth.ceil(alpha * 255.0f) << 24;
        if ((what & 0xFC000000) == 0) {
            return;
        }

        final int newWidth = ((this.width) / 5) + 64;
        //graphics.fill(0, 13, newWidth, height, -1873784752);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        final int logoW = 90 + this.height / 11;
        graphics.blit(LOGO, (newWidth / 2) - (logoW / 2), -5, 0, 0, logoW, logoW, logoW, logoW);

        final var player = DummyClientPlayerEntity.getInstance();
        final int height = this.height + 50;
        final int playerX = this.width - (int) (this.height / 3.4F);
        renderEntityFollowsMouse(graphics, playerX, height, (int) (this.height / 2.5F), -mouseX + playerX, -mouseY + height - (this.height / 1.535F), player);

        if (!this.confirmOpened) {
            if (widgetsAdded) {
                this.removeWidget(quitButton);
                this.removeWidget(cancelButton);
                this.widgetsAdded = false;
            }
        } else {
            if (!this.widgetsAdded) {
                this.addRenderableWidget(quitButton);
                this.addRenderableWidget(cancelButton);
                this.widgetsAdded = true;
            }

            final var confirmQuit = Component.translatable("menu.chromium.confirmQuit").getString();
            final int textLength = this.font.width(confirmQuit);
            graphics.drawString(this.font, confirmQuit, this.width / 2 - textLength / 2, this.height / 2 - 26, -2039584);
        }

        for (final var element : this.children()) {
            if (!(element instanceof AbstractWidget)) continue;
            ((AbstractWidget) element).setAlpha(alpha);
        }

        super.render(graphics, mouseX, mouseY, delta);

        final var userName = this.minecraft.getUser().getName();
        //String goldAmount = "2023"; //TODO: Get player gold using launcher meta server

        final int centerX = newWidth / 2;
        final int nameLength = this.font.width(userName);
        //int amountLength = this.font.width(goldAmount);

        graphics.drawString(this.font, userName, centerX - (nameLength / 2), 96, -2039584);

        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //int goldX = centerX - (amountLength + 14) / 2;
        //graphics.blit(GOLD, goldX, 110, 0, 0, 11, 11, 11, 11);
        //graphics.drawString(this.font, goldAmount, goldX + 14, 112, 0xFFD700);

        final int settingsButtonY = settingsButton.getY();
        final int realmsButtonY = realmsButton.getY();
        if (SharedConstants.SNAPSHOT) {
            if (settingsButtonY != 15) settingsButton.setY(15);
            if (realmsButtonY != 15) realmsButton.setY(15);
            graphics.fill(0, 0, width, 13, -1873784752);
            final var unstable = Component.translatable("chromium.warnings.unstable").getString();
            graphics.drawString(this.font, unstable, i, 3, 0xFF5555);

            i -= 1;
            if (i < (-font.width(unstable))) {
                i = width;
            }
        } else {
            if (settingsButtonY != 2) settingsButton.setY(2);
            if (realmsButtonY != 2) realmsButton.setY(2);
        }
    }

    private ResourceLocation getBackground() {
        final int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hours >= 6 && hours < 10) {
            return MORNING_BACKGROUND;
        }
        if (hours >= 10 && hours < 19) {
            return DAY_BACKGROUND;
        }
        if (hours >= 19 && hours < 21) {
            return EVENING_BACKGROUND;
        }
        return NIGHT_BACKGROUND;
    }

    private float yaw = 190.0F;

    private void renderEntityFollowsMouse(GuiGraphics graphics, int x, int y, int size, float lookX, float lookY, LocalPlayer player) {
        if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_ALT)) {
            if (this.yaw++ > 359.0F) {
                this.yaw = 0;
            }
        } else if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_ALT)) {
            if (this.yaw-- < 1.0F) {
                this.yaw = 360;
            }
        } else if (InputConstants.isKeyDown(minecraft.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL)) {
            this.yaw = 190.0F;
        }

        final float sideRot = (float) Math.atan(lookX / 400);
        final float upRot = (float) Math.atan(lookY / 400);
        final var poseMultiplier = (new Quaternionf()).rotateZ((float) Math.PI);
        final float yBodyRot = player.yBodyRot;
        final float yRot = player.getYRot();
        final float xRot = player.getXRot();
        final float yHeadRotO = player.yHeadRotO;
        final float yHeadRot = player.yHeadRot;
        player.yBodyRot = yaw + sideRot * 20;
        player.setYRot(yaw + sideRot * 40);
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
