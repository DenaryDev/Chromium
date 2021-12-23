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
package io.sapphiremc.client.config;

import io.sapphiremc.client.config.option.OptionConvertable;
import io.sapphiremc.client.config.option.BooleanConfigOption;
import net.minecraft.client.option.Option;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class SapphireClientConfig {

    public static final BooleanConfigOption CHAT_MESSAGES_TIME = new BooleanConfigOption("chat_messages_time", false);
    public static final BooleanConfigOption SHOW_FPS = new BooleanConfigOption("show_fps", false);
    public static final BooleanConfigOption SHOW_TIME = new BooleanConfigOption("show_time", false);
    public static final BooleanConfigOption SHOW_COORDS = new BooleanConfigOption("show_coords", false);
    public static final BooleanConfigOption SHOW_LIGHT = new BooleanConfigOption("show_light", false);
    public static final BooleanConfigOption SHOW_BIOME = new BooleanConfigOption("show_biome", false);

    public static Option[] asOptions() {
        ArrayList<Option> options = new ArrayList<>();
        for (Field field : SapphireClientConfig.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && OptionConvertable.class.isAssignableFrom(field.getType())) {
                try {
                    options.add(((OptionConvertable) field.get(null)).asOption());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return options.toArray(Option[]::new);
    }
}
