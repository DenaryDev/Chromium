package io.sapphiremc.client.gui;

import com.terraformersmc.modmenu.config.ModMenuConfigManager;
import io.sapphiremc.client.config.SapphireClientConfig;
import io.sapphiremc.client.config.SapphireClientConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class SapphireClientOptionsScreen extends GameOptionsScreen {

    private final Screen previous;
    private ButtonListWidget buttonList;

    public SapphireClientOptionsScreen(Screen previous) {
        super(previous, MinecraftClient.getInstance().options, new TranslatableText("sapphireclient.menu.options_title"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        this.buttonList = new ButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
        this.buttonList.addAll(SapphireClientConfig.asOptions());
        this.addSelectableChild(this.buttonList);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, (element) -> {
            SapphireClientConfigManager.save();
            this.client.setScreen(this.previous);
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.buttonList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 5, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        List<OrderedText> tooltip = getHoveredButtonTooltip(this.buttonList, mouseX, mouseY);
        if (tooltip != null) {
            this.renderOrderedTooltip(matrices, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void removed() {
        ModMenuConfigManager.save();
    }
}
