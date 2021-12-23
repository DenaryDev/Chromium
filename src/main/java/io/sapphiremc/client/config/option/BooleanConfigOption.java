/*
 * Copyright (c) 2021 DenaryDev
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
package io.sapphiremc.client.config.option;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BooleanConfigOption implements OptionConvertable {
    private final String key, translationKey;
    private final boolean defaultValue;
    private final Text enabledText;
    private final Text disabledText;

    public BooleanConfigOption(String key, boolean defaultValue, String enabledKey, String disabledKey) {
        ConfigOptionStorage.setBoolean(key, defaultValue);
        this.key = key;
        this.translationKey = "sapphireclient.option." + key;
        this.defaultValue = defaultValue;
        this.enabledText = new TranslatableText( "sapphireclient.option." + enabledKey);
        this.disabledText = new TranslatableText( "sapphireclient.option." + disabledKey);
    }

    public BooleanConfigOption(String key, boolean defaultValue) {
        this(key, defaultValue, "true", "false");
    }

    public String getKey() {
        return key;
    }

    public boolean getValue() {
        return ConfigOptionStorage.getBoolean(key);
    }

    public void setValue(boolean value) {
        ConfigOptionStorage.setBoolean(key, value);
    }

    public void toggleValue() {
        ConfigOptionStorage.toggleBoolean(key);
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public Text getButtonText() {
        return ScreenTexts.composeGenericOptionText(new TranslatableText(translationKey), getValue() ? enabledText : disabledText);
    }

    @Override
    public CyclingOption<Boolean> asOption() {
        if (enabledText != null && disabledText != null) {
            return CyclingOption.create(translationKey, enabledText, disabledText, ignored -> ConfigOptionStorage.getBoolean(key), (ignored, option, value) -> ConfigOptionStorage.setBoolean(key, value));
        }
        return CyclingOption.create(translationKey, ignored -> ConfigOptionStorage.getBoolean(key), (ignored, option, value) -> ConfigOptionStorage.setBoolean(key, value));
    }
}