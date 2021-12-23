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