/*
 * Copyright (c) 2022 DenaryDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package io.sapphiremc.chromium.client.gui;

import io.sapphiremc.chromium.client.config.ChromiumConfig;
import io.sapphiremc.chromium.client.ChromiumClientMod;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class OptionsScreenBuilder {

    private static final Function<Boolean, Text> yesNoSupplier = bool -> {
        if (bool) return new TranslatableText("label.sapphireclient.on");
        else return new TranslatableText("label.sapphireclient.off");
    };

    public static Screen build(ChromiumClientMod mod) {
        MinecraftClient client = MinecraftClient.getInstance();
        ChromiumConfig defaults = new ChromiumConfig();
        ChromiumConfig current = mod.getConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(client.currentScreen)
                .setTitle(new TranslatableText("title.sapphireclient.config"))
                .transparentBackground()
                .setDoesConfirmSave(true)
                .setSavingRunnable(() -> mod.getConfigManager().writeConfig(true));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(new TranslatableText("category.sapphireclient.general"));

        AbstractConfigListEntry<ChromiumConfig.TitleScreenProvider> changeScreenType = entryBuilder.startEnumSelector(new TranslatableText("options.sapphireclient.changeScreenProvider"), ChromiumConfig.TitleScreenProvider.class, current.getTitleScreenProvider())
                .setDefaultValue(defaults.getTitleScreenProvider())
                .setTooltip(getTooltip("options.sapphireclient.changeScreenProvider"))
                .setSaveConsumer(value -> mod.getConfig().setTitleScreenProvider(value))
                .setEnumNameProvider(anEnum -> {
                    if (anEnum.equals(ChromiumConfig.TitleScreenProvider.CHROMIUM)) return new TranslatableText("options.sapphireclient.screenType.sapphireClient");
                    else return new TranslatableText("options.sapphireclient.screenType.minecraft");
                })
                .build();

        AbstractConfigListEntry<Boolean> toggleShowFps = entryBuilder.startBooleanToggle(new TranslatableText("options.sapphireclient.showFps"), current.isShowFps())
                .setDefaultValue(defaults.isShowFps())
                .setTooltip(getTooltip("options.sapphireclient.showFps"))
                .setSaveConsumer(value -> mod.getConfig().setShowFps(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        AbstractConfigListEntry<Boolean> toggleShowTime = entryBuilder.startBooleanToggle(new TranslatableText("options.sapphireclient.showTime"), current.isShowTime())
                .setDefaultValue(defaults.isShowTime())
                .setTooltip(getTooltip("options.sapphireclient.showTime"))
                .setSaveConsumer(value -> mod.getConfig().setShowTime(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        AbstractConfigListEntry<Boolean> toggleShowCoords = entryBuilder.startBooleanToggle(new TranslatableText("options.sapphireclient.showCoords"), current.isShowCoords())
                .setDefaultValue(defaults.isShowCoords())
                .setTooltip(getTooltip("options.sapphireclient.showCoords"))
                .setSaveConsumer(value -> mod.getConfig().setShowCoords(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        AbstractConfigListEntry<Boolean> toggleShowLight = entryBuilder.startBooleanToggle(new TranslatableText("options.sapphireclient.showLight"), current.isShowLight())
                .setDefaultValue(defaults.isShowLight())
                .setTooltip(getTooltip("options.sapphireclient.showLight"))
                .setSaveConsumer(value -> mod.getConfig().setShowLight(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        AbstractConfigListEntry<Boolean> toggleShowBiome = entryBuilder.startBooleanToggle(new TranslatableText("options.sapphireclient.showBiome"), current.isShowBiome())
                .setDefaultValue(defaults.isShowBiome())
                .setTooltip(getTooltip("options.sapphireclient.showBiome"))
                .setSaveConsumer(value -> mod.getConfig().setShowBiome(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        AbstractConfigListEntry<Boolean> toggleMessagesTime = entryBuilder.startBooleanToggle(new TranslatableText("options.sapphireclient.showMessagesTime"), current.isShowMessagesTime())
                .setDefaultValue(defaults.isShowMessagesTime())
                .setTooltip(getTooltip("options.sapphireclient.showMessagesTime"))
                .setSaveConsumer(value -> mod.getConfig().setShowMessagesTime(value))
                .setYesNoTextSupplier(yesNoSupplier)
                .build();

        SubCategoryBuilder screen = entryBuilder.startSubCategory(new TranslatableText("category.sapphireclient.screen"));
        screen.add(changeScreenType);
        screen.setExpanded(true);

        SubCategoryBuilder info = entryBuilder.startSubCategory(new TranslatableText("category.sapphireclient.info"));
        info.add(toggleShowFps);
        info.add(toggleShowTime);
        info.add(toggleShowCoords);
        info.add(toggleShowLight);
        info.add(toggleShowBiome);
        info.setExpanded(true);

        SubCategoryBuilder chat = entryBuilder.startSubCategory(new TranslatableText("category.sapphireclient.chat"));
        chat.add(toggleMessagesTime);
        chat.setExpanded(true);

        category.addEntry(screen.build());
        category.addEntry(info.build());
        category.addEntry(chat.build());

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
