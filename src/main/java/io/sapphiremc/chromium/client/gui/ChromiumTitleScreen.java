/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.client.compat.IASCompat;
import io.sapphiremc.chromium.client.compat.ModMenuCompat;
import io.sapphiremc.chromium.client.dummy.DummyClientPlayerEntity;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import java.util.Calendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChromiumTitleScreen extends Screen {

    private static final Identifier MORNING_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/morning.png");
    private static final Identifier DAY_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/day.png");
    private static final Identifier EVENING_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/evening.png");
    private static final Identifier NIGHT_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/night.png");

    private static final Identifier LOGO = new Identifier(ChromiumMod.MOD_ID, "textures/ui/logo.png");
    //private static final Identifier GOLD = new Identifier(ChromiumMod.MOD_ID, "textures/ui/gold.png");

    @Getter
    @Setter
    private boolean confirmOpened = false;
    private boolean widgetsAdded = false;

    private int i = width;
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;

    private ButtonWidget quitButton;
    private ButtonWidget cancelButton;
    private ButtonWidget settingsButton;
    private ButtonWidget realmsButton;

    public ChromiumTitleScreen() {
        this(false);
    }

    public ChromiumTitleScreen(boolean doBackgroundFade) {
        super(Text.translatable("narrator.screen.title"));
        this.doBackgroundFade = doBackgroundFade;
    }

    public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(
                textureManager.loadTextureAsync(MORNING_BACKGROUND, executor),
                textureManager.loadTextureAsync(DAY_BACKGROUND, executor),
                textureManager.loadTextureAsync(EVENING_BACKGROUND, executor),
                textureManager.loadTextureAsync(NIGHT_BACKGROUND, executor),
                textureManager.loadTextureAsync(LOGO, executor)
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        this.confirmOpened = !this.confirmOpened;
        return false;
    }

    @Override
    public void init() {
        DummyClientPlayerEntity.getInstance().getSkinTexture(); // Load skin texture
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

        assert this.client != null;
        this.addDrawableChild(createButton(x, centerY - 38 - modifier, buttonW, 20, Text.translatable("menu.singleplayer"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new SelectWorldScreen(this));
        }));
        this.addDrawableChild(createButton(x, centerY - 10 - modifier, buttonW, 20, Text.translatable("menu.multiplayer"), (element) -> {
            this.confirmOpened = false;
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen(screen);
        }));
        if (hasModMenu) {
            this.addDrawableChild(createButton(x, centerY + 4 - (modifier - 14), buttonW, 20, ModMenuApi.createModsButtonText(), (button) -> {
                this.confirmOpened = false;
                ModMenuCompat.openModsList(this.client, this);
            }));
        }
        if (hasIas) {
            this.addDrawableChild(createButton(x, centerY + 4 + (modifier - 14), buttonW, 20, Text.translatable("menu.chromium.accounts"), (button) -> {
                this.confirmOpened = false;
                IASCompat.openAccountsList(this.client, this);
            }));
        }
        this.addDrawableChild(createButton(x, centerY + 18 + modifier, buttonW, 20, Text.translatable("menu.options"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));

        this.quitButton = createButton(this.width / 2 - 50, this.height / 2 - 10, 100, 20, Text.translatable("menu.chromium.quit"), (element) ->
                this.client.stop());
        this.cancelButton = createButton(this.width / 2 - 50, this.height / 2 + 18, 100, 20, Text.translatable("menu.chromium.cancel"), (element) ->
                this.confirmOpened = false);
        this.settingsButton = createButton(this.width - 22, 2, 20, 20, Text.literal("S"), (element) ->
                this.client.setScreen(OptionsScreenBuilder.build()));
        this.realmsButton = createButton(this.width - 44, 2, 20, 20, Text.literal("R"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new ChromiumTitleScreen());
        });
        this.addDrawableChild(settingsButton);
        this.addDrawableChild(realmsButton);
    }

    private ButtonWidget createButton(int x, int y, int width, int height, Text text, ButtonWidget.PressAction pressAction) {
        return ButtonWidget.builder(text, pressAction)
                .dimensions(x, y, width, height)
                .build();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = System.currentTimeMillis();
        }

        assert this.client != null;
        final float f = this.doBackgroundFade ? (float) (System.currentTimeMillis() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        context.drawTexture(getBackground(), 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        final float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
        final int l = MathHelper.ceil(g * 255.0f) << 24;
        if ((l & 0xFC000000) == 0) {
            return;
        }

        final int newWidth = ((this.width) / 5) + 64;
        //fill(matrixStack, 0, 13, newWidth, height, -1873784752);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);
        final int logoW = 90 + this.height / 11;
        context.drawTexture(LOGO, (newWidth / 2) - (logoW / 2), -5, 0, 0, logoW, logoW, logoW, logoW);

        final var player = DummyClientPlayerEntity.getInstance();
        final int height = this.height + 50;
        final int playerX = this.width - (int) (this.height / 3.4F);
        drawEntity(playerX, height, (int) (this.height / 2.5F), -mouseX + playerX, -mouseY + height - (this.height / 1.535F), player);

        if (!this.confirmOpened) {
            if (widgetsAdded) {
                this.remove(quitButton);
                this.remove(cancelButton);
                this.widgetsAdded = false;
            }
        } else {
            if (!this.widgetsAdded) {
                this.addDrawableChild(quitButton);
                this.addDrawableChild(cancelButton);
                this.widgetsAdded = true;
            }

            final var confirmQuit = Text.translatable("menu.chromium.confirmQuit").getString();
            final int textLength = this.textRenderer.getWidth(confirmQuit);
            context.drawTextWithShadow(this.textRenderer, confirmQuit, this.width / 2 - textLength / 2, this.height / 2 - 26, -2039584);
        }

        for (final var element : this.children()) {
            if (!(element instanceof ClickableWidget)) continue;
            ((ClickableWidget) element).setAlpha(g);
        }

        super.render(context, mouseX, mouseY, delta);

        final var userName = this.client.getSession().getUsername();
        //String goldAmount = "2022"; //TODO: Get player gold using launcher meta server

        final int centerX = newWidth / 2;
        final int nameLength = this.textRenderer.getWidth(userName);
        //int amountLength = this.textRenderer.getWidth(goldAmount);

        context.drawTextWithShadow(this.textRenderer, userName, centerX - (nameLength / 2), 96, -2039584);

        //RenderSystem.enableBlend();
        //RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        //RenderSystem.setShaderTexture(0, GOLD);
        //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        //int goldX = centerX - (amountLength + 14) / 2;
        //drawTexture(matrixStack, goldX, 110, 0, 0, 11, 11, 11, 11);
        //this.textRenderer.drawWithShadow(matrixStack, goldAmount, goldX + 14, 112, 0xFFD700);

        final var modVersion = FabricLoader.getInstance().getModContainer(ChromiumMod.MOD_ID).get().getMetadata().getVersion().getFriendlyString().toLowerCase();
        final boolean isUnstable = modVersion.contains("alpha") || modVersion.contains("beta") || modVersion.contains("pre") || modVersion.contains("rc") || modVersion.contains("snapshot");
        final int settingsButtonY = settingsButton.getY();
        final int realmsButtonY = realmsButton.getY();
        if (isUnstable) {
            if (settingsButtonY != 15) settingsButton.setY(15);
            if (realmsButtonY != 15) realmsButton.setY(15);
            context.fill(0, 0, width, 13, -1873784752);
            final var beta = Text.translatable("chromium.warnings.unstable").getString();
            context.drawTextWithShadow(this.textRenderer, beta, i, 3, 0xFF5555);

            i -= 1;
            if (i < (-textRenderer.getWidth(beta))) {
                i = width;
            }
        } else {
            if (settingsButtonY != 2) settingsButton.setY(2);
            if (realmsButtonY != 2) realmsButton.setY(2);
        }
    }

    private Identifier getBackground() {
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

    private void drawEntity(int x, int y, int size, float mouseX, float mouseY, ClientPlayerEntity player) {
        assert this.client != null;
        if (InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_LEFT_ALT)) {
            if (this.yaw++ > 359.0F) {
                this.yaw = 0;
            }
        } else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_RIGHT_ALT)) {
            if (this.yaw-- < 1.0F) {
                this.yaw = 360;
            }
        } else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_RIGHT_CONTROL)) {
            this.yaw = 190.0F;
        }

        final float rotationSide = (float) Math.atan(mouseX / 400.0F);
        final float rotationUp = (float) Math.atan(mouseY / 400.0F);
        final var viewMatrices = RenderSystem.getModelViewStack();
        viewMatrices.push();
        viewMatrices.translate(x, y, 1050.0D);
        viewMatrices.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        final var sizeMatrices = new MatrixStack();
        sizeMatrices.translate(0.0D, 0.0D, 1000.0D);
        sizeMatrices.scale((float) size, (float) size, (float) size);
        final var rotatedZ = new Quaternionf().rotateZ((float) Math.PI);
        sizeMatrices.multiply(rotatedZ);
        final float bodyYaw = player.bodyYaw;
        final float playerYaw = player.getYaw();
        final float playerPitch = player.getPitch();
        final float prevHeadYaw = player.prevHeadYaw;
        final float headYaw = player.headYaw;
        player.bodyYaw = yaw + rotationSide * 20.0F;
        player.setYaw(yaw + rotationSide * 40.0F);
        player.setPitch(-rotationUp * 20.0F);
        player.headYaw = player.getYaw();
        player.prevHeadYaw = player.getYaw();
        DiffuseLighting.method_34742();
        final var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        dispatcher.setRenderShadows(false);
        final var consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> dispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, sizeMatrices, consumers, 15728880));
        consumers.draw();
        dispatcher.setRenderShadows(true);
        player.bodyYaw = bodyYaw;
        player.setYaw(playerYaw);
        player.setPitch(playerPitch);
        player.prevHeadYaw = prevHeadYaw;
        player.headYaw = headYaw;
        viewMatrices.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }
}
