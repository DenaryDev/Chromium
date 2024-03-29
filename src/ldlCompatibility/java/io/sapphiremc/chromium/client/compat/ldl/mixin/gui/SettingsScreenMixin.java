/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.compat.ldl.mixin.gui;

import dev.lambdaurora.lambdynlights.gui.LightSourceListWidget;
import dev.lambdaurora.lambdynlights.gui.SettingsScreen;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import dev.lambdaurora.spruceui.widget.container.tabbed.SpruceTabbedWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SettingsScreen.class)
public abstract class SettingsScreenMixin extends SpruceScreen {

    @Shadow private SpruceTabbedWidget tabbedWidget;

    @Shadow protected abstract SpruceTabbedWidget.ContainerFactory tabContainerBuilder(SpruceTabbedWidget.ContainerFactory innerFactory);

    @Shadow protected abstract SpruceOptionListWidget buildGeneralTab(int width, int height);

    @Shadow @Final private SpruceOption entitiesOption;

    @Shadow protected abstract LightSourceListWidget buildEntitiesTab(int width, int height);

    @Shadow @Final private SpruceOption blockEntitiesOption;

    @Shadow protected abstract LightSourceListWidget buildBlockEntitiesTab(int width, int height);

    protected SettingsScreenMixin(Component title) {
        super(title);
    }

    /**
     * @author DenaryDev
     * @reason I don't want pride flags in my minecraft!!!
     */
    @Overwrite
    protected void init() {
        super.init();
        final var dynamicLightSources = Component.translatable("lambdynlights.menu.light_sources");
        this.tabbedWidget = new SpruceTabbedWidget(Position.origin(), this.width, this.height, null, Math.max(100, this.width / 8), 0);
        //this.tabbedWidget.getList().setBackground(RandomPrideFlagBackground.random());
        this.tabbedWidget.addTabEntry(Component.translatable("lambdynlights.menu.tabs.general"), null, this.tabContainerBuilder(this::buildGeneralTab));
        this.tabbedWidget.addSeparatorEntry(null);
        this.tabbedWidget
                .addTabEntry(
                        Component.empty().append(dynamicLightSources).append(": ").append(this.entitiesOption.getPrefix()),
                        null,
                        this.tabContainerBuilder(this::buildEntitiesTab)
                );
        this.tabbedWidget
                .addTabEntry(
                        Component.empty().append(dynamicLightSources).append(": ").append(this.blockEntitiesOption.getPrefix()),
                        null,
                        this.tabContainerBuilder(this::buildBlockEntitiesTab)
                );
        this.addRenderableWidget(this.tabbedWidget);
    }
}
