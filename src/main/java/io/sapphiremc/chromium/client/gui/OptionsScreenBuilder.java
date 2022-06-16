/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.client.gui;

import io.sapphiremc.chromium.shared.config.ChromiumConfig;
import io.sapphiremc.chromium.ChromiumMod;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.IntegerListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public class OptionsScreenBuilder {

    private static final Function<Boolean, Text> yesNoSupplier = bool -> {
        if (bool) return Text.translatable("label.chromium.on");
        else return Text.translatable("label.chromium.off");
    };

    public static Screen build() {
        MinecraftClient client = MinecraftClient.getInstance();
        ChromiumConfig defaults = new ChromiumConfig();
        ChromiumConfig current = ChromiumMod.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(client.currentScreen)
                .setTitle(Text.translatable("title.chromium.config"))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> ChromiumMod.getConfigManager().writeConfig(true));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.translatable("category.chromium.general"));

        /*========================= Info panel settings =========================*/
        AbstractConfigListEntry<Boolean> toggleShowFps = entryBuilder.startBooleanToggle(Text.translatable("options.chromium.showFps"), current.isShowFps())
                .setDefaultValue(defaults.isShowFps())
                .setTooltip(getTooltip("options.chromium.showFps"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowFps(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        AbstractConfigListEntry<Boolean> toggleShowTime = entryBuilder.startBooleanToggle(Text.translatable("options.chromium.showTime"), current.isShowTime())
                .setDefaultValue(defaults.isShowTime())
                .setTooltip(getTooltip("options.chromium.showTime"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowTime(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        AbstractConfigListEntry<Boolean> toggleShowCoords = entryBuilder.startBooleanToggle(Text.translatable("options.chromium.showCoords"), current.isShowCoords())
                .setDefaultValue(defaults.isShowCoords())
                .setTooltip(getTooltip("options.chromium.showCoords"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowCoords(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        AbstractConfigListEntry<Boolean> toggleShowLight = entryBuilder.startBooleanToggle(Text.translatable("options.chromium.showLight"), current.isShowLight())
                .setDefaultValue(defaults.isShowLight())
                .setTooltip(getTooltip("options.chromium.showLight"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowLight(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        AbstractConfigListEntry<Boolean> toggleShowBiome = entryBuilder.startBooleanToggle(Text.translatable("options.chromium.showBiome"), current.isShowBiome())
                .setDefaultValue(defaults.isShowBiome())
                .setTooltip(getTooltip("options.chromium.showBiome"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowBiome(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        /*========================= Chat settings =========================*/
        AbstractConfigListEntry<Boolean> toggleMessagesTime = entryBuilder.startBooleanToggle(Text.translatable("options.chromium.showMessagesTime"), current.isShowMessagesTime())
                .setDefaultValue(defaults.isShowMessagesTime())
                .setTooltip(getTooltip("options.chromium.showMessagesTime"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setShowMessagesTime(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();
        IntegerListEntry maxMessages = entryBuilder.startIntField(Text.translatable("options.chromium.maxMessages"), current.getMaxMessages())
                .setDefaultValue(defaults.getMaxMessages())
                .setMin(50).setMax(500)
                .setTooltip(getTooltip("options.chromium.maxMessages"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setMaxMessages(value))
                .build();

        /*========================= Hopper settings =========================*/
        IntegerListEntry hopperTransfer = entryBuilder.startIntField(Text.translatable("options.chromium.world.hopperTransfer"), current.getHopperTransfer())
                .setDefaultValue(defaults.getHopperTransfer())
                .setMin(2).setMax(200)
                .setTooltip(getTooltip("options.chromium.world.hopperTransfer"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setHopperTransfer(value))
                .build();
        IntegerListEntry hopperAmount = entryBuilder.startIntField(Text.translatable("options.chromium.world.hopperAmount"), current.getHopperAmount())
                .setDefaultValue(defaults.getHopperAmount())
                .setMin(1).setMax(64)
                .setTooltip(getTooltip("options.chromium.world.hopperAmount"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setHopperAmount(value))
                .build();

        /*========================= Screen settings =========================*/
        AbstractConfigListEntry<ChromiumConfig.TitleScreenProvider> changeScreenType = entryBuilder.startEnumSelector(Text.translatable("options.chromium.changeScreenProvider"), ChromiumConfig.TitleScreenProvider.class, current.getTitleScreenProvider())
                .setDefaultValue(defaults.getTitleScreenProvider())
                .setTooltip(getTooltip("options.chromium.changeScreenProvider"))
                .setSaveConsumer(value -> ChromiumMod.getConfig().setTitleScreenProvider(value))
                .setEnumNameProvider(anEnum -> {
                    if (anEnum.equals(ChromiumConfig.TitleScreenProvider.CHROMIUM)) return Text.translatable("options.chromium.screenType.chromium");
                    else return Text.translatable("options.chromium.screenType.minecraft");
                })
                .build();

        SubCategoryBuilder info = entryBuilder.startSubCategory(Text.translatable("category.chromium.info"));
        info.add(toggleShowFps);
        info.add(toggleShowTime);
        info.add(toggleShowCoords);
        info.add(toggleShowLight);
        info.add(toggleShowBiome);

        SubCategoryBuilder chat = entryBuilder.startSubCategory(Text.translatable("category.chromium.chat"));
        chat.add(toggleMessagesTime);
        chat.add(maxMessages);

        SubCategoryBuilder world = entryBuilder.startSubCategory(Text.translatable("category.chromium.world"));

        SubCategoryBuilder hopper = entryBuilder.startSubCategory(Text.translatable("category.chromium.world.hopper"));
        hopper.add(hopperTransfer);
        hopper.add(hopperAmount);
        hopper.setExpanded(true);
        world.add(hopper.build());

        SubCategoryBuilder screen = entryBuilder.startSubCategory(Text.translatable("category.chromium.screen"));
        screen.add(changeScreenType);

        category.addEntry(info.build());
        category.addEntry(chat.build());
        category.addEntry(world.build());
        category.addEntry(screen.build());

        return builder.build();
    }

    private static Text[] getTooltip(String key) {
        List<Text> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String finalKey = key + ".tooltip." + (i + 1);
            String value = I18n.translate(finalKey);

            if (value.equals(finalKey)) break;

            list.add(Text.of(value));
        }

        return list.size() == 0 ? null : list.toArray(new Text[0]);
    }
}
