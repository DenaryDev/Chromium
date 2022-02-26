/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.common.util.skins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static JsonObject parseJson(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
