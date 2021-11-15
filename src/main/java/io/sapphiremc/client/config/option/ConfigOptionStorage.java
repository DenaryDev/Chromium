package io.sapphiremc.client.config.option;

import java.util.HashMap;
import java.util.Map;

public class ConfigOptionStorage {
    private static final Map<String, Boolean> BOOLEAN_OPTIONS = new HashMap<>();

    public static void setBoolean(String key, boolean value) {
        BOOLEAN_OPTIONS.put(key, value);
    }

    public static void toggleBoolean(String key) {
        setBoolean(key, !getBoolean(key));
    }

    public static boolean getBoolean(String key) {
        return BOOLEAN_OPTIONS.get(key);
    }
}
