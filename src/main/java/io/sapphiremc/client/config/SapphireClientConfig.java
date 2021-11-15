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
