/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.chromium.shared.skins.provider;

import com.google.gson.JsonObject;
import com.mojang.authlib.properties.Property;
import io.sapphiremc.chromium.shared.skins.SkinVariant;
import io.sapphiremc.chromium.shared.util.skins.JsonUtils;
import io.sapphiremc.chromium.shared.util.skins.WebUtils;
import java.io.IOException;
import java.net.URL;

public class MineSkinSkinsProvider {
    private static final String API = "https://api.mineskin.org/generate/url";
    private static final String USER_AGENT = "Chromium_Skins";
    private static final String TYPE = "application/json";

    public static Property getSkin(String url, SkinVariant variant) {
        try {
            String input = ("{\"variant\":\"%s\",\"name\":\"%s\",\"visibility\":%d,\"url\":\"%s\"}")
                    .formatted(variant.toString(), "none", 1, url);

            JsonObject texture = JsonUtils.parseJson(WebUtils.POSTRequest(new URL(API), USER_AGENT, TYPE, TYPE, input))
                    .getAsJsonObject("data").getAsJsonObject("texture");

            return new Property("textures", texture.get("value").getAsString(), texture.get("signature").getAsString());
        } catch (IOException e) {
            return null;
        }
    }
}
