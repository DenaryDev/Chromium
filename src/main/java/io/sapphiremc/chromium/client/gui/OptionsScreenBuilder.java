/*
 * Copyright (c) 2023 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.gui;

import io.sapphiremc.chromium.ChromiumMod;
import io.sapphiremc.chromium.config.ChromiumConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.function.Function;

public class OptionsScreenBuilder {

    private static final Function<Boolean, Component> yesNoSupplier = bool -> {
        if (bool) return Component.translatable("label.chromium.on");
        else return Component.translatable("label.chromium.off");
    };

    public static Screen build() {
        final var minecraft = Minecraft.getInstance();
        final var defaults = new ChromiumConfig();
        final var current = ChromiumMod.getConfig();

        final var builder = ConfigBuilder.create()
                .setParentScreen(minecraft.screen)
                .setTitle(Component.translatable("title.chromium.config"))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> ChromiumMod.getConfigManager().writeConfig(true));

        final var entryBuilder = builder.entryBuilder();
        final var category = builder.getOrCreateCategory(Component.translatable("category.chromium.general"));

        /*========================= Info panel settings =========================*/
        final var toggleShowFps = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showFps"), current.isShowFps())
                .setDefaultValue(defaults.isShowFps())
                .setTooltip(getTooltip("options.chromium.showFps"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowFps(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowTime = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showTime"), current.isShowTime())
                .setDefaultValue(defaults.isShowTime())
                .setTooltip(getTooltip("options.chromium.showTime"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowTime(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowCoords = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showCoords"), current.isShowCoords())
                .setDefaultValue(defaults.isShowCoords())
                .setTooltip(getTooltip("options.chromium.showCoords"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowCoords(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowLight = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showLight"), current.isShowLight())
                .setDefaultValue(defaults.isShowLight())
                .setTooltip(getTooltip("options.chromium.showLight"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowLight(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowBiome = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showBiome"), current.isShowBiome())
                .setDefaultValue(defaults.isShowBiome())
                .setTooltip(getTooltip("options.chromium.showBiome"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowBiome(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        /*========================= Chat settings =========================*/
        final var toggleMessagesTime = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showMessagesTime"), current.isShowMessagesTime())
                .setDefaultValue(defaults.isShowMessagesTime())
                .setTooltip(getTooltip("options.chromium.showMessagesTime"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowMessagesTime(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var maxMessages = entryBuilder.startIntField(Component.translatable("options.chromium.maxMessages"), current.getMaxMessages())
                .setDefaultValue(defaults.getMaxMessages())
                .setMin(50).setMax(32767)
                .setTooltip(getTooltip("options.chromium.maxMessages"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setMaxMessages(value))
                .build();

        /*========================= Tile entities view distance settings =========================*/
        final var bannerRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.bannerRenderDistance"),
                        current.getBannerRenderDistance())
                .setDefaultValue(defaults.getBannerRenderDistance())
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.bannerRenderDistance"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setBannerRenderDistance(value))
                .build();

        final var chestRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.chestRenderDistance"),
                        current.getChestRenderDistance())
                .setDefaultValue(defaults.getChestRenderDistance())
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.chestRenderDistance"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setChestRenderDistance(value))
                .build();

        final var shulkerBoxRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.shulkerBoxRenderDistance"),
                        current.getShulkerBoxRenderDistance())
                .setDefaultValue(defaults.getShulkerBoxRenderDistance())
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.shulkerBoxRenderDistance"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShulkerBoxRenderDistance(value))
                .build();
        final var signRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.signRenderDistance"),
                        current.getSignRenderDistance())
                .setDefaultValue(defaults.getSignRenderDistance())
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.signRenderDistance"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setSignRenderDistance(value))
                .build();
        final var skullRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.skullRenderDistance"),
                        current.getSkullRenderDistance())
                .setDefaultValue(defaults.getSkullRenderDistance())
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.skullRenderDistance"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setSkullRenderDistance(value))
                .build();

        /*========================= Hopper settings =========================*/
        final var hopperTransfer = entryBuilder.startIntField(Component.translatable("options.chromium.world.hopperTransfer"), current.getHopperTransfer())
                .setDefaultValue(defaults.getHopperTransfer())
                .setMin(2).setMax(200)
                .setTooltip(getTooltip("options.chromium.world.hopperTransfer"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setHopperTransfer(value))
                .build();
        final var hopperAmount = entryBuilder.startIntField(Component.translatable("options.chromium.world.hopperAmount"), current.getHopperAmount())
                .setDefaultValue(defaults.getHopperAmount())
                .setMin(1).setMax(64)
                .setTooltip(getTooltip("options.chromium.world.hopperAmount"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setHopperAmount(value))
                .build();

        /*========================= Screen settings =========================*/
        final var changeScreenType = entryBuilder.startEnumSelector(Component.translatable("options.chromium.changeScreenProvider"), ChromiumConfig.TitleScreenProvider.class, current.getTitleScreenProvider())
                .setDefaultValue(defaults.getTitleScreenProvider())
                .setTooltip(getTooltip("options.chromium.changeScreenProvider"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setTitleScreenProvider(value))
                .setEnumNameProvider(anEnum -> {
                    if (anEnum.equals(ChromiumConfig.TitleScreenProvider.CHROMIUM))
                        return Component.translatable("options.chromium.screenType.chromium");
                    else return Component.translatable("options.chromium.screenType.minecraft");
                })
                .build();

        final var infoBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.info"));
        infoBuilder.add(toggleShowFps);
        infoBuilder.add(toggleShowTime);
        infoBuilder.add(toggleShowCoords);
        infoBuilder.add(toggleShowLight);
        infoBuilder.add(toggleShowBiome);

        final var chatBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.chat"));
        chatBuilder.add(toggleMessagesTime);
        chatBuilder.add(maxMessages);

        final var renderBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.render"));

        final var teBuider = entryBuilder.startSubCategory(Component.translatable("category.chromium.render.te"));
        teBuider.add(bannerRenderDistance);
        teBuider.add(chestRenderDistance);
        teBuider.add(shulkerBoxRenderDistance);
        teBuider.add(signRenderDistance);
        teBuider.add(skullRenderDistance);
        renderBuilder.add(teBuider.build());

        final var worldBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.world"));

        final var hopperBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.world.hopper"));
        hopperBuilder.add(hopperTransfer);
        hopperBuilder.add(hopperAmount);
        hopperBuilder.setExpanded(true);
        worldBuilder.add(hopperBuilder.build());

        final var screenBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.screen"));
        screenBuilder.add(changeScreenType);

        category.addEntry(infoBuilder.build());
        category.addEntry(chatBuilder.build());
        category.addEntry(renderBuilder.build());
        category.addEntry(worldBuilder.build());
        category.addEntry(screenBuilder.build());

        return builder.build();
    }

    private static Component[] getTooltip(String key) {
        final var list = new ArrayList<Component>();

        for (int i = 0; i < 10; i++) {
            final var finalKey = key + ".tooltip." + (i + 1);
            final var value = I18n.get(finalKey);

            if (value.equals(finalKey)) break;

            list.add(Component.literal(value));
        }

        return list.size() == 0 ? null : list.toArray(new Component[0]);
    }
}
