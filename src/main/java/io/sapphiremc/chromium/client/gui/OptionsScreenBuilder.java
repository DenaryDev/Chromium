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
import io.sapphiremc.chromium.util.ColorUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Optional;
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
        final var toggleShowFps = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showFps"), current.showFps)
                .setDefaultValue(defaults.showFps)
                .setTooltip(getTooltip("options.chromium.showFps"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().showFps = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowTime = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showTime"), current.showTime)
                .setDefaultValue(defaults.showTime)
                .setTooltip(getTooltip("options.chromium.showTime"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().showTime = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowCoords = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showCoords"), current.showCoords)
                .setDefaultValue(defaults.showCoords)
                .setTooltip(getTooltip("options.chromium.showCoords"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().showCoords = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowLight = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showLight"), current.showLight)
                .setDefaultValue(defaults.showLight)
                .setTooltip(getTooltip("options.chromium.showLight"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().showLight = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var toggleShowBiome = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showBiome"), current.showBiome)
                .setDefaultValue(defaults.showBiome)
                .setTooltip(getTooltip("options.chromium.showBiome"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().showBiome = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        /*========================= Chat settings =========================*/
        final var toggleMessagesTime = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showMessagesTime"), current.showTimestamp)
                .setDefaultValue(defaults.showTimestamp)
                .setTooltip(getTooltip("options.chromium.showMessagesTime"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().showTimestamp = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var maxMessages = entryBuilder.startIntField(Component.translatable("options.chromium.maxMessages"), current.messagesHistorySize)
                .setDefaultValue(defaults.messagesHistorySize)
                .setMin(50).setMax(32767)
                .setTooltip(getTooltip("options.chromium.maxMessages"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().messagesHistorySize = it)
                .build();
        final var toggleMessageAnimations = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.messageAnimations"), current.messageAnimations)
                .setDefaultValue(defaults.messageAnimations)
                .setTooltip(getTooltip("options.chromium.messageAnimations"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().messageAnimations = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        /*========================= Tablist settings =========================*/
        final var toggleShowPingAmount = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.showPingAmount"), current.showPingAmount)
                .setDefaultValue(defaults.showPingAmount)
                .setTooltip(getTooltip("options.chromium.showPingAmount"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().showPingAmount = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var renderPingBars = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.replacePingBars"), current.replacePingBars)
                .setDefaultValue(defaults.replacePingBars)
                .setTooltip(getTooltip("options.chromium.replacePingBars"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().replacePingBars = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var pingAmountAutoColor = entryBuilder.startBooleanToggle(Component.translatable("options.chromium.pingAmountAutoColor"), current.pingAmountAutoColor)
                .setDefaultValue(defaults.pingAmountAutoColor)
                .setTooltip(getTooltip("options.chromium.pingAmountAutoColor"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().pingAmountAutoColor = it)
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        final var pingAmountColor = entryBuilder.startColorField(Component.translatable("options.chromium.pingAmountColor"), ColorUtils.fromHex(current.pingAmountColor))
                .setDefaultValue(ColorUtils.fromHex(defaults.pingAmountColor))
                .setTooltip(getTooltip("options.chromium.pingAmountColor"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().pingAmountColor = ColorUtils.toHex(it))
                .build();
        final var pingAmountFormat = entryBuilder.startTextField(Component.translatable("options.chromium.pingAmountFormat"), current.pingAmountFormat)
                .setDefaultValue(defaults.pingAmountFormat)
                .setTooltip(getTooltip("options.chromium.pingAmountFormat"))
                .setErrorSupplier(it -> {
                    if (!it.contains("<num>")) return Optional.of(Component.translatable("options.chromium.pingAmountFormat.invalid"));
                    else return Optional.empty();
                })
                .setSaveConsumer(it -> ChromiumMod.getConfig().pingAmountFormat = it)
                .build();

        /*========================= Tile entities rendeding settings =========================*/
        final var bannerRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.bannerRenderDistance"), current.bannerRenderDistance)
                .setDefaultValue(defaults.bannerRenderDistance)
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.bannerRenderDistance"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().bannerRenderDistance = it)
                .build();

        final var chestRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.chestRenderDistance"), current.chestRenderDistance)
                .setDefaultValue(defaults.chestRenderDistance)
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.chestRenderDistance"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().chestRenderDistance = it)
                .build();

        final var shulkerBoxRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.shulkerBoxRenderDistance"), current.shulkerBoxRenderDistance)
                .setDefaultValue(defaults.shulkerBoxRenderDistance)
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.shulkerBoxRenderDistance"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().shulkerBoxRenderDistance = it)
                .build();
        final var signRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.signRenderDistance"), current.signRenderDistance)
                .setDefaultValue(defaults.signRenderDistance)
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.signRenderDistance"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().signRenderDistance = it)
                .build();
        final var skullRenderDistance = entryBuilder.startIntField(Component.translatable("options.chromium.render.te.skullRenderDistance"), current.skullRenderDistance)
                .setDefaultValue(defaults.skullRenderDistance)
                .setMin(16).setMax(1024)
                .setTooltip(getTooltip("options.chromium.render.te.skullRenderDistance"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().skullRenderDistance = it)
                .build();

        /*========================= Hopper settings =========================*/
        final var hopperTransfer = entryBuilder.startIntField(Component.translatable("options.chromium.mechanics.hopperTransfer"), current.hopperTransfer)
                .setDefaultValue(defaults.hopperTransfer)
                .setMin(2).setMax(200)
                .setTooltip(getTooltip("options.chromium.mechanics.hopperTransfer"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().hopperTransfer = it)
                .build();
        final var hopperAmount = entryBuilder.startIntField(Component.translatable("options.chromium.mechanics.hopperAmount"), current.hopperAmount)
                .setDefaultValue(defaults.hopperAmount)
                .setMin(1).setMax(64)
                .setTooltip(getTooltip("options.chromium.mechanics.hopperAmount"))
                .setSaveConsumer(it -> ChromiumMod.getConfig().hopperAmount = it)
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
        chatBuilder.add(toggleMessageAnimations);
        
        final var tablistBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.tablist"));
        tablistBuilder.add(toggleShowPingAmount);
        tablistBuilder.add(renderPingBars);
        tablistBuilder.add(pingAmountAutoColor);
        tablistBuilder.add(pingAmountColor);
        tablistBuilder.add(pingAmountFormat);

        final var renderBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.render"));

        final var teBuider = entryBuilder.startSubCategory(Component.translatable("category.chromium.render.te"));
        teBuider.add(bannerRenderDistance);
        teBuider.add(chestRenderDistance);
        teBuider.add(shulkerBoxRenderDistance);
        teBuider.add(signRenderDistance);
        teBuider.add(skullRenderDistance);
        renderBuilder.add(teBuider.build());

        final var mechanicsBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.mechanics"));

        final var hopperBuilder = entryBuilder.startSubCategory(Component.translatable("category.chromium.mechanics.hopper"));
        hopperBuilder.add(hopperTransfer);
        hopperBuilder.add(hopperAmount);
        hopperBuilder.setExpanded(true);
        mechanicsBuilder.add(hopperBuilder.build());

        category.addEntry(infoBuilder.build());
        category.addEntry(chatBuilder.build());
        category.addEntry(tablistBuilder.build());
        category.addEntry(renderBuilder.build());
        category.addEntry(mechanicsBuilder.build());

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

        return list.isEmpty() ? null : list.toArray(new Component[0]);
    }
}
