/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.gui.ModsScreen;
import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.client.dummy.DummyClientPlayerEntity;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import the_fireplace.ias.IAS;
import the_fireplace.ias.gui.AccountListScreen;

public class ChromiumTitleScreen extends Screen {

    private static final Identifier MORNING_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/morning.png");
    private static final Identifier DAY_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/day.png");
    private static final Identifier EVENING_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/evening.png");
    private static final Identifier NIGHT_BACKGROUND = new Identifier(ChromiumMod.MOD_ID, "textures/ui/background/night.png");

    private static final Identifier LOGO = new Identifier(ChromiumMod.MOD_ID, "textures/ui/logo.png");
    private static final Identifier GOLD = new Identifier(ChromiumMod.MOD_ID, "textures/ui/gold.png");

    @Getter @Setter private boolean confirmOpened = false;
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
        return CompletableFuture.allOf(textureManager.loadTextureAsync(MORNING_BACKGROUND, executor), textureManager.loadTextureAsync(DAY_BACKGROUND, executor), textureManager.loadTextureAsync(EVENING_BACKGROUND, executor), textureManager.loadTextureAsync(NIGHT_BACKGROUND, executor), textureManager.loadTextureAsync(LOGO, executor), textureManager.loadTextureAsync(GOLD, executor));
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
        DummyClientPlayerEntity.getInstance().updateSkin();
        int buttonW = (this.width) / 5;
        int x = (buttonW + 64) / 2 - buttonW / 2;
        int centerY = this.height / 2 + 32;
        int modifier = 0;

        final var hasModMenu = FabricLoader.getInstance().isModLoaded("modmenu");
        final var hasIas = FabricLoader.getInstance().isModLoaded("ias");
        if (hasModMenu && hasIas) {
            modifier = 28;
        } else if (hasModMenu) {
            modifier = 14;
        } else if (hasIas) {
            modifier = 14;
        }

        assert this.client != null;
        this.addDrawableChild(new ButtonWidget(x, centerY - 38 - modifier, buttonW, 20, Text.translatable("menu.singleplayer"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new SelectWorldScreen(this));
        }));
        this.addDrawableChild(new ButtonWidget(x, centerY - 10 - modifier, buttonW, 20, Text.translatable("menu.multiplayer"), (element) -> {
            this.confirmOpened = false;
            Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen(screen);
        }));
        if (hasModMenu) {
            this.addDrawableChild(new ButtonWidget(x, centerY + 4 - (modifier - 14), buttonW, 20, ModMenuApi.createModsButtonText(), (button) -> {
                this.confirmOpened = false;
                this.client.setScreen(new ModsScreen(this));
            }));
        }
        if (hasIas) {
            this.addDrawableChild(new ButtonWidget(x, centerY + 4 + (modifier - 14), buttonW, 20, Text.translatable("menu.chromium.accounts"), (button -> {
                this.confirmOpened = false;
                this.client.setScreen(new AccountListScreen(this));
            })));
        }
        this.addDrawableChild(new ButtonWidget(x, centerY + 18 + modifier, buttonW, 20, Text.translatable("menu.options"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));

        this.quitButton = new ButtonWidget(this.width / 2 - 50, this.height / 2 - 10, 100, 20, Text.translatable("menu.chromium.quit"), (element) ->
                this.client.stop());
        this.cancelButton = new ButtonWidget(this.width / 2 - 50, this.height / 2 + 18, 100, 20, Text.translatable("menu.chromium.cancel"), (element) ->
                this.confirmOpened = false);
        this.settingsButton = new ButtonWidget(this.width - 22, 2, 20, 20, Text.literal("S"), (element) ->
                this.client.setScreen(OptionsScreenBuilder.build()));
        this.realmsButton = new ButtonWidget(this.width - 44, 2, 20, 20, Text.literal("R"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new ChromiumTitleScreen());
        });
        this.addDrawableChild(settingsButton);
        this.addDrawableChild(realmsButton);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = System.currentTimeMillis();
        }

        assert this.client != null;
        float f = this.doBackgroundFade ? (float) (System.currentTimeMillis() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        GlStateManager._disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, getBackground());
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float) MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrixStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
        int l = MathHelper.ceil(g * 255.0f) << 24;
        if ((l & 0xFC000000) == 0) {
            return;
        }

        int newWidth = ((this.width) / 5) + 64;
        // fill(matrixStack, 0, 13, newWidth, height, -1873784752);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, LOGO);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);
        int logoW = 90 + this.height / 11;
        drawTexture(matrixStack, (newWidth / 2) - (logoW / 2), -5, 0, 0, logoW, logoW, logoW, logoW);

        ClientPlayerEntity player = DummyClientPlayerEntity.getInstance();
        int height = this.height + 50;
        int playerX = this.width - (int) (this.height / 3.4F);
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

            String confirmQuit = Text.translatable("menu.chromium.confirmQuit").getString();
            int textLength = this.textRenderer.getWidth(confirmQuit);
            this.textRenderer.drawWithShadow(matrixStack, confirmQuit, this.width / 2.0F - textLength / 2.0F, this.height / 2.0F - 26.0F, -2039584);
        }

        for (Element element : this.children()) {
            if (!(element instanceof ClickableWidget)) continue;
            ((ClickableWidget)element).setAlpha(g);
        }

        super.render(matrixStack, mouseX, mouseY, delta);

        String userName = this.client.getSession().getUsername();
       // String goldAmount = "2022";

        int centerX = newWidth / 2;
        int nameLength = this.textRenderer.getWidth(userName);
       // int amountLength = this.textRenderer.getWidth(goldAmount);

        this.textRenderer.drawWithShadow(matrixStack, userName, centerX - (nameLength / 2F), 96, -2039584);

       // RenderSystem.enableBlend();
       // RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
       // RenderSystem.setShaderTexture(0, GOLD);
       // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

       // int goldX = centerX - (amountLength + 14) / 2;
       // drawTexture(matrixStack, goldX, 110, 0, 0, 11, 11, 11, 11);
       // this.textRenderer.drawWithShadow(matrixStack, goldAmount, goldX + 14, 112, 0xFFD700);

        String modVersion = FabricLoader.getInstance().getModContainer(ChromiumMod.MOD_ID).get().getMetadata().getVersion().getFriendlyString().toLowerCase();
        boolean isUnstable = modVersion.contains("alpha") || modVersion.contains("beta") || modVersion.contains("pre") || modVersion.contains("rc") || modVersion.contains("snapshot");
        int settingsButtonY = settingsButton.y;
        int realmsButtonY = realmsButton.y;
        if (isUnstable) {
            if (settingsButtonY != 15) settingsButton.y = 15;
            if (realmsButtonY != 15) realmsButton.y = 15;
            fill(matrixStack, 0, 0, width, 13, -1873784752);
            String beta = Text.translatable("chromium.warnings.unstable").getString();
            this.textRenderer.drawWithShadow(matrixStack, beta, i, 3, 0xFF5555);

            i -= 1;
            if (i < (-textRenderer.getWidth(beta))) {
                i = width;
            }
        } else {
            if (settingsButtonY != 2) settingsButton.y = 2;
        }
    }

    private Identifier getBackground() {
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hours >= 6 && hours < 10) {
            return MORNING_BACKGROUND;
        }
        if (hours >= 10 && hours < 16) {
            return DAY_BACKGROUND;
        }
        if (hours >= 16 && hours < 20) {
            return EVENING_BACKGROUND;
        }
        return NIGHT_BACKGROUND;
    }

    private float yaw = 190.0F;

    private void drawEntity(int x, int y, int size, float mouseX, float mouseY, ClientPlayerEntity player) {
        assert this.client != null;
        float finalYaw = yaw;
        if (InputUtil.isKeyPressed(client.getWindow().getHandle(), 342)) {
            finalYaw = this.yaw++;
            if (finalYaw > 359.0F) {
                this.yaw = 0;
            }
        } else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), 346)) {
            finalYaw = this.yaw--;
            if (finalYaw < 1.0F) {
                this.yaw = 360;
            }
        } else if (InputUtil.isKeyPressed(client.getWindow().getHandle(), 345)) {
            this.yaw = 190.0F;
        }

        float f = (float)Math.atan(mouseX / 400.0F);
        float g = (float)Math.atan(mouseY / 400.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 1050.0D);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        matrixStack2.translate(0.0D, 0.0D, 1000.0D);
        matrixStack2.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(g * 20.0F);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack2.multiply(quaternion);
        float h = player.bodyYaw;
        float i = player.getYaw();
        float j = player.getPitch();
        float k = player.prevHeadYaw;
        float l = player.headYaw;
        player.bodyYaw = finalYaw + f * 20.0F;
        player.setYaw(yaw + f * 40.0F);
        player.setPitch(-g * 20.0F);
        player.headYaw = player.getYaw();
        player.prevHeadYaw = player.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixStack2, immediate, 15728880));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        player.bodyYaw = h;
        player.setYaw(i);
        player.setPitch(j);
        player.prevHeadYaw = k;
        player.headYaw = l;
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }
}
