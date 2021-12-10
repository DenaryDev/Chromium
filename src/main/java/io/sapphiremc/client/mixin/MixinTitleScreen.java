package io.sapphiremc.client.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.gui.ModsScreen;
import io.sapphiremc.client.SapphireClientMod;
import io.sapphiremc.client.dummy.DummyClientPlayerEntity;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    @Shadow private long backgroundFadeStart;
    @Shadow @Final private boolean doBackgroundFade;

    @Shadow protected abstract boolean areRealmsNotificationsEnabled();

    @Shadow private Screen realmsNotificationGui;

    private static final Identifier BACKGROUND = new Identifier(SapphireClientMod.MOD_ID, "textures/ui/background.png");
    private static final Identifier LOGO = new Identifier(SapphireClientMod.MOD_ID, "textures/ui/logo.png");
    private static final Identifier GOLD = new Identifier(SapphireClientMod.MOD_ID, "textures/ui/gold.png");

    private boolean confirmOpened = false;
    private boolean widgetsAdded = false;

    private ButtonWidget quitButton;
    private ButtonWidget cancelButton;

    public MixinTitleScreen() {
        super(new TranslatableText("narrator.screen.title"));
    }

    /**
     * @author DenaryDev
     * @reason Fully custom main menu
     */
    @Overwrite
    public static CompletableFuture<Void> loadTexturesAsync(TextureManager textureManager, Executor executor) {
        return CompletableFuture.allOf(textureManager.loadTextureAsync(BACKGROUND, executor), textureManager.loadTextureAsync(LOGO, executor), textureManager.loadTextureAsync(GOLD, executor));
    }

    /**
     * @author DenaryDev
     * @reason Fully custom main menu
     */
    @Overwrite
    public boolean shouldCloseOnEsc() {
        this.confirmOpened = !this.confirmOpened;
        return false;
    }

    /**
     * @author DenaryDev
     * @reason Fully custom main menu
     */
    @Overwrite
    public void init() {
        SapphireClientMod.dummyClientPlayer.updateSkin();
        int buttonW = (this.width - 128) / 5;

        this.addDrawableChild(new ButtonWidget(48, 48, buttonW, 20, new TranslatableText("sapphireclient.menu.singleplayer"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new SelectWorldScreen(this));
        }));
        this.addDrawableChild(new ButtonWidget(48 + 8 + buttonW, 48, buttonW, 20, new TranslatableText("sapphireclient.menu.multiplayer"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new MultiplayerScreen(this));
        }));
        this.addDrawableChild(new ButtonWidget(48 + 16 + buttonW * 2, 48, buttonW, 20, new TranslatableText("sapphireclient.menu.options"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));
        this.addDrawableChild(new ButtonWidget(48 + 24 + buttonW * 3, 48, buttonW, 20, new TranslatableText("sapphireclient.menu.mods", FabricLoader.getInstance().getAllMods().size()), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new ModsScreen(this));
        }));
        this.addDrawableChild(new ButtonWidget(48 + 32 + buttonW * 4, 48, buttonW, 20, new TranslatableText("sapphireclient.menu.vkGroup"), (element) -> {
            this.confirmOpened = false;
            this.client.setScreen(new ConfirmChatLinkScreen((confirmOpened) -> {
                if (confirmOpened) {
                    Util.getOperatingSystem().open("https://vk.com/denaryworld");
                }
                this.client.setScreen(this);
            }, "https://vk.com/denaryworld", false));
        }));

        this.quitButton = new ButtonWidget(this.width / 2 - 80, this.height / 2 - 10, 160, 20, new TranslatableText("sapphireclient.menu.quit"), (element) ->
                this.client.stop());
        this.cancelButton = new ButtonWidget(this.width / 2 - 80, this.height / 2 + 18, 160, 20, new TranslatableText("sapphireclient.menu.cancel"), (element) ->
                this.confirmOpened = false);
    }

    /**
     * @author DenaryDev
     * @reason Fully custom main menu
     */
    @Overwrite
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = System.currentTimeMillis();
        }

        float f = this.doBackgroundFade ? (float) (System.currentTimeMillis() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        GlStateManager._disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrixStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
        float g = this.doBackgroundFade ? MathHelper.clamp(f - 1.0f, 0.0f, 1.0f) : 1.0f;
        int l = MathHelper.ceil(g * 255.0f) << 24;
        if ((l & 0xFC000000) == 0) {
            return;
        }

        if (!this.confirmOpened) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, LOGO);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);
            drawTexture(matrixStack, 16, this.height - 104, 0, 0, 120, 120, 120, 120);

            if (widgetsAdded) {
                this.remove(quitButton);
                this.remove(cancelButton);
                this.widgetsAdded = false;
            }

            ClientPlayerEntity player = DummyClientPlayerEntity.getInstance();
            int height = this.height + 50;
            int playerX = this.width - (int) (this.height / 3.2F);
            InventoryScreen.drawEntity(playerX, height, this.height / 3, -mouseX + playerX, -mouseY + height - (this.height / 1.9F), player);
        } else {
            if (!this.widgetsAdded) {
                this.addDrawableChild(quitButton);
                this.addDrawableChild(cancelButton);
                this.widgetsAdded = true;
            }

            String confirmQuit = new TranslatableText("sapphireclient.menu.confirmQuit").getString();
            int textLength = this.textRenderer.getWidth(confirmQuit);
            this.textRenderer.drawWithShadow(matrixStack, confirmQuit, this.width / 2.0F - textLength / 2.0F, this.height / 2.0F - 26.0F, -2039584);
        }

        for (Element element : this.children()) {
            if (!(element instanceof ClickableWidget)) continue;
            ((ClickableWidget)element).setAlpha(g);
        }

        super.render(matrixStack, mouseX, mouseY, delta);

        if (areRealmsNotificationsEnabled() && g >= 1.0f) {
            this.realmsNotificationGui.render(matrixStack, mouseX, mouseY, delta);
        }

        String userName = this.client.getSession().getUsername();
        String goldAmount = "2021";

        int nameLength = this.textRenderer.getWidth(userName);
        int amountLength = this.textRenderer.getWidth(goldAmount);

        this.textRenderer.drawWithShadow(matrixStack, userName, this.width - 52 - nameLength, 20, -2039584);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, GOLD);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrixStack, this.width - 76 - nameLength, 18, 0, 0, 11, 11, 11, 11);

        this.textRenderer.drawWithShadow(matrixStack, goldAmount, this.width - 78 - nameLength - amountLength, 20, 0xFFD700);
    }
}
